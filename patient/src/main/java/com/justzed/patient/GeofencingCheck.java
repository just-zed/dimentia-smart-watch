package com.justzed.patient;

/**
 * Created by Tristan on 03/09/2015.
 *
 * This class checks wether the patient is in a geofence.
 */
public class GeofencingCheck {
    //Constants
    //Int INSIDE_FENCE = 0
    //Int OUTSIDE_FENCE = 1

    //Variables
    //Int PreviouslyInAFence = INSIDE_FENCE
    //Int CurrentlyInAFence = INSIDE_FENCE
    //List<double[]> of all Geofences
    //Current Location

    /**
     * Created by Tristan.
     *
     * Main method to check if a patient is in a geofence.
     */

        //Get all the geofences from the database
        //if geofences are found
            //Get my location
            //for each geofence, check wether i'm inside or outside.
            //depending on wether i was inside or outside a geofence previously return a value

    /**
     * Created by Tristan.
     *
     * This method gets the values of all geofences from the database and stores them in a list.
     */
        //Erase all data from the list
        //get each geofence data relevant from the geofence table and store it in a list

    /**
     * Created by Tristan.
     *
     * //Get my current location and store it
     */

    /**
     * Created by Tristan.
     *
     * This uses the location of the device and all the geofence values to check wether the device is inside a geofence.
     */
        //set outside geofence to false

        //for each geofence
        //check if i am inside of it.
        //  if i am outside any geofence, change outside geofence to true.
        //Store the previous value
        //Store the new value

    /**
     * Created by Tristan.
     *
     * This checks if the patient has entered or exited a fence.
     */
        //if the patient was inside a fence and is now outside,
            //return 1 (patient has exited a fence)
        //if the patient was outside a fence and is now inside,
            //return 2 (patient has re-entered a fence)
        //else
            //return 0 (the patient status has not changed (no notification is needed)).
}
