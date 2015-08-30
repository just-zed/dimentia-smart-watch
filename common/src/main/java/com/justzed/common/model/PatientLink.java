package com.justzed.common.model;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import rx.Observable;

/**
 * Created by freeman on 8/16/15.
 */
public class PatientLink {

    private static final String TAG = "Person";


    private static final String KEY_PATIENT_LINK = "PatientLink";
    private static final String KEY_PATIENT_UNIQUE_TOKEN = "patientUniqueToken";
    private static final String KEY_CARETAKER_UNIQUE_TOKEN = "caretakerUniqueToken";
    private static final String KEY_PATIENT = "patient";
    private static final String KEY_CARETAKER = "caretaker";


    //this can only be set by internal operation
    private String objectId = null;

    public ParseObject getParseObject() {
        if (parseObject != null) {
            return parseObject;
        } else if (objectId != null) {
            return ParseObject.createWithoutData(KEY_PATIENT_LINK, objectId);
        } else {
            return null;
        }
    }

    private ParseObject parseObject;


    public Person getPatient() {
        return patient;
    }

    public Person getCaretaker() {
        return caretaker;
    }

    private Person patient;
    private Person caretaker;

    public String getObjectId() {
        return objectId;
    }

    public PatientLink(Person patient, Person caretaker) {
        this.patient = patient;
        this.caretaker = caretaker;
    }

    public PatientLink(ParseObject parseObject, Person patient, Person caretaker) {
        this.parseObject = parseObject;
        this.objectId = parseObject.getObjectId();
        this.patient = patient;
        this.caretaker = caretaker;

        //TODO: put some check to check that patient is patient, caretaker is caretaker
//        if (patient.getType()!= Person.PATIENT || caretaker.getType()!= Person.CARETAKER){
//            throw new Exception("");
//        }
    }

    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PATIENT_LINK));
    }

    private ParseObject serialize(ParseObject link) {
        link.put(KEY_PATIENT, patient.getParseObject());
        link.put(KEY_CARETAKER, caretaker.getParseObject());
        link.put(KEY_PATIENT_UNIQUE_TOKEN, patient.getUniqueToken());
        link.put(KEY_CARETAKER_UNIQUE_TOKEN, caretaker.getUniqueToken());
        return link;
    }


    public static PatientLink deserialize(ParseObject parseObject) throws ParseException {
        return new PatientLink(
                parseObject,
                Person.deserialize(parseObject.getParseObject(KEY_PATIENT).fetchIfNeeded()),
                Person.deserialize(parseObject.getParseObject(KEY_CARETAKER).fetchIfNeeded())
        );
    }

    /**
     * this method automatically checks for duplicate and save the personlink object to database
     *
     * @return Observable<PatientLink>
     */
    public Observable<PatientLink> save() {
        return Observable.defer(() -> Observable.create(subscriber -> {
            PatientLink link = null;
            link = getByPersons(patient, caretaker).toBlocking().first();

            if (link == null) {
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
            } else {
                Log.e(TAG, "patientLink exists");
                subscriber.onNext(link);
                subscriber.onCompleted();
            }

        }));
    }

    /**
     * @param patient   patient
     * @param caretaker caretaker
     * @return Observable<PatientLink>
     */
    public static Observable<PatientLink> getByPersons(Person patient, Person caretaker) {

        return Observable.create(subscriber -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PATIENT_LINK);
            query.whereEqualTo(KEY_PATIENT, patient.getParseObject());
            query.whereEqualTo(KEY_CARETAKER, caretaker.getParseObject());
            query.setLimit(1);
            query.findInBackground((list, e) -> {
                try {
                    if (e == null && list.size() == 1) {
                        subscriber.onNext(deserialize(list.get(0)));
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

    /**
     * can be multiple
     *
     * @param patient patient
     * @return Observable<PatientLink>
     */
    public static Observable<PatientLink> getByPatient(Person patient) {

        //TODO: handle multiple patient links of the same patient
        return Observable.create(subscriber -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PATIENT_LINK);
            query.whereEqualTo(KEY_PATIENT, patient.getParseObject());
            query.setLimit(1);
            query.findInBackground((list, e) -> {
                try {
                    if (e == null && list.size() == 1) {
                        subscriber.onNext(deserialize(list.get(0)));
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

    /**
     * can be multiple
     *
     * @param caretaker caretaker
     * @return Observable<PatientLink>
     */
    public static Observable<PatientLink> getByCaretaker(Person caretaker) {

        return null;

    }

    /**
     * @return Observable<PatientLink>
     */
    public Observable<PatientLink> delete() {
        return Observable.create(subscriber -> {
            if (objectId == null) {
                // this should never happen in the app
                subscriber.onError(new Exception("incorrect usage"));
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PATIENT_LINK);
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
