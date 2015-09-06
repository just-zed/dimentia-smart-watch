package com.justzed.patient;

import com.justzed.common.model.PatientFence;
import com.justzed.common.model.Person;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Tristan Dubois on 03/09/2015.
 * <p>
 * This class checks wether the patient is in a geofence.
 */
public class GeofencingCheck {
    //Constants
    //Status
    private int INSIDE_FENCE = 0;
    private int OUTSIDE_FENCE = 1;
    //Status Change
    private int EXITED_A_FENCE = 1;
    private int REENTERED_A_FENCE = 2;
    private int NOTHING_HAS_CHANGED = 0;
    private int NO_GEOFENCES_FOUND = 3;
    //Location and Geofencing Indicies
    private int LATITUDE_INDEX = 0;
    private int LONGITUDE_INDEX = 1;
    private int RADIUS_INDEX = 2;

    //Variables
    private int previouslyInAFence = INSIDE_FENCE;
    private int currentlyInAFence = INSIDE_FENCE;
    private List<double[]> geofenceList;
    private double[] currentLocation;


    /**
     * Created by Tristan Dubois.
     * <p>
     * Main method to check if a patient is in a geofence.
     */
    public int checkGeofence(double[] myLocation, Person patient) {
        getGeofencesFromDatabase(patient);

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
    public void getGeofencesFromDatabase(Person person) {
        geofenceList = new ArrayList<double[]>();

        List<PatientFence> patientFences = PatientFence.getPatientFences(person)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(patientFences1 -> patientFences1 != null && patientFences1.size() > 0)
                .toBlocking().single();

        for (int i = 0; i < patientFences.size(); i++) {
            PatientFence patientFence = patientFences.get(i);
            double[] toAddToList = new double[]{patientFence.getCenter().latitude, patientFence.getCenter().longitude, patientFence.getRadius()};

            geofenceList.add(toAddToList);

        }

    }

    /**
     * Created by Tristan Dubois.
     * <p>
     * This uses the location of the device and all the geofence values to check wether the device is inside a geofence.
     */
    public int checkIfInsideGeofences(List<double[]> geofences, double[] deviceLocation) {
        double distanceBetweenTwoPoints;
        previouslyInAFence = currentlyInAFence;

        if (!geofences.isEmpty()) {
            currentlyInAFence = OUTSIDE_FENCE;
            for (int indexOfGeofences = 0; indexOfGeofences < geofences.size(); indexOfGeofences++) {
                //Uses the equation Math.sqrt((lat2-lat1)*(lat2-lat1) + (long2-long1)*(long2-long1))to check if the distance between the two points is less than
                //the radius.
                distanceBetweenTwoPoints = Math.sqrt((geofences.get(indexOfGeofences)[LATITUDE_INDEX] - deviceLocation[LATITUDE_INDEX])
                        * (geofences.get(indexOfGeofences)[LATITUDE_INDEX] - deviceLocation[LATITUDE_INDEX])
                        + (geofences.get(indexOfGeofences)[LONGITUDE_INDEX] - deviceLocation[LONGITUDE_INDEX])
                        * (geofences.get(indexOfGeofences)[LONGITUDE_INDEX] - deviceLocation[LONGITUDE_INDEX]));

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
    public int checkIfStatusHasChanged(int currentStatus, int previousStatus) {

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