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
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CircleOptions;

import java.util.ArrayList;
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

    private ArrayList<String> strFencesList;
    private ArrayList<Circle> circlesList;
    private ArrayList<Marker> markerList;

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

    @Override
    public void onMapClick(LatLng latLng) {
        clickMap(latLng);
    }

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

    // Initializing faked geofences list for test functions
    private void initFencesList(){
        strFencesList = new ArrayList<String>();
        markerList = new ArrayList<Marker>();
        circlesList = new ArrayList<Circle>();
    }

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

    private void showMarkers(boolean flag){
        int size = markerList.size();
        for (int i = 0; i < size; i++){
            markerList.get(i).setVisible(flag);
        }
    }

    private boolean checkTitleFence(String title){
        if ((title.trim().length() == 0) || (strFencesList.contains(title))) {
            return false;
        } else {
            return true;
        }
    }

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
                if (txtFenceTitle.getText().toString() == curTitleFence) {

                    saveEditMode();
                    toast("txtFenceTitle.getText().toString() == curTitleFence");
                } else {
                    toast("txtFenceTitle.getText().toString() == curTitleFence");
                    if (checkTitleFence(txtFenceTitle.getText().toString()) == true) {
                        toast("checkTitleFence(txtFenceTitle.getText().toString()) == true");
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

    private void clickClearButton(){
        txtFenceTitle.setText("");
        toast("Clear Button");
    }

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

    private void changedSeekBar(int progress) {
        mTempCircle.setRadius((double) progress);
        txvFenceRadius.setText("Radius of fence : " + Integer.toString(progress));
    }

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

    private void drawTempMarker(GoogleMap mMap, LatLng latLng, String title){
        mTempMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
    }

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