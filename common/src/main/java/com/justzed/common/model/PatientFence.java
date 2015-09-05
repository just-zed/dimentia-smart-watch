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
 * Created by freeman on 8/23/15.
 */
public class PatientFence {


    private static final String KEY_PERSONFENCE = "PatientFence";
    private static final String KEY_PATIENT = "patient";
    private static final String KEY_CENTER = "center";
    private static final String KEY_RADIUS = "radius";

    private Person patient;
    private LatLng center;
    private double radius;

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
    }

    public PatientFence(ParseObject parseObject, Person patient, LatLng center, double radius) {
        this.objectId = parseObject.getObjectId();
        this.parseObject = parseObject;
        this.patient = patient;
        this.center = center;
        this.radius = radius;
    }

    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PERSONFENCE));
    }

    private ParseObject serialize(ParseObject parseObject) {
        parseObject.put(KEY_PATIENT, patient.getParseObject());
        parseObject.put(KEY_CENTER, LocationHelper.toParseGeoPoint(center));
        parseObject.put(KEY_RADIUS, radius);
        return parseObject;
    }

    public static PatientFence deserialize(ParseObject parseObject) throws ParseException {
        return new PatientFence(parseObject,
                Person.deserialize(parseObject.fetchIfNeeded().getParseObject(KEY_PATIENT)),
                LocationHelper.toLatLng(parseObject.fetchIfNeeded().getParseGeoPoint(KEY_CENTER)),
                parseObject.getDouble(KEY_RADIUS));

    }


    public Observable<PatientFence> save() {
        return Observable.create(subscriber -> {
            ParseObject parseObject = this.serialize();
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
