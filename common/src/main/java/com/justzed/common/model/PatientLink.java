package com.justzed.common.model;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * PatientLink object + data access layer.
 * <p>
 * Database contains connection between 2 Persons, 1 as patient, 1 as caretaker.
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-08-15
 */
public class PatientLink {

    private static final String TAG = "Person";


    private static final String KEY_PATIENT_LINK = "PatientLink";
    private static final String KEY_PATIENT = "patient";
    private static final String KEY_CARETAKER = "caretaker";


    //this can only be set by internal operation
    private String objectId = null;
    private ParseObject parseObject;
    private Person patient;
    private Person caretaker;

    public PatientLink(Person patient, Person caretaker) {
        this.patient = patient;
        this.caretaker = caretaker;
    }

    private PatientLink(ParseObject parseObject, Person patient, Person caretaker) {
        this.parseObject = parseObject;
        this.objectId = parseObject.getObjectId();
        this.patient = patient;
        this.caretaker = caretaker;

        //TODO: put some check to check that patient is patient, caretaker is caretaker
//        if (patient.getType()!= Person.PATIENT || caretaker.getType()!= Person.CARETAKER){
//            throw new Exception("");
//        }
    }

    protected static PatientLink deserialize(ParseObject parseObject) throws ParseException {
        return new PatientLink(
                parseObject,
                Person.deserialize(parseObject.getParseObject(KEY_PATIENT).fetchIfNeeded()),
                Person.deserialize(parseObject.getParseObject(KEY_CARETAKER).fetchIfNeeded())
        );
    }

    /**
     * This method gets PatientLink by inputing both patient and caretaker, only used for checking duplicates.
     *
     * @param patient   a Person Object.
     * @param caretaker an other Person Object.
     * @return PatientLink Observable  that contains a link between the patient and the caretaker.
     */
    public static Observable<PatientLink> findByPersons(Person patient, Person caretaker) {

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
     * get the latest PatientLink by patient,
     * the only use case right now is to find if there is any caretaker exist for particular patient
     *
     * @param patient the patient's Person object
     * @return PatientLink Observable that contains a link between the patient and the caretaker.
     */
    public static Observable<PatientLink> findLatestByPatient(Person patient) {
        return findByPerson(patient, Person.PATIENT, 1)
                .map(patientLinks -> {
                    if (patientLinks == null) {
                        return null;
                    } else {
                        return patientLinks.get(0);
                    }
                });
    }

    /**
     * get a list of PatientLinks by patient
     * it is unused at the moment
     *
     * @param patient the patient's Person object
     * @return List&lt;PatientLink&gt; Observable that contains list of links between the patient and all associated caretakers.
     */
    @SuppressWarnings("unused")
    public static Observable<List<PatientLink>> findAllByPatient(Person patient) {
        return findByPerson(patient, Person.PATIENT, -1);
    }

    /**
     * get latest PatientLink by caretaker
     *
     * @param caretaker the caretaker's Person object
     * @return PatientLink Observable that contains a link between the patient and the caretaker.
     */
    public static Observable<PatientLink> findLatestByCaretaker(Person caretaker) {
        return findByPerson(caretaker, Person.CARETAKER, 1)
                .map(patientLinks -> {
                    if (patientLinks == null) {
                        return null;
                    } else {
                        return patientLinks.get(0);
                    }
                });
    }

    /**
     * get a list of PatientLinks by caretaker
     *
     * @param caretaker the caretaker's Person object
     * @return List&lt;PatientLink&gt; Observable that contains list of links between the caretaker and all associated patients.
     */
    public static Observable<List<PatientLink>> findAllByCaretaker(Person caretaker) {
        return findByPerson(caretaker, Person.CARETAKER, -1);
    }

    /**
     * get all PatientLinks by caretaker
     *
     * @param person     a Person Object.
     * @param personType type of person
     * @param limit      number of results
     * @return List&lt;PatientLink&gt; Observable that contains links between the patient and the caretaker.
     */
    private static Observable<List<PatientLink>> findByPerson(Person person, @Person.Type int personType, int limit) {
        return Observable.create(subscriber -> {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PATIENT_LINK);
            String personTypeKey = (personType == Person.PATIENT) ? KEY_PATIENT : KEY_CARETAKER;
            query.whereEqualTo(personTypeKey, person.getParseObject());
            // latest first
            query.addDescendingOrder("createdAt");
            if (limit > 0) {
                query.setLimit(limit);
            }
            query.findInBackground((list, e) -> {
                try {
                    List<PatientLink> patientLinks = new ArrayList<>();
                    if (e == null && list.size() >= 0) {
                        for (int i = 0; i < list.size(); i++) {
                            patientLinks.add(deserialize(list.get(i)));
                        }
                        subscriber.onNext(patientLinks);
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

    public ParseObject getParseObject() {
        if (parseObject != null) {
            return parseObject;
        } else if (objectId != null) {
            return ParseObject.createWithoutData(KEY_PATIENT_LINK, objectId);
        } else {
            return null;
        }
    }

    public Person getPatient() {
        return patient;
    }

    public Person getCaretaker() {
        return caretaker;
    }

    public String getObjectId() {
        return objectId;
    }

    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PATIENT_LINK));
    }

    private ParseObject serialize(ParseObject link) {
        link.put(KEY_PATIENT, patient.getParseObject());
        link.put(KEY_CARETAKER, caretaker.getParseObject());
        return link;
    }

    /**
     * This method automatically checks for duplicate and save the personlink object to database.
     *
     * @return PatientLink Observable that contains a link between the patient and the caretaker.
     */
    public Observable<PatientLink> save() {
        return Observable.defer(() -> Observable.create(subscriber -> {
            PatientLink link = null;
            link = findByPersons(patient, caretaker).toBlocking().first();

            if (link == null) {
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
            } else {
                Log.e(TAG, "patientLink exists");
                subscriber.onNext(link);
                subscriber.onCompleted();
            }

        }));
    }

    /**
     * delete PatientLink
     *
     * @return PatientLink Observable or null for success
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
