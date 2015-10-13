package com.justzed.common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContentResolver;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

/**
 * copied from http://stackoverflow.com/questions/6610437/how-to-test-class-using-content-resolver-provider
 *
 * @author Freeman Man
 * @version 1.0
 * @since 2015-10-13
 */
@RunWith(AndroidJUnit4.class)
public class DeviceUtilsTest extends AndroidTestCase {

    //Specialized Mock Content provider for step 2.  Uses a hashmap to return data dependent on the uri in the query
    private class HashMapMockContentProvider extends MockContentProvider {
        private HashMap<Uri, Cursor> expectedResults = new HashMap<>();

        public void addQueryResult(Uri uriIn, Cursor expectedResult) {
            expectedResults.put(uriIn, expectedResult);
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            return expectedResults.get(uri);
        }
    }

    private class ContextWithMockContentResolver extends RenamingDelegatingContext {
        private ContentResolver contentResolver;

        public void setContentResolver(ContentResolver contentResolver) {
            this.contentResolver = contentResolver;
        }

        public ContextWithMockContentResolver(Context targetContext) {
            super(targetContext, "test");
        }

        @Override
        public ContentResolver getContentResolver() {
            return contentResolver;
        }

        @Override
        public Context getApplicationContext() {
            return this;
        } //Added in-case my class called getApplicationContext()
    }


    @Test
    public void testGetDeviceOwnerName() {

        String contextName = "Context Name";
        String defaultName = "Default Name";


        //Step 1: Create data you want to return and put it into a matrix cursor
        //In this case I am mocking getting phone numbers from Contacts Provider
        String[] exampleData = {contextName};
        String[] exampleProjection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        MatrixCursor matrixCursor = new MatrixCursor(exampleProjection);
        matrixCursor.addRow(exampleData);

        //Step 2: Create a stub content provider and add the matrix cursor as the expected result of the query
        HashMapMockContentProvider mockProvider = new HashMapMockContentProvider();
        mockProvider.addQueryResult(ContactsContract.Profile.CONTENT_URI, matrixCursor);

        //Step 3: Create a mock resolver and add the content provider.
        MockContentResolver mockResolver = new MockContentResolver();
        mockResolver.addProvider(ContactsContract.AUTHORITY /*Needs to be the same as the authority of the provider you are mocking */, mockProvider);

        //Step 4: Add the mock resolver to the mock context
        ContextWithMockContentResolver mockContext = new ContextWithMockContentResolver(getContext());
        mockContext.setContentResolver(mockResolver);


        // our test
        String ownerName = DeviceUtils.getDeviceOwnerName(mockContext, defaultName);
        String ownerNullName = DeviceUtils.getDeviceOwnerName(null, defaultName);

        assertNotSame(ownerName, defaultName);
        assertEquals(ownerNullName, defaultName);

    }

    @Test
    public void testGetDeviceOwnerNameIfOwnerNameNotSet() {


        String defaultName = "Default Name";


        //Step 1: Create data you want to return and put it into a matrix cursor
        //In this case I am mocking getting phone numbers from Contacts Provider
        String[] exampleProjection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
        MatrixCursor matrixCursor = new MatrixCursor(exampleProjection);

        //Step 2: Create a stub content provider and add the matrix cursor as the expected result of the query
        HashMapMockContentProvider mockProvider = new HashMapMockContentProvider();
        mockProvider.addQueryResult(ContactsContract.Profile.CONTENT_URI, matrixCursor);

        //Step 3: Create a mock resolver and add the content provider.
        MockContentResolver mockResolver = new MockContentResolver();
        mockResolver.addProvider(ContactsContract.AUTHORITY /*Needs to be the same as the authority of the provider you are mocking */, mockProvider);

        //Step 4: Add the mock resolver to the mock context
        ContextWithMockContentResolver mockContext = new ContextWithMockContentResolver(getContext());
        mockContext.setContentResolver(mockResolver);


        // our test
        String ownerName = DeviceUtils.getDeviceOwnerName(mockContext, defaultName);

        assertEquals(ownerName, defaultName);

    }


}
