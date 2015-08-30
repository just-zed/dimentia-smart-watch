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
 * Created by freeman on 8/16/15.
 */
public class Person implements Parcelable {

    public static final String PARCELABLE_KEY = "person";

    private static final String TAG = "Person";


    private static final String KEY_PERSON = "Person";
    private static final String KEY_TYPE_ID = "typeId";
    private static final String KEY_UNIQUE_TOKEN = "uniqueToken";


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.getObjectId(),
                this.getUniqueToken()
        });
        dest.writeInt(this.getType());
    }

    protected Person(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        objectId = data[0];
        type = parseType(in.readInt());
        uniqueToken = data[1];
    }

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

    private ParseObject parseObject;

    public String getObjectId() {
        return objectId;
    }

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


    public Person(ParseObject parseObject, @Type int type, String uniqueToken) {
        this.objectId = parseObject.getObjectId();
        this.parseObject = parseObject;
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


    public static Person deserialize(ParseObject parseObject) {
        return new Person(parseObject,
                parseType(parseObject.getInt(KEY_TYPE_ID)),
                parseObject.getString(KEY_UNIQUE_TOKEN));
    }

    /**
     * this method automatically checks for duplicate and save the person object to database
     * <p>
     * usage:
     * person.save()
     * .subscribeOn(Scheduler.io())
     * .observerOn(AndroidSchedulers.mainThread())
     * .subscribe(person->{
     * // do something in UI thread with saved person object with new objectId already inside
     * },throwable->{
     * // handle error
     * })
     *
     * @return Observable<Person>
     */
    public Observable<Person> save() {
        return Observable.create(subscriber -> {
            Person person = null;
            person = getByUniqueToken(uniqueToken).toBlocking().first();

            if (person == null || person.getType() != type) {
                ParseObject parseObject;
                if (person == null) {
                    parseObject = this.serialize();
                } else {
                    parseObject = this.serialize(person.getParseObject());
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
     * (static)
     * usage:
     * Person.getByUniqueToken(token)
     * .subscribeOn(Scheduler.io())
     * .observerOn(AndroidSchedulers.mainThread())
     * .subscribe(person->{
     * // do something in UI thread with retrieved person object
     * },throwable->{
     * // handle error
     * })
     *
     * @param uniqueToken generated token that's unique to device
     * @return Observable<Person>
     */
    public static Observable<Person> getByUniqueToken(String uniqueToken) {

        return Observable.create(subscriber -> {
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
        });

    }

    /**
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
     * @return Observable<Person>
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
