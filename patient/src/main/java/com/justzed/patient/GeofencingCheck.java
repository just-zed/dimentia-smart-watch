package com.justzed.patient;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tristan Dubois on 03/09/2015.
 *
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
     *
     * Main method to check if a patient is in a geofence.
     */
    public void checkGeofence(){
        getGeofencesFromDatabase();

        if(!geofenceList.isEmpty()) {
            getMyLocationFromDatabase();
            checkIfInsideGeofences(geofenceList, currentLocation);
            checkIfStatusHasChanged(currentlyInAFence, previouslyInAFence);
        }
        else{

        }
    }

        //Get all the geofences from the database
        //if geofences are found
            //Get my location
            //for each geofence, check wether i'm inside or outside.
            //depending on wether i was inside or outside a geofence previously return a value
    /**
     * Created by Tristan Dubois.
     *
     * This method gets the values of all geofences from the database and stores them in a list.
     */
    public void getGeofencesFromDatabase(){
        geofenceList = new ArrayList<double[]>();

        for (int i = 0; i < 0; i++){

        }
    }
        //Erase all data from the list
        //get each geofence data relevant from the geofence table and store it in a list

    /**
     * Created by Tristan Dubois.
     *
     * //Get my current location and store it
     */
    public void getMyLocationFromDatabase(){

    }

    /**
     * Created by Tristan Dubois.
     *
     * This uses the location of the device and all the geofence values to check wether the device is inside a geofence.
     */
    public int checkIfInsideGeofences(List<double[]> geofences, double[] deviceLocation){
        double distanceBetweenTwoPoints;
        previouslyInAFence = currentlyInAFence;

        if(!geofences.isEmpty()) {
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
        return  currentlyInAFence;
    }

    /**
     * Created by Tristan Dubois.
     *
     * This checks if the patient has entered or exited a fence.
     */
    public int checkIfStatusHasChanged(int currentStatus, int previousStatus) {

        if (previousStatus == INSIDE_FENCE && currentStatus == OUTSIDE_FENCE) {
            //if the patient was inside a fence and is now outside,
            //return 1 (patient has exited a fence)
            return EXITED_A_FENCE;

        }
        else if (previousStatus == OUTSIDE_FENCE && currentStatus == INSIDE_FENCE) {
            return REENTERED_A_FENCE;

        }
        else {
            return NOTHING_HAS_CHANGED;
        }
    }
}