package com.justzed.common.models;

import android.support.annotation.IntDef;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rx.Observable;

/**
 * Created by freeman on 8/16/15.
 */
public class Person {

    private static final String TAG = "Person";


    private static final String KEY_PERSON = "Person";
    private static final String KEY_TYPE_ID = "typeId";
    private static final String KEY_UNIQUE_TOKEN = "uniqueToken";


    @IntDef({PATIENT, CARETAKER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public static final int PATIENT = 0;
    public static final int CARETAKER = 1;

    //this can only be set by internal operation
    private String objectId = null;
    @Type
    private int type;
    private String uniqueToken;

    public String getObjectId() {
        return objectId;
    }

    @Type
    public int getType() {
        return type;
    }

    @Type
    public static int parseType(int typeId) {
        switch (typeId) {
            case PATIENT:
                return PATIENT;
            default:
                return CARETAKER;
        }
    }

    public String getUniqueToken() {
        return uniqueToken;
    }

    public Person(@Type int type, String uniqueToken) {
        this.type = type;
        this.uniqueToken = uniqueToken;
    }

    public Person(String objectId, @Type int type, String uniqueToken) {
        this.objectId = objectId;
        this.type = type;
        this.uniqueToken = uniqueToken;
    }


    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PERSON));
    }

    private ParseObject serialize(ParseObject person) {
        person.put(KEY_TYPE_ID, type);
        person.put(KEY_UNIQUE_TOKEN, uniqueToken);
        return person;
    }


    private static Person deserialize(ParseObject parseObject) {
        return new Person(parseObject.getObjectId(),
                parseType(parseObject.getInt(KEY_TYPE_ID)),
                parseObject.getString(KEY_UNIQUE_TOKEN));
    }

    public Observable<Person> save() {
        return Observable.defer(() -> Observable.create(subscriber -> {
            Person person = null;
            person = getByUniqueToken(uniqueToken).toBlocking().first();

            if (person == null) {
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
                Log.e(TAG, "unique token exists");
                subscriber.onNext(person);
                subscriber.onCompleted();
            }

        }));
    }

    public static Observable<Person> getByUniqueToken(String uniqueToken) {

        return Observable.defer(() ->
                Observable.create(subscriber -> {
                    if (uniqueToken.isEmpty()) {
                        subscriber.onError(new Exception("unique token cannot be empty"));
                    }
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSON);
                    query.whereEqualTo(KEY_UNIQUE_TOKEN, uniqueToken);
                    query.setLimit(1);
                    query.findInBackground((list, e) -> {
                        if (e == null && list.size() == 1) {
                            subscriber.onNext(deserialize(list.get(0)));
                            subscriber.onCompleted();
                        } else if (list.size() == 0) {
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(e);
                        }
                    });
                }));

    }


    public Observable<Person> delete() {
        return Observable.defer(() ->
                Observable.create(subscriber -> {
                    if (objectId == null) {
                        // this should never happen in the app
                        subscriber.onError(new Exception("incorrect usage"));
                    }
                    ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSON);
                    query.getInBackground(objectId, (parseObject, e) -> {
                        if (e == null) {
                            parseObject.deleteInBackground(e1 -> {
                                if (e1 == null) {
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
                }));
    }
}
