package com.justzed.patient;

import android.location.Location;
import android.support.annotation.IntDef;

import com.justzed.common.model.PatientFence;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class checks wether the patient is in a geofence.
 *
 * @author Tristan Dubois
 * @version 2.0
 * @since 2015-09-03
 */
public class GeofencingCheck {

    private static final String TAG = GeofencingCheck.class.getSimpleName();

    //Constants
    //Status
    public static final int INSIDE_FENCE = 0;
    public static final int OUTSIDE_FENCE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({INSIDE_FENCE, OUTSIDE_FENCE})
    public @interface Status {
    }

    //Status Change
    public static final int EXITED_A_FENCE = 1;
    public static final int REENTERED_A_FENCE = 2;
    public static final int NOTHING_HAS_CHANGED = 0;
    public static final int NO_GEOFENCES_FOUND = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOTHING_HAS_CHANGED, EXITED_A_FENCE, REENTERED_A_FENCE, NO_GEOFENCES_FOUND})
    public @interface StatusChange {
    }

    //Variables
    @Status
    private int previouslyInAFence = INSIDE_FENCE;
    @Status
    private int currentlyInAFence = INSIDE_FENCE;
    private List<PatientFence> geofenceList;

    public void setPreviouslyInAFence(@Status int Status){previouslyInAFence = Status;}


    /**
     * Main method to check if a patient is in a geofence.
     *
     * @param myLocation This is the location of a patient.
     * @param patient This is the Person database details of the patient.
     * @return int This returns whether a status changed has happened or if there are no geofences.
     */
    @StatusChange
    public int checkGeofence(PatientLocation myLocation, Person patient) {
        if(geofenceList != null){
            if ( !geofenceList.isEmpty()) {
                checkIfInsideGeofences(geofenceList, myLocation);
                return checkIfStatusHasChanged(currentlyInAFence, previouslyInAFence);
            } else {
                return NO_GEOFENCES_FOUND;}
        }else {
            return NO_GEOFENCES_FOUND;}
    }

    /**
     * This method gets the values of all geofences from the database and stores them in a list.
     *
     * @param patientFences This is a list of geofences
     * @return List PatientFence This returns a clean List of geofences
     */
    public List<PatientFence> getGeofencesFromDatabase(List<PatientFence> patientFences) {
        geofenceList = new ArrayList<>();
        /*
        if (patientFences != null) {
            for (int i = 0; i < patientFences.size(); i++) {
                PatientFence toAddToList = patientFences.get(i);
                geofenceList.add(toAddToList);
            }
        }*/
        geofenceList = patientFences;
        return geofenceList;

    }

    /**
     * This uses the location of the device and all the geofence values to check wether the device is inside a geofence.
     *
     * @param patientFences This is a List of the geofences being checked.
     * @param deviceLocation This is the device thats being checked.
     * @return int This returns the status after all geofences have been checked.
     */

    @Status
    public int checkIfInsideGeofences(List<PatientFence> patientFences, PatientLocation deviceLocation) {
        float[] distance = new float[1];
        double distanceBetweenTwoPoints;
        previouslyInAFence = currentlyInAFence;

        if (!patientFences.isEmpty()) {
            currentlyInAFence = OUTSIDE_FENCE;

            for (int indexOfGeofences = 0; indexOfGeofences < patientFences.size(); indexOfGeofences++) {

                Calendar currentDate = Calendar.getInstance();
                Calendar geofenceEndDate = patientFences.get(indexOfGeofences).getEndTime();
                Calendar geofenceStartDate = patientFences.get(indexOfGeofences).getStartTime();

                /* If the geofence has no end date
                 * or if the geofence has and end date and the current time is before or equal to the end time,
                 * check if the patient is inside or outside the fence.
                 */
                if (geofenceEndDate == null
                        || geofenceEndDate.before(geofenceStartDate)
                        || geofenceEndDate.after(currentDate)
                        || currentDate.equals(geofenceEndDate)) {

                    Location.distanceBetween(patientFences.get(indexOfGeofences).getCenter().latitude,
                            patientFences.get(indexOfGeofences).getCenter().longitude,
                            deviceLocation.getLatLng().latitude,
                            deviceLocation.getLatLng().longitude,
                            distance);

                    distanceBetweenTwoPoints = new BigDecimal(String.valueOf(distance[0])).doubleValue();

                    if (distanceBetweenTwoPoints < patientFences.get(indexOfGeofences).getRadius()) {
                        currentlyInAFence = INSIDE_FENCE;

                    }
                }
            }
        }
        return currentlyInAFence;
    }

    /**
     * This checks if the patient has entered or exited a fence.
     *
     * @param currentStatus This is the current status of the patient.
     * @param previousStatus This is the last status of the patient
     * @return int This returns a status change
     */
    @StatusChange
    public int checkIfStatusHasChanged(@Status int currentStatus, @Status int previousStatus) {

        if (previousStatus == INSIDE_FENCE && currentStatus == OUTSIDE_FENCE) {
            //if the patient was inside a fence and is now outside,
            //return 1 (patient has exited a fence)
            return EXITED_A_FENCE;

        } else if (previousStatus == OUTSIDE_FENCE && currentStatus == INSIDE_FENCE) {
            return REENTERED_A_FENCE;

        } else {
            return NOTHING_HAS_CHANGED;
        }
    }

}