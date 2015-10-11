package com.justzed.common.model;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.FenceUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;


/**
 * PatientFence object + data layer.
 * <p>
 * Geo fences are saved in circles only.
 * Each database row contains patient, center point and radius and description
 * startTime, endTime - are for timer based fences
 * groupId - is for "tunnels"
 *
 * @author Freeman Man
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
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_GROUP_ID = "groupId";


    //Variables
    private Person patient;
    private LatLng center;
    private double radius;
    private String description;
    private String objectId = null;
    private ParseObject parseObject = null;
    private long groupId = 0;
    private Calendar startTime;
    private Calendar endTime;

    public PatientFence(Person patient, LatLng center, double radius) {
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = null;
        this.startTime = null;
        this.endTime = null;
        this.groupId = 0;
    }

    public PatientFence(Person patient, LatLng center, double radius, String description) {
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = description;
        this.startTime = null;
        this.endTime = null;
        this.groupId = 0;
    }

    private PatientFence(ParseObject parseObject,
                         Person patient,
                         LatLng center,
                         double radius,
                         String description,
                         Calendar startTime,
                         Calendar endTime,
                         long groupId) {
        this.objectId = parseObject.getObjectId();
        this.parseObject = parseObject;
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.groupId = groupId;

    }

    protected static PatientFence deserialize(ParseObject parseObject) throws ParseException {
        return new PatientFence(parseObject,
                Person.deserialize(parseObject.fetchIfNeeded().getParseObject(KEY_PATIENT)),
                FenceUtils.toLatLng(parseObject.fetchIfNeeded().getParseGeoPoint(KEY_CENTER)),
                parseObject.getDouble(KEY_RADIUS),
                parseObject.getString(KEY_DESCRIPTION),
                FenceUtils.dateToCalendar(parseObject.getDate(KEY_START_TIME)),
                FenceUtils.dateToCalendar(parseObject.getDate(KEY_END_TIME)),
                parseObject.getLong(KEY_GROUP_ID)
        );
    }

    /**
     * This method gets the whole list of geo fences by patient.
     *
     * @param patient a Person Object
     * @return List of PatientFence Observable
     */
    public static Observable<List<PatientFence>> findPatientFences(Person patient) {
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

    public static Observable<Long> findMaxGroupId(Person patient) {
        return Observable.create(subscriber -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSONFENCE);
            query.whereEqualTo(KEY_PATIENT, patient.getParseObject());
            query.addDescendingOrder(KEY_GROUP_ID);
            query.setLimit(1);
            query.findInBackground((list, e) -> {
                if (e == null && list != null && list.size() >= 1) {
                    subscriber.onNext(list.get(0).getLong(KEY_GROUP_ID));
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(e);
                }
            });
        });
    }

    public Person getPatient() {
        return patient;
    }

    public LatLng getCenter() {
        return center;
    }

    public void setCenter(LatLng center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObjectId() {
        return objectId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
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

    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PERSONFENCE));
    }

    private ParseObject serialize(ParseObject parseObject) {
        parseObject.put(KEY_PATIENT, patient.getParseObject());
        parseObject.put(KEY_CENTER, FenceUtils.toParseGeoPoint(center));
        parseObject.put(KEY_RADIUS, radius);
        if (description != null) {
            parseObject.put(KEY_DESCRIPTION, description);
        }
        if (startTime != null && endTime != null) {
            parseObject.put(KEY_START_TIME, startTime.getTime());
            parseObject.put(KEY_END_TIME, endTime.getTime());
        }
        parseObject.put(KEY_GROUP_ID, groupId);
        return parseObject;
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

}
