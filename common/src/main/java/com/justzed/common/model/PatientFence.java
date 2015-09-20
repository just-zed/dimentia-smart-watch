package com.justzed.common.model;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.LocationHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;


/**
 * PatientFence object + data layer.
 * <p>
 * Geo fences are saved in circles only.
 * Each database row contains patient, center point and radius.
 *
 * @author Freeman
 * @version 1.0
 * @since 2015-08-23
 */
public class PatientFence {
    //Constants
    private static final String KEY_PERSONFENCE = "PatientFence";
    private static final String KEY_PATIENT = "patient";
    private static final String KEY_CENTER = "center";
    private static final String KEY_RADIUS = "radius";
    private static final String KEY_DESCRIPTION = "description";

    //Variables
    private Person patient;

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private LatLng center;
    private double radius;
    private String description;

    private String objectId = null;
    private ParseObject parseObject = null;

    public Person getPatient() {
        return patient;
    }

    public LatLng getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public String getDescription() {
        return description;
    }

    public String getObjectId() {
        return objectId;
    }

    public ParseObject getParseObject() {
        if (parseObject != null) {
            return parseObject;
        } else if (objectId != null) {
            return ParseObject.createWithoutData(KEY_PERSONFENCE, objectId);
        } else {
            return null;
        }
    }

    public PatientFence(Person patient, LatLng center, double radius) {
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = null;
    }

    public PatientFence(Person patient, LatLng center, double radius, String description) {
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = description;
    }

    public PatientFence(ParseObject parseObject, Person patient, LatLng center, double radius, String description) {
        this.objectId = parseObject.getObjectId();
        this.parseObject = parseObject;
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = description;
    }

    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PERSONFENCE));
    }

    private ParseObject serialize(ParseObject parseObject) {
        parseObject.put(KEY_PATIENT, patient.getParseObject());
        parseObject.put(KEY_CENTER, LocationHelper.toParseGeoPoint(center));
        parseObject.put(KEY_RADIUS, radius);
        if (description != null) {
            parseObject.put(KEY_DESCRIPTION, description);
        }
        return parseObject;
    }

    public static PatientFence deserialize(ParseObject parseObject) throws ParseException {
        return new PatientFence(parseObject,
                Person.deserialize(parseObject.fetchIfNeeded().getParseObject(KEY_PATIENT)),
                LocationHelper.toLatLng(parseObject.fetchIfNeeded().getParseGeoPoint(KEY_CENTER)),
                parseObject.getDouble(KEY_RADIUS),
                parseObject.getString(KEY_DESCRIPTION));

    }

    /**
     * This method saves to the PatientFence.
     *
     * @return PatientFence Observable
     */
    public Observable<PatientFence> save() {
        return Observable.create(subscriber -> {
            ParseObject parseObject;
            if (objectId == null) {
                parseObject = this.serialize();
            } else {
                parseObject = this.serialize(this.parseObject);
            }
            parseObject.saveInBackground(e -> {
                if (e == null) {
                    objectId = parseObject.getObjectId();
                    subscriber.onNext(this);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(e);
                }
            });
        });
    }

    /**
     * This method deletes PatientFence.
     *
     * @return PatientFence Observable
     */
    public Observable<PatientFence> delete() {
        return Observable.create(subscriber -> {
            if (objectId == null) {
                // this should never happen in the app
                subscriber.onError(new Exception("incorrect usage"));
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSONFENCE);
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

    /**
     * This method gets the whole list of geo fences by patient.
     *
     * @param patient a Person Object
     * @return List of PatientFence Observable
     */
    public static Observable<List<PatientFence>> getPatientFences(Person patient) {
        return Observable.create(subscriber -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSONFENCE);
            query.whereEqualTo(KEY_PATIENT, patient.getParseObject());
            query.findInBackground((list, e) -> {
                try {
                    if (e == null && list != null && list.size() >= 1) {
                        List<PatientFence> patientFences = new ArrayList<>();
                        for (int i = 0; i < list.size(); i++) {
                            patientFences.add(deserialize(list.get(i)));
                        }
                        subscriber.onNext(patientFences);
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

}
