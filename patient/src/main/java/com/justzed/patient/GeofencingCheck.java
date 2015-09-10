package com.justzed.patient;

import android.location.Location;
import android.support.annotation.IntDef;

import com.justzed.common.model.PatientFence;
import com.justzed.common.model.Person;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tristan Dubois on 03/09/2015.
 * <p>
 * This class checks wether the patient is in a geofence.
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

    //Location and Geofencing Indicies
    //TODO: change it to use PatientFence class later
    private static final int LATITUDE_INDEX = 0;
    private static final int LONGITUDE_INDEX = 1;
    private static final int RADIUS_INDEX = 2;


    //Variables
    @Status
    private int previouslyInAFence = INSIDE_FENCE;
    @Status
    private int currentlyInAFence = INSIDE_FENCE;
    private List<double[]> geofenceList;
    private double[] currentLocation;


    /**
     * Created by Tristan Dubois.
     * <p>
     * Main method to check if a patient is in a geofence.
     */
    @StatusChange
    public int checkGeofence(double[] myLocation, Person patient,List<PatientFence> patientFences) {

        if (!geofenceList.isEmpty()) {
            checkIfInsideGeofences(geofenceList, myLocation);
            return checkIfStatusHasChanged(currentlyInAFence, previouslyInAFence);
        } else {
            return NO_GEOFENCES_FOUND;
        }
    }

    /**
     * Created by Tristan Dubois.
     * <p>
     * This method gets the values of all geofences from the database and stores them in a list.
     */
    public List<double[]> getGeofencesFromDatabase(List<PatientFence> patientFences) {
        geofenceList = new ArrayList<>();

        if (patientFences != null) {
            for (int i = 0; i < patientFences.size(); i++) {
                PatientFence patientFence = patientFences.get(i);
                double[] toAddToList = new double[]{patientFence.getCenter().latitude, patientFence.getCenter().longitude, patientFence.getRadius()};

                geofenceList.add(toAddToList);
            }
        }
        return geofenceList;

    }

    /**
     * Created by Tristan Dubois.
     * <p>
     * This uses the location of the device and all the geofence values to check wether the device is inside a geofence.
     */

    //TODO: change to more type safe codes
    @Status
    public int checkIfInsideGeofences(List<PatientFence> patientFences, double[] deviceLocation) {
        float[] distance = new float[1];
        double distanceBetweenTwoPoints;
        previouslyInAFence = currentlyInAFence;

        if (!patientFences.isEmpty()) {
            currentlyInAFence = OUTSIDE_FENCE;

            for (int indexOfGeofences = 0; indexOfGeofences < patientFences.size(); indexOfGeofences++) {
                Location.distanceBetween(patientFences.get(indexOfGeofences).getCenter().latitude,
                        patientFences.get(indexOfGeofences).getCenter().longitude,
                        deviceLocation[LATITUDE_INDEX],
                        deviceLocation[LONGITUDE_INDEX],
                        distance);

                distanceBetweenTwoPoints = new BigDecimal(String.valueOf(distance[0])).doubleValue();

                //Uses the equation Math.sqrt((lat2-lat1)*(lat2-lat1) + (long2-long1)*(long2-long1))to check if the distance between the two points is less than
                //the radius.
                //distanceBetweenTwoPoints = Math.sqrt((geofences.get(indexOfGeofences)[LATITUDE_INDEX] - deviceLocation[LATITUDE_INDEX])
                //       * (geofences.get(indexOfGeofences)[LATITUDE_INDEX] - deviceLocation[LATITUDE_INDEX])
                //        + (geofences.get(indexOfGeofences)[LONGITUDE_INDEX] - deviceLocation[LONGITUDE_INDEX])
                //        * (geofences.get(indexOfGeofences)[LONGITUDE_INDEX] - deviceLocation[LONGITUDE_INDEX]));

                if (distanceBetweenTwoPoints < geofences.get(indexOfGeofences)[RADIUS_INDEX]) {
                    currentlyInAFence = INSIDE_FENCE;
                }
            }


        }
        return currentlyInAFence;
    }

    /**
     * Created by Tristan Dubois.
     * <p>
     * This checks if the patient has entered or exited a fence.
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