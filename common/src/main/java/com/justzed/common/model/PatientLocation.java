package com.justzed.common.model;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.FenceUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import rx.Observable;

/**
 * Patient Location object + data access layer.
 * Database contains list of all saved patient location, data can be sorted by CreatedAt timestamp,
 * the latest entry contains the patient's latest known location.
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-08-23
 */
public class PatientLocation {


    private static final String KEY_PERSONLOCATION = "PatientLocation";
    private static final String KEY_PATIENT = "patient";
    private static final String KEY_LATLNG = "latLng";

    private Person patient;
    private LatLng latLng;
    private String objectId = null;
    private ParseObject parseObject = null;

    public PatientLocation(Person patient, LatLng latLng) {
        this.patient = patient;
        this.latLng = latLng;
    }

    private PatientLocation(ParseObject parseObject, Person patient, LatLng latLng) {
        this.objectId = parseObject.getObjectId();
        this.parseObject = parseObject;
        this.patient = patient;
        this.latLng = latLng;
    }

    protected static PatientLocation deserialize(ParseObject parseObject) throws ParseException {
        return new PatientLocation(parseObject,
                Person.deserialize(parseObject.fetchIfNeeded().getParseObject(KEY_PATIENT)),
                FenceUtils.toLatLng(parseObject.fetchIfNeeded().getParseGeoPoint(KEY_LATLNG)));
    }

    /**
     * This method gets the latest location of patient.
     *
     * @param patient patient as Person
     * @return PatientLocation Observable
     */

    public static Observable<PatientLocation> findLatestPatientLocation(Person patient) {
        return Observable.create(subscriber -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSONLOCATION);
            query.whereEqualTo(KEY_PATIENT, patient.getParseObject());
            query.orderByDescending("createdAt").setLimit(1);
            query.findInBackground((list, e) -> {
                try {
                    if (e == null && list.size() >= 1) {
                        if (list.get(0) != null) {
                            subscriber.onNext(deserialize(list.get(0)));
                        }
                        subscriber.onCompleted();
                    } else if (list.size() == 0) {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(e);
                    }
                } catch (ParseException pe) {
                    subscriber.onError(pe);
                }
            });
        });
    }

    public Person getPatient() {
        return patient;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getObjectId() {
        return objectId;
    }

    public ParseObject getParseObject() {
        if (parseObject != null) {
            return parseObject;
        } else if (objectId != null) {
            return ParseObject.createWithoutData(KEY_PERSONLOCATION, objectId);
        } else {
            return null;
        }
    }

    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PERSONLOCATION));
    }

    private ParseObject serialize(ParseObject parseObject) {
        parseObject.put(KEY_PATIENT, patient.getParseObject());
        parseObject.put(KEY_LATLNG, FenceUtils.toParseGeoPoint(latLng));
        return parseObject;
    }

    /**
     * This method saves this to database.
     *
     * @return PatientLocation Observable
     */
    public Observable<PatientLocation> save() {
        return Observable.create(subscriber -> {
            ParseObject parseObject = this.serialize();
            parseObject.saveInBackground(e -> {
                if (e == null) {
                    objectId = parseObject.getObjectId();
                    this.parseObject = parseObject;
                    subscriber.onNext(this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(e);
                }
            });

        });
    }

    /**
     * This method deletes from database.
     *
     * @return PatientLocation Observable (null for success)
     */
    public Observable<PatientLocation> delete() {
        return Observable.create(subscriber -> {
            if (objectId == null) {
                // this should never happen in the app
                subscriber.onError(new Exception("incorrect usage"));
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSONLOCATION);
            query.getInBackground(objectId, (parseObject, e) -> {
                if (e == null) {
                    parseObject.deleteInBackground(e1 -> {
                        if (e1 == null) {
                            objectId = null;
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(e1);
                        }
                    });
                } else {
                    subscriber.onError(e);
                }
            });
        });
    }

}
