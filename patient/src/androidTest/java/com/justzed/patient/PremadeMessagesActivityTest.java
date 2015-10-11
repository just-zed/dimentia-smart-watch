package com.justzed.patient;

import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityTestCase;
import android.widget.ListView;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Hiroki Takahashi on 6/10/2015.
 * Testing is broken (11/10/2015)
 */
public class PremadeMessagesActivityTest extends ActivityInstrumentationTestCase2<PremadeMessagesActivity> {

    private PremadeMessagesActivity messageActivity;
    private ListView testListView;

    public PremadeMessagesActivityTest() {
        super(PremadeMessagesActivity.class);
    }

    /*
     * Set up testing
     * @return nothing
     */
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        messageActivity = getActivity();
        //setActivityInitialTouchMode(true);
        //testListView = (ListView)messageActivity.findViewById(R.id.list);
    }

    @Test
    public void testList(){

        onView(withId(R.id.list)).perform(click());

    }

    /*
     * Tear down testing
     * @return noting
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
