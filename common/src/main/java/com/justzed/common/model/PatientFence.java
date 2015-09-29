package com.justzed.common.model;

import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.justzed.common.LocationHelper;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";

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

    private Calendar startTime;
    private Calendar endTime;

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
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
        this.startTime = null;
        this.endTime = null;
    }

    public PatientFence(Person patient, LatLng center, double radius, String description) {
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = description;
        this.startTime = null;
        this.endTime = null;
    }

    private PatientFence(ParseObject parseObject,
                         Person patient,
                         LatLng center,
                         double radius,
                         String description,
                         Calendar startTime,
                         Calendar endTime) {
        this.objectId = parseObject.getObjectId();
        this.parseObject = parseObject;
        this.patient = patient;
        this.center = center;
        this.radius = radius;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
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
        if (startTime != null && endTime != null) {
            parseObject.put(KEY_START_TIME, calendarToTimeString(startTime));
            parseObject.put(KEY_END_TIME, calendarToTimeString(endTime));
        }
        return parseObject;
    }

    public static PatientFence deserialize(ParseObject parseObject) throws ParseException {
        return new PatientFence(parseObject,
                Person.deserialize(parseObject.fetchIfNeeded().getParseObject(KEY_PATIENT)),
                LocationHelper.toLatLng(parseObject.fetchIfNeeded().getParseGeoPoint(KEY_CENTER)),
                parseObject.getDouble(KEY_RADIUS),
                parseObject.getString(KEY_DESCRIPTION),
                timeStringToCalendar(parseObject.getString(KEY_START_TIME)),
                timeStringToCalendar(parseObject.getString(KEY_END_TIME)));
    }

    private static final String TIME_FORMATTER = "%tR";

    /**
     * @param cal Calendar object of certain hour and minute of any day
     * @return time string in HH:MM format
     */
    public static String calendarToTimeString(Calendar cal) {
        return String.format(TIME_FORMATTER, cal);
    }

    /**
     * @param timeString time string in HH:MM format
     * @return Calendar object of that hour and minute of today
     */
    public static Calendar timeStringToCalendar(String timeString) {
        if (!TextUtils.isEmpty(timeString)) {
            Calendar now = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            DateFormat df = new SimpleDateFormat("HH:MM", Locale.ENGLISH);
            try {
                // parse time string
                cal.setTime(df.parse(timeString));
                // set date to today's date
                cal.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR));
                cal.set(Calendar.YEAR, now.get(Calendar.YEAR));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            return cal;
        }
        return null;
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
