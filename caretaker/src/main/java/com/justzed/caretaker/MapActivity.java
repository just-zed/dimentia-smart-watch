package com.justzed.caretaker;

import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    private Button btnSelect;
    private Button btnAdd;
    private Button btnSave;
    private Button btnDelete;
    private Button btnClear;
    private Button btnCancel;
    private TextView txvFenceMode;
    private EditText txtFenceTitle;
    private TextView txvFenceRadius;
    private SeekBar skbFenceRadius;
    private ListView ltvFencesList;

    private boolean addMode = false;
    private boolean editMode = false;
    private boolean selectMode = false;
    private boolean deleteMode = false;

    private Circle mCircle;
    private Marker mMarker;


    private ArrayAdapter<String> arrFenceListAdapter;
    //private List<FenceCircle> fencesList;
    //private ArrayAdapter<FenceCircle> arrFenceListAdapter;

    private final static double RADIUS_DEFAULT = 200.0;
    private final static int RADIUS_MAX = 1000;

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
                case R.id.select_button:
                    //do stuff
                    clickSelectButton();
                    break;
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
                    clickClearButton();
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

    OnItemLongClickListener ltvItemLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (selectMode){
                //String title = fencesList.get(position).getTitle();
                //LatLng latLng= fencesList.get(position).getLatLng();
                //double radius = fencesList.get(position).getRadius();

                ltvFencesList.setVisibility(View.GONE);

                selectMode = false;

                btnSelect.setEnabled(true);
                btnAdd.setEnabled(true);
                btnDelete.setEnabled(true);
                btnCancel.setEnabled(false);

                //drawFence(mMap, latLng, radius);
            }

            if (deleteMode){

            }
            return false;
        }
    };

    @Override
    public void onMapClick(LatLng latLng) {
        clickMap(latLng);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        LatLng center = mCircle.getCenter();
        double radius = mCircle.getRadius();
        float[] distance = new float[1];
        Location.distanceBetween(latLng.latitude, latLng.longitude, center.latitude, center.longitude, distance);
        boolean clicked = distance[0] < radius;
        if (clicked){
            clickEditButton();
        }
    }

    private void initFenceActivitySetup() {

        ibtnMapCenter = (ImageButton) findViewById(R.id.mapCenterButton);

        fenceLayout = (LinearLayout) findViewById(R.id.fence_layout);
        fenceLayout.setVisibility(View.GONE);

        fenceModeLayout = (LinearLayout) findViewById(R.id.fence_mode_layout);
        fenceModeLayout.setVisibility(View.VISIBLE);

        btnSelect = (Button) findViewById(R.id.select_button);
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

        ltvFencesList = (ListView) findViewById(R.id.fences_List_Layout);
        //ltvFencesList.setVisibility(View.VISIBLE);

        btnSelect.setOnClickListener(btnClickListener);
        btnAdd.setOnClickListener(btnClickListener);
        btnSave.setOnClickListener(btnClickListener);
        btnDelete.setOnClickListener(btnClickListener);
        btnClear.setOnClickListener(btnClickListener);
        btnCancel.setOnClickListener(btnClickListener);
        skbFenceRadius.setOnSeekBarChangeListener(skbChangeListener);

        ltvFencesList.setOnItemLongClickListener(ltvItemLongClickListener);
    }

    // Initializing faked geofences list for test functions
    private void initFencesList(){

        List<String> arr = new ArrayList<>();

        ArrayAdapter<String> adapter=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, arr);
        ltvFencesList.setAdapter(adapter);

        arr.add("AAA");
        arr.add("BBB");

        adapter.notifyDataSetChanged();


        //fencesList = new ArrayList<FenceCircle>();
        //arrFenceListAdapter = new ArrayAdapter<FenceCircle>(this, android.R.layout.simple_list_item_1);
        //ltvFencesList.setAdapter(arrFenceListAdapter);

        //fencesList.add(new FenceCircle("Number 1", new LatLng(-27.592782, 153.064673),200.0));
        //fencesList.add(new FenceCircle("Number 2", new LatLng(-27.607956, 153.061025), 200.0));
        //fencesList.add(new FenceCircle("Number 3", new LatLng(-27.603906, 153.104520), 200.0));
        //fencesList.add(new FenceCircle("Number 4", new LatLng(-27.589910, 153.101752), 200.0));
        //fencesList.add(new FenceCircle("Number 5", new LatLng(-27.597213, 153.084070), 200.0));
        //arrFenceListAdapter.notifyDataSetChanged();
    }

    private void clickSelectButton(){
        ltvFencesList.setVisibility(View.VISIBLE);
        //ltvFencesList.setEnabled(true);
        btnCancel.setEnabled(true);
        selectMode = true;

        btnSelect.setEnabled(false);
        btnAdd.setEnabled(false);
        btnDelete.setEnabled(false);

        toast("Select Button");
    }

    private void clickAddButton(){
        fenceLayout.setVisibility(View.VISIBLE);
        ibtnMapCenter.setVisibility(View.GONE);
        fenceModeLayout.setVisibility(View.GONE);
        txvFenceMode.setText("ADD MODE");
        addMode = true;
    }

    private void clickEditButton(){
        fenceLayout.setVisibility(View.VISIBLE);
        ibtnMapCenter.setVisibility(View.GONE);
        fenceModeLayout.setVisibility(View.GONE);
        txvFenceMode.setText("EDIT MODE");
        editMode = true;
    }

    private void clickSaveButton(){
        if (addMode){
            mMarker.setVisible(false);
            fenceLayout.setVisibility(View.GONE);
            ibtnMapCenter.setVisibility(View.VISIBLE);
            fenceModeLayout.setVisibility(View.VISIBLE);
            txvFenceMode.setText("");
            addMode = false;
            toast("Saved fence successfully.");
            // Need to save GeoFence in database here.
            // Need to draw all circle in List here.
        }

        if (editMode){
            fenceLayout.setVisibility(View.GONE);
            ibtnMapCenter.setVisibility(View.VISIBLE);
            fenceModeLayout.setVisibility(View.VISIBLE);
            txvFenceMode.setText("");
            editMode = false;
            toast("Edited fence successfully.");
            // Need to save GeoFence in database here.
            // Need to draw all circle in List here.
        }
    }

    private void clickDeleteButton() {
        toast("Delete Button");
    }

    private void clickClearButton(){
        txtFenceTitle.setText("");
        toast("Clear Button");
    }

    private void changedSeekBar(int progress) {
        mCircle.setRadius((double) progress);
        txvFenceRadius.setText(Integer.toString(progress));
    }

    public void clickMap(LatLng latLng) {
        if (addMode) {
            // Clears the previously touched position
            mMap.clear();

            // Animating to the touched position
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
            drawFence(mMap, latLng, 500.0);

            int i = (int)mCircle.getRadius();
            skbFenceRadius.setProgress(i);
            String t = Integer.toString(i);
            txvFenceRadius.setText(t);

            // Moving CameraPosition to touched position
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    // This method just draws the circle and marker.
    // All values of circle and marker are gotten from fence.
    // Only one circle shows on Map View.
    private void drawFence(GoogleMap mMap, LatLng latLng, double radius){
        // Init value of raDius if there is no fence
        if (radius <= 0){
            radius = RADIUS_DEFAULT;
        }

        mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng));

        mCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .fillColor(0x40ff0000)
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
                //mFenceCircleList = new ArrayList<FenceCircle>();
                //mFenceCircle = new FenceCircle();
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
                patientMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(X,Y)).title(getPatientName()));
                //patientMarker = mMap.addMarker(new MarkerOptions().position(patientCurrentLocation).title(getPatientName()));
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