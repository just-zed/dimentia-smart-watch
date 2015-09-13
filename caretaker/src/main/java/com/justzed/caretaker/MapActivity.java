package com.justzed.caretaker;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.view.View.OnClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.justzed.common.model.PatientFence;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CircleOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

public class MapActivity extends FragmentActivity implements OnMapClickListener,
OnMapLongClickListener {
    private static final String TAG = MapActivity.class.getSimpleName();
    //Variables
    private Person patient;

    private String patientMarkerName = "";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Marker patientMarker;
    boolean test = false;

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Declare variables for my functions.
    * */
    //==================== Brian Tran==============
    private LinearLayout fenceLayout;
    private LinearLayout fenceModeLayout;
    private ImageButton ibtnMapCenter;
    private Button btnAdd;
    private Button btnSave;
    private Button btnDelete;
    private Button btnClear;
    private Button btnCancel;
    private TextView txvFenceMode;
    private EditText txtFenceTitle;
    private TextView txvFenceRadius;
    private SeekBar skbFenceRadius;

    private boolean addMode = false;
    private boolean editMode = false;

    private Circle mTempCircle;
    private Marker mTempMarker;

    private List<String> strFencesList;
    private List<Circle> circlesList;
    private List<Marker> markerList;
    private List<PatientFence> patientFenceList;

    private int curPosFence;
    private String curTitleFence;
    private LatLng curCenFence;
    private double curRadFence;

    private final static String TITLE_DEFAULT = "";
    private final static double RADIUS_DEFAULT = 200.0;
    private final static int RADIUS_MAX = 1000;
    private final static int RADIUS_MIN = 0;

    private final static double X = -27.596927;
    private final static double Y = 153.081946;

    // ===================== End Brian Tran ==================

    //Constants

    private static final int UPDATE_TIMER_NORMAL = 10000;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        patient = data.getParcelable(Person.PARCELABLE_KEY);

        mMap = null;
        setContentView(R.layout.activity_map);

        initFenceActivitySetup();
        initFencesList();
    }

    // ==================== Functions Brian Tran =======================

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Set ClickListener for buttons.
    * */
    OnClickListener btnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.add_button:
                    //do stuff
                    clickAddButton();
                    break;
                case R.id.save_button:
                    //do stuff
                    clickSaveButton();
                    break;
                case R.id.delete_button:
                    //do stuff
                    clickDeleteButton();
                    break;
                case R.id.clear_button:
                    //do stuff
                    clickClearButton();
                    break;
                case R.id.cancel_button:
                    //do stuff
                    clickCancelButton();
                    break;
                default: break;
            }
        }
    };

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Set ChangeListener for Seek Bar (adjusting radius of fence).
    * */
    OnSeekBarChangeListener skbChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            changedSeekBar(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Set MapClickListener for Adding and Editing Fences.
    * */
    @Override
    public void onMapClick(LatLng latLng) {
        clickMap(latLng);
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Set MapLongClickListener for going to EDIT AND DELETE FENCE MODE.
    * */
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (!editMode) {
            int size = circlesList.size();
            for (int i = 0; i < size; i++) {
                LatLng center = circlesList.get(i).getCenter();
                double radius = circlesList.get(i).getRadius();
                float[] distance = new float[1];
                Location.distanceBetween(latLng.latitude, latLng.longitude, center.latitude, center.longitude, distance);

                boolean clicked = distance[0] < radius;
                if (clicked) {
                    clickEditButton(i);
                    break;
                }
            }
        }
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Set ChangeListener for Seek Bar (adjust radius of fence).
    * */
    private void initFenceActivitySetup() {
        ibtnMapCenter = (ImageButton) findViewById(R.id.mapCenterButton);

        fenceLayout = (LinearLayout) findViewById(R.id.fence_layout);
        fenceLayout.setVisibility(View.GONE);

        fenceModeLayout = (LinearLayout) findViewById(R.id.fence_mode_layout);
        fenceModeLayout.setVisibility(View.VISIBLE);

        btnAdd = (Button) findViewById(R.id.add_button);
        btnSave = (Button) findViewById(R.id.save_button);
        btnDelete = (Button) findViewById(R.id.delete_button);
        btnClear = (Button) findViewById(R.id.clear_button);
        btnCancel = (Button) findViewById(R.id.cancel_button);
        txvFenceMode = (TextView) findViewById(R.id.fence_mode_text_view);
        txtFenceTitle = (EditText) findViewById(R.id.fence_title_edit_text);
        txvFenceRadius = (TextView) findViewById(R.id.fence_radius_text_view);
        skbFenceRadius = (SeekBar) findViewById(R.id.fence_seek_bar);
        skbFenceRadius.setMax(RADIUS_MAX);

        btnAdd.setOnClickListener(btnClickListener);
        btnSave.setOnClickListener(btnClickListener);
        btnDelete.setOnClickListener(btnClickListener);
        btnClear.setOnClickListener(btnClickListener);
        btnCancel.setOnClickListener(btnClickListener);
        skbFenceRadius.setOnSeekBarChangeListener(skbChangeListener);
    }

    /*
     * Created by Nguyen Nam Cuong Tran
     * <p>
     * Initializing some lists for needed fence functions.
     * */
    private void initFencesList(){
        strFencesList = new ArrayList<String>();
        markerList = new ArrayList<Marker>();
        circlesList = new ArrayList<Circle>();
        // Load existing geofences from database here
        // DO stuff


/*
        ParseObject  patientFence = new ParseObject("PatientFence");

        patient.getParseObject("");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("PatientFence");
        query.whereEqualTo("patient", "WPCxaXY1tg");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fenceList, ParseException e) {
                if (e == null) {
                    int size = fenceList.size();
                    String t = String.valueOf(size).toString();
                    toast("OK");
                    //Log.d("score", "Retrieved " + fenceList.size() + " scores");
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
*/

        PatientFence fence1 = patientFenceList.get(0);
        fence1.delete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        patientFence -> {
                            // updates the object in the list
                            // if patientFence == null it is successful, do another update from database

                        },
                        throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        }
                );

        new PatientFence(patient, new LatLng(0,0), 10f, "")
                .save()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        patientFence -> {
                            // updates the object in the list

                        },
                        throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        }
                );



        getFencesListFromDatabase(patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientFences -> {
                    patientFenceList = patientFences;
                    //loop through the list and add markers and circles...



                },throwable -> {
                    Log.e(TAG, throwable.getMessage());
                });

            int size = patientFenceList.size();
            String t = String.valueOf(size);
            toast(t);

/*        query.

        int size = fenceList.size();

        for (int i = 0; i < size; i++){
            String title = patientFenceList.get(i).getObjectId();
            LatLng center = patientFenceList.get(i).getCenter();
            double radius = patientFenceList.get(i).getRadius();

            strFencesList.add(title);
            markerList.add(drawMarker(mMap, center, title));
            circlesList.add(drawCircle(mMap, center, radius));
        }
*/


    }

    private Marker drawMarker(GoogleMap map, LatLng center, String title){
        return map.addMarker(new MarkerOptions()
                .position(center)
                .title(title));
    }

    private Circle drawCircle(GoogleMap map, LatLng center, double radius){
        return map.addCircle(new CircleOptions()
                .center(center)
                .radius(radius));
    }

    private Observable<List<PatientFence>> getFencesListFromDatabase(Person patient){
        return PatientFence.getPatientFences(patient);
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of clicking Add button.
    * Going to Add mode.
    * */
    private void clickAddButton(){
        fenceLayout.setVisibility(View.VISIBLE);
        ibtnMapCenter.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnAdd.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        txvFenceMode.setText("ADD MODE");
        txtFenceTitle.setText("");
        skbFenceRadius.setProgress(RADIUS_MIN);
        skbFenceRadius.setEnabled(false);
        addMode = true;

        showMarkers(true);
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Show or hide all markers on the Map.
    * */
    private void showMarkers(boolean flag){
        int size = markerList.size();
        for (int i = 0; i < size; i++){
            markerList.get(i).setVisible(flag);
        }
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Checking title of fence is existed.
    * Checking title of fence is blank.
    * */
    private boolean checkTitleFence(String title){
        if ((title.trim().length() == 0) || (strFencesList.contains(title))) {
            return false;
        } else {
            return true;
        }
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of Edit Mode.
    * Editing fences.
    * */
    private void clickEditButton(int pos){

        fenceLayout.setVisibility(View.VISIBLE);
        ibtnMapCenter.setVisibility(View.GONE);
        btnDelete.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        txvFenceMode.setText("EDIT MODE");
        editMode = true;

        curPosFence = pos;
        curTitleFence = strFencesList.get(pos);
        curCenFence = circlesList.get(pos).getCenter();
        curRadFence = circlesList.get(pos).getRadius();

        drawTempMarker(mMap, curCenFence, curTitleFence);
        drawTempFence(mMap, curCenFence, curRadFence);

        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(curCenFence));

        txtFenceTitle.setText(curTitleFence);
        int i = (int) curRadFence;
        skbFenceRadius.setProgress(i);
        String t = Integer.toString(i);
        txvFenceRadius.setText(t);

        // Moving CameraPosition to touched position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curCenFence, 14));

        showMarkers(true);
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of Save Button.
    * Saving fence. There are 2 mode: Add mode and Edit mode.
    * */
    private void clickSaveButton(){
        if (addMode){
            try {
                if ((checkTitleFence(txtFenceTitle.getText().toString()) == true)
                        && (skbFenceRadius.isEnabled())){

                    strFencesList.add(txtFenceTitle.getText().toString());

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(mTempMarker.getPosition())
                            .title(txtFenceTitle.getText().toString()));

                    Circle circle = mMap.addCircle(new CircleOptions()
                                    .center(mTempCircle.getCenter())
                                    .radius(mTempCircle.getRadius())
                                    .fillColor(0x20ff0000)
                                    .strokeColor(Color.TRANSPARENT)
                                    .strokeWidth(2)
                    );

                    markerList.add(marker);
                    circlesList.add(circle);

                    mTempMarker.remove();
                    mTempCircle.remove();

                    fenceLayout.setVisibility(View.GONE);
                    ibtnMapCenter.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.GONE);
                    addMode = false;

                    toast("Saved fence successfully.");
                    // Need to save GeoFence in database here.
                    // Need to draw all circle in List here.
                } else {
                    toast("The title is blank or already. Please type another title of the fence.");
                }
            } catch (Exception e){
                Log.e(TAG, "clickSaveButton: addMode is wrong.");
            }
        }

        if (editMode){
            try {
                if (txtFenceTitle.getText().toString().trim().contentEquals(curTitleFence.toString())){
                    saveEditMode();
                } else {
                    if (checkTitleFence(txtFenceTitle.getText().toString()) == true){
                        saveEditMode();
                    } else {
                        toast("The title is blank or already. Please type another title of the fence.");
                    }
                }

            } catch (Exception e){
                Log.e(TAG, "clickSaveButton: editMode is wrong.");
            }
        }

        showMarkers(false);
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of Save Button.
    * Saving fence in Edit mode.
    * */
    private void saveEditMode(){
        toast("saveEditMode");
        strFencesList.set(curPosFence, txtFenceTitle.getText().toString());
        markerList.get(curPosFence).setPosition(mTempMarker.getPosition());
        markerList.get(curPosFence).setTitle(mTempMarker.getTitle());
        circlesList.get(curPosFence).setCenter(mTempCircle.getCenter());
        circlesList.get(curPosFence).setRadius(mTempCircle.getRadius());

        mTempMarker.remove();
        mTempCircle.remove();

        fenceLayout.setVisibility(View.GONE);
        ibtnMapCenter.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        editMode = false;

        toast("Edited fence successfully.");
        // Need to save GeoFence in database here.
        // Need to draw all circle in List here.
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of Delete Button.
    * Deleting fence in Edit mode.
    * */
    private void clickDeleteButton() {
        try {
            AlertDialog.Builder b = new AlertDialog.Builder(MapActivity.this);

            b.setTitle("Delete fence");
            b.setMessage("Are you sure you want to delete this fence?");
            b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    mTempMarker.remove();
                    mTempCircle.remove();

                    markerList.get(curPosFence).remove();
                    circlesList.get(curPosFence).remove();

                    markerList.remove(curPosFence);
                    circlesList.remove(curPosFence);

                    strFencesList.remove(curPosFence);

                    fenceLayout.setVisibility(View.GONE);
                    ibtnMapCenter.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.VISIBLE);
                    btnCancel.setVisibility(View.GONE);
                    editMode = false;

                    toast("Deleted successfully.");

                    showMarkers(false);
                }});

            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.cancel();
                }

            });

            b.create().show();

        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of Clear Button.
    * Clearing content of fence title.
    * */
    private void clickClearButton(){
        txtFenceTitle.setText("");
        toast("Clear Button");
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of Cancel Button.
    * Canceling Add mode and Edit mode to go back the Map View.
    * */
    private void clickCancelButton(){
        fenceLayout.setVisibility(View.GONE);
        ibtnMapCenter.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        addMode = false;
        editMode = false;

        if (mTempMarker != null){
            mTempMarker.remove();
        }

        if (mTempCircle != null) {
            mTempCircle.remove();
        }

        btnCancel.setVisibility(View.GONE);
        showMarkers(false);
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of Radius Seek Bar.
    * Changing radius and title of fence when seek bar changes.
    * */
    private void changedSeekBar(int progress) {
        mTempCircle.setRadius((double) progress);
        txvFenceRadius.setText("Radius of fence : " + Integer.toString(progress));
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Implementing actions of clicking on map.
    * Set location to draw or edit fence.
    * */
    public void clickMap(LatLng latLng) {
        if (mTempMarker != null){
            mTempMarker.remove();
        }

        if (mTempCircle != null) {
            mTempCircle.remove();
        }
        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        // Moving CameraPosition to touched position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));

        if (addMode) {
            try {
                skbFenceRadius.setEnabled(true);

                drawTempMarker(mMap, latLng, TITLE_DEFAULT);
                drawTempFence(mMap, latLng, RADIUS_DEFAULT);

                int i = (int) mTempCircle.getRadius();
                skbFenceRadius.setProgress(i);
                String t = Integer.toString(i);
                txvFenceRadius.setText("Radius of fence : " + t);
            }
            catch (Exception e){
                Log.e(TAG,"clickMap: addMode is Wrong.");
            }
        }

        if (editMode) {
            try {
                drawTempMarker(mMap, latLng, curTitleFence);
                drawTempFence(mMap, latLng, curRadFence);

                int i = (int) mTempCircle.getRadius();
                skbFenceRadius.setProgress(i);
                String t = Integer.toString(i);
                txvFenceRadius.setText(t);
            }
            catch (Exception e){
                Log.e(TAG,"clickMap: editMode is Wrong.");
            }

        }
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Drawing temporary marker for adding and editing fence.
    * */
    private void drawTempMarker(GoogleMap mMap, LatLng latLng, String title){
        mTempMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }

    /*
    * Created by Nguyen Nam Cuong Tran
    * <p>
    * Drawing temporary circle for adding and editing fence.
    * */
    private void drawTempFence(GoogleMap mMap, LatLng latLng, double radius){
        mTempCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(0x70ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2));
    }

    // ============== End Function Brian Tran ====================


    @Override
    protected void onResume() {
        super.onResume();
        checkIfSetUpMapNeeded();
        countdownToNextUpdate(UPDATE_TIMER_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //patientMarker.remove();
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This checks if the map needs to be set up.
     */
    public void checkIfSetUpMapNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.

        try {
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.

                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
            }

            // Check if we were successful in obtaining the map.
            if (mMap == null || patientMarker == null) {
                setUpMap();
                mMap.setOnMapClickListener(this);
                mMap.setOnMapLongClickListener(this);
            }
        } catch (Exception e) {
            toast("Map could not be loaded.");
        }
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This method is used to set up the map.
     */
    private void setUpMap() {
        /* TODO Retrieve and set to the current location of the patient.*/

        getPatientLocation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showPatientOnMap,
                        throwable -> Log.e(TAG, throwable.getMessage()));
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * This method adds a marker for the patient on the map.
     */
    private void showPatientOnMap(LatLng patientCurrentLocation) {
        try {
            if (patientMarker == null) {
                //patientMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(X,Y)).title(getPatientName()));
                patientMarker = mMap.addMarker(new MarkerOptions().position(patientCurrentLocation).title(getPatientName()));
            } else {
                updatePatientLocationOnMap(patientMarker, patientCurrentLocation, false);
            }
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        } catch (Exception e) {
            toast("A Marker could not be placed.");
        }
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * This method updates the patient marker to a new location
     */
    public void updatePatientLocationOnMap(final Marker marker, final LatLng toPosition,
                                           final boolean hideMarker) {
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj = mMap.getProjection();
//        Point startPoint = proj.toScreenLocation(marker.getPosition());
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final long INTERPOLATION_DURATION = 500;
//        final Interpolator interpolator = new LinearInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed
//                        / INTERPOLATION_DURATION);
//                double lng = t * toPosition.longitude + (1 - t)
//                        * startLatLng.longitude;
//                double lat = t * toPosition.latitude + (1 - t)
//                        * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                } else {
//                    if (hideMarker) {
//                        marker.setVisible(false);
//                    } else {
//                        marker.setVisible(true);
//                    }
//                }
//            }
//        });

        if (hideMarker) {
            marker.setVisible(false);
        } else {
            marker.setVisible(true);
            marker.setPosition(new LatLng(toPosition.latitude, toPosition.longitude));
        }
    }

    /**
     * Created by Tristan Duboi
     * <p>
     * Countdown till the next patient location update.
     */
    private void countdownToNextUpdate(long timeBetweenUpdates) {
        subscription = getPatientLocation()
                .delay(timeBetweenUpdates, TimeUnit.MILLISECONDS)
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(latLng -> {
//                    toast("Update");
                    updatePatientLocationOnMap(patientMarker, latLng, false);
                });

    }

    /**
     * Created by Tristan Dubois
     * <p>
     * His activates when the center button is pressed and centers the map on the patient.
     */
    public void centerPatientMarker(View view) {
        try {
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        } catch (Exception e) {

            toast("Failed to center the map on the user.");

        }
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This gets the current location of the patient from the database.
     */
    private Observable<LatLng> getPatientLocation() {
        return PatientLocation.getLatestPatientLocation(patient)
                .filter(patientLocation1 -> patientLocation1 != null)
                .map(PatientLocation::getLatLng);
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This gets the name of the patient from the database.
     */
    private String getPatientName() {
        patientMarkerName = "Bob";
        return patientMarkerName;
    }

    private void toast(String toastMessage) {
        try {
            Toast.makeText(MapActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception toast) {
        }
    }
}