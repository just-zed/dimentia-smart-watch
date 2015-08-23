package com.justzed.caretaker;
import android.test.ActivityTestCase;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Shirin on 8/23/2015.
 */
public class SaveSyncTokenTest extends ActivityUnitTestCase<MainActivity> {
    public SaveSyncTokenTest() {
        super(MainActivity.class);
    }


    @Test
    public void TestfindDeviceId() {
        SaveSyncToken saveSyncToken = new SaveSyncToken(getActivity());
        String careTakerToken = saveSyncToken.findDeviceId();
        assertNotNull(careTakerToken);
    }
    /*
    @SmallTest
    public String TestPatientDeviceID(){
        SaveSyncToken saveSyncToken =new SaveSyncToken(getActivity());
        String patientToken=saveSyncToken.patientDeviceID();
        return patientToken;
    }
    @Test
    public void testInserToDB(){
        SaveSyncToken saveSyncToken = new SaveSyncToken(getActivity());
        PatientLink patientLink = null;
        saveSyncToken.inserToDB("7326473462","123456");
        Assert.assertEquals("7326473462",patientLink.getCaretaker() );
        Assert.assertEquals("123456", patientLink.getPatient());
        /*
        Person patient = Person.getByUniqueToken("").toBlocking().first();
        Person caretaker = Person.getByUniqueToken("123302-032-21-03").toBlocking().first();

        PatientLink patientLink = new PatientLink(patient, caretaker).save().toBlocking().single();
        */
        /*
        Person patient1 = patientLink.getPatient();
        Person caretaker1 = patientLink.getCaretaker();

        if (patient1.getType() == Person.PATIENT){
            //do something
        }

        patient1.getUniqueToken();
        caretaker1.getUniqueToken();


        PatientLink patientLink1 = PatientLink.getByPatient(patient).toBlocking().single();
        */
}



