package com.justzed.common.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rx.Observable;

/**
 * Person object + data access layer, implements parcelable
 * person object can be parceled and passed between activities through intent
 * <p>
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-08-16
 */
public class Person implements Parcelable {


    public static final int INACTIVE_USER = -1;
    public static final int PATIENT = 0;
    public static final int CARETAKER = 1;

    @IntDef({INACTIVE_USER, PATIENT, CARETAKER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    @Type
    public static int parseType(int typeId) {
        switch (typeId) {
            case PATIENT:
                return PATIENT;
            case CARETAKER:
                return CARETAKER;
            default:
                return INACTIVE_USER;
        }
    }

    private static final String TAG = "Person";
    private static final String KEY_PERSON = "Person";
    private static final String KEY_TYPE_ID = "typeId";
    private static final String KEY_UNIQUE_TOKEN = "uniqueToken";
    private static final String KEY_NAME = "name";
    private static final String KEY_DISABLE_GEOFENCE_CHECkS = "disableGeofenceChecks";
    private String objectId = null;

    @Type
    private int type;
    private String uniqueToken;
    private boolean disableGeofenceChecks = false;
    private String name;


    private ParseObject parseObject;

    public static final String PARCELABLE_KEY = "person";
    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    protected Person(Parcel in) {
        objectId = in.readString();
        type = parseType(in.readInt());
        uniqueToken = in.readString();
        disableGeofenceChecks = in.readInt() == 1;
        name = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectId);
        dest.writeInt(type);
        dest.writeString(uniqueToken);
        dest.writeInt((disableGeofenceChecks) ? 1 : 0);
        dest.writeString(name);

    }

    public Person(@Type int type, String uniqueToken) {
        this.type = type;
        this.uniqueToken = uniqueToken;
        this.disableGeofenceChecks = false;
    }


    private Person(ParseObject parseObject,
                   @Type int type,
                   String uniqueToken,
                   boolean disableGeofenceChecks,
                   String name) {
        this.objectId = parseObject.getObjectId();
        this.parseObject = parseObject;
        this.type = type;
        this.uniqueToken = uniqueToken;
        this.disableGeofenceChecks = disableGeofenceChecks;
        this.name = name;
    }


    protected static Person deserialize(ParseObject parseObject) {
        return new Person(parseObject,
                parseType(parseObject.getInt(KEY_TYPE_ID)),
                parseObject.getString(KEY_UNIQUE_TOKEN),
                parseObject.getBoolean(KEY_DISABLE_GEOFENCE_CHECkS),
                parseObject.getString(KEY_NAME));
    }

    /**
     * (static) get person by unique token
     * <p>
     * usage:
     * Person.findByUniqueToken(token)
     * .subscribeOn(Scheduler.io())
     * .observerOn(AndroidSchedulers.mainThread())
     * .subscribe(person->{
     * // do something in UI thread with retrieved person object
     * },throwable->{
     * // handle error
     * })
     *
     * @param uniqueToken generated token that's unique to device
     * @return Person Observable
     */
    public static Observable<Person> findByUniqueToken(String uniqueToken) {

        return Observable.create(subscriber -> {
            if (uniqueToken.isEmpty()) {
                subscriber.onError(new Exception("unique token cannot be empty"));
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSON);
            query.whereEqualTo(KEY_UNIQUE_TOKEN, uniqueToken);
            query.setLimit(1);
            query.findInBackground((list, e) -> {
                if (e == null && list != null && list.size() == 1) {
                    subscriber.onNext(deserialize(list.get(0)));
                    subscriber.onCompleted();
                } else if (list != null && list.size() == 0) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(e);
                }
            });
        });

    }


    public String getObjectId() {
        return objectId;
    }

    /**
     * If parseObject is empty but objectId is not, this creates an empty ParseObject("Person")
     * with only objectId inside, and can be used for parse.com pointer relationship.
     *
     * @return ParseObject of person
     */
    public ParseObject getParseObject() {
        if (parseObject != null) {
            return parseObject;
        } else if (objectId != null) {
            return ParseObject.createWithoutData(KEY_PERSON, objectId);
        } else {
            return null;
        }
    }

    @Type
    public int getType() {
        return type;
    }

    public boolean getDisableGeofenceChecks() {
        return disableGeofenceChecks;
    }

    public void setDisableGeofenceChecks(boolean disableGeofenceChecks) {
        this.disableGeofenceChecks = disableGeofenceChecks;
    }

    public String getUniqueToken() {
        return uniqueToken;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private ParseObject serialize() {
        return serialize(new ParseObject(KEY_PERSON));
    }

    private ParseObject serialize(ParseObject person) {
        person.put(KEY_TYPE_ID, type);
        person.put(KEY_UNIQUE_TOKEN, uniqueToken);
        person.put(KEY_DISABLE_GEOFENCE_CHECkS, disableGeofenceChecks);
        if (name != null) {
            person.put(KEY_NAME, name);
        }
        return person;
    }

    /**
     * this method automatically checks for duplicate and save the person object to database
     * <p>
     * TODO: this will need to be fixed later
     * currently if the type is changed (certain device is changed from patient to caretaker)
     * it still saves correctly
     * <p>
     * usage:
     * person.save()
     * .subscribeOn(Scheduler.io())
     * .observerOn(AndroidSchedulers.mainThread())
     * .subscribe(person->{
     * // do something in UI thread with saved person object with new objectId
     * },throwable->{
     * // handle error
     * })
     *
     * @return Person Observable
     */
    public Observable<Person> save() {
        return Observable.create(subscriber -> {
            Person person = null;
            person = findByUniqueToken(uniqueToken).toBlocking().first();

            if (person == null || objectId != null || person.getType() != type) {
                ParseObject parseObject;
                if (person != null) {
                    parseObject = this.serialize(person.getParseObject());
                } else {
                    if (this.objectId != null) {
                        parseObject = this.serialize(getParseObject());
                    } else {
                        parseObject = this.serialize();
                    }
                }
                parseObject.saveInBackground(e -> {
                    if (e == null) {
                        this.objectId = parseObject.getObjectId();
                        this.parseObject = parseObject;
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

        });
    }

    /**
     * delete person from database, return null if success
     * <p>
     * usage:
     * person.delete()
     * .subscribeOn(Scheduler.io())
     * .observerOn(AndroidSchedulers.mainThread())
     * .subscribe(person->{
     * // person is always null
     * },throwable->{
     * // handle error
     * })
     *
     * @return Person Observable (null for success)
     */
    public Observable<Person> delete() {
        return Observable.create(subscriber -> {
            if (objectId == null) {
                // this should never happen in the app
                subscriber.onError(new Exception("incorrect usage"));
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(KEY_PERSON);
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
