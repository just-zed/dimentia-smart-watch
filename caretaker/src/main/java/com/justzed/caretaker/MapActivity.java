package com.justzed.caretaker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
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

import java.util.HashMap;
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

    Button btnAdd;
    Button btnSave;
    Button btnDelete;
    TextView txvFenceMode;
    EditText txtFenceTitle;
    SeekBar skbFenceRadius;

    ImageButton ibtnMapCenter;
    boolean addMode = false;
    boolean editMode = false;
    Map<String, Circle> fences;
    Circle circle;
    Circle mCircle;
    private Marker mMarker;

    private final static double RADIUS_DEFAULT = 200.0;

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

        fences = new HashMap<String, Circle>();
        ibtnMapCenter = (ImageButton) findViewById(R.id.mapCenterButton);
        setupButton();
    }

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
                default: break;
            }
        }
    };


        @Override
        public void onMapClick(LatLng latLng) {
            toast("onMapClick OK");
            clickMap(latLng);
        }


        @Override
        public void onMapLongClick(LatLng latLng) {
            toggleEditMode(true);
        }

    //init fences
    private void initFences(){
    }

    private void setupButton() {

        btnAdd = (Button) findViewById(R.id.add_button);
        btnSave = (Button) findViewById(R.id.save_button);
        btnDelete = (Button) findViewById(R.id.delete_button);
        txvFenceMode = (TextView) findViewById(R.id.fence_mode_text_view);
        txtFenceTitle = (EditText) findViewById(R.id.fence_title_edit_text);
        skbFenceRadius = (SeekBar) findViewById(R.id.fence_seek_bar);


        btnAdd.setOnClickListener(btnClickListener);
        btnSave.setOnClickListener(btnClickListener);
        btnDelete.setOnClickListener(btnClickListener);

    }

    private void clickAddButton(){
        toggleAddMode(true);
    }

    private void clickSaveButton(){

    }

    private void clickDeleteButton(){

    }

    private void toggleEditMode(boolean show) {
        LinearLayout linearLayout;
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        linearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        ibtnMapCenter.setVisibility(show ? View.GONE : View.VISIBLE);
        btnAdd.setVisibility(show ? View.GONE : View.VISIBLE);

    }

    private void toggleAddMode(boolean show) {
        LinearLayout linearLayout;
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        linearLayout.setVisibility(show ? View.VISIBLE : View.GONE);

        ibtnMapCenter.setVisibility(show ? View.GONE : View.VISIBLE);
        btnAdd.setVisibility(show ? View.GONE : View.VISIBLE);
        addMode = true;
        txvFenceMode.setText("ADD MODE");

    }

    public void clickMap(LatLng latLng) {
        if (addMode) {
            // Clears the previously touched position
            mMap.clear();

            // Animating to the touched position
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            // Placing a marker on the touched position
            drawMarker(mMap, latLng);
            drawCircle(mMap, latLng, 500.0);

            // Moving CameraPosition to touched position
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        }
    }

    // This method just draws a marker.
    // All values of marker are gotten from fence.
    // Only one circle shows on Map View.
    private void drawMarker(GoogleMap mMap, LatLng latLng){
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng));
    }

    // This method just draws the circle.
    // All values of circle are gotten from fence.
    // Only one circle shows on Map View.
    private void drawCircle(GoogleMap mMap, LatLng latLng, double raDius){
        // Init value of raDius if there is no fence
        if (raDius <= 0){
            raDius = RADIUS_DEFAULT;
        }

        mCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(raDius)
                .fillColor(0x40ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2));
    }


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
        patientMarker.remove();
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