package com.justzed.caretaker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.justzed.common.model.PatientFence;
import com.justzed.common.model.PatientLocation;
import com.justzed.common.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom;

/**
 * Created by Tristan on 21/08/2015.
 * <p>
 * This class uses the google map API to display the location of the patient device by getting data from the parse.com database.
 */
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


    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to set ClickListener for buttons.
     */
    OnClickListener btnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
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
                default:
                    break;
            }
        }
    };

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to set ChangeListener for Seek Bar (adjusting radius of fence).
     */
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

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to Set MapClickListener for Adding and Editing Fences.
     *
     * @param latLng The location which user clicked on the map.
     */
    @Override
    public void onMapClick(LatLng latLng) {
        clickMap(latLng);
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to set MapLongClickListener for going to
     * EDIT AND DELETE FENCE MODE.
     *
     * @param latLng The location which user clicked on the map.
     */
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (!editMode) {
            int pos = posFenceWhenClicked(latLng);
            if (pos != -1) {
                clickEditButton(pos);
            }
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to recognise the position of the fence
     * when it is clicked.
     *
     * @param latLng The location which user clicked on the map.
     * @return int the position of the fence in circleList.
     */
    private int posFenceWhenClicked(LatLng latLng) {
        int pos = -1;
        int size = circlesList.size();
        for (int i = 0; i < size; i++) {
            LatLng center = circlesList.get(i).getCenter();
            double radius = circlesList.get(i).getRadius();
            float[] distance = new float[1];
            Location.distanceBetween(latLng.latitude, latLng.longitude, center.latitude, center.longitude, distance);

            boolean clicked = distance[0] < radius;
            if (clicked) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to setup Geofences activity on caretaker app.
     */
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

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to initialise some lists for needed fence functions
     * and show the existing Geofences which are in database on the map.
     */
    private void initFencesList() {
        strFencesList = new ArrayList<String>();
        markerList = new ArrayList<Marker>();
        circlesList = new ArrayList<Circle>();
        patientFenceList = new ArrayList<PatientFence>();

        strFencesList.clear();
        markerList.clear();
        circlesList.clear();
        patientFenceList.clear();

        PatientFence.getPatientFences(patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientFences -> {
                    patientFenceList = patientFences;
                    //loop through the list and add markers and circles...
                    int size = patientFenceList.size();
                    for (int i = 0; i < size; i++) {
                        String title = patientFenceList.get(i).getDescription();
                        LatLng center = patientFenceList.get(i).getCenter();
                        double radius = patientFenceList.get(i).getRadius();

                        strFencesList.add(title);
                        markerList.add(drawMarker(mMap, center, title));
                        circlesList.add(drawCircle(mMap, center, radius));
                    }
                }, throwable -> {
                    Log.e(TAG, throwable.getMessage());
                });
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to draw a marker on the map.
     *
     * @param map    An object of Google map.
     * @param center The location of the marker on the map.
     * @param title  The title of the marker.
     * @return Marker A marker on the map.
     */
    private Marker drawMarker(GoogleMap map, LatLng center, String title) {
        return map.addMarker(new MarkerOptions()
                        .position(center)
                        .title(title)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        .visible(false)
        );
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to draw a circle on the map.
     *
     * @param map    An object of Google map.
     * @param center The center of the circle on the map.
     * @param radius The radius of the circle.
     * @return Circle A circle on the map.
     */
    private Circle drawCircle(GoogleMap map, LatLng center, double radius) {
        return map.addCircle(new CircleOptions()
                .center(center)
                .radius(radius)
                .fillColor(0x20ff0000)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2));
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of clicking Add button.
     * Going to Add mode.
     */
    private void clickAddButton() {
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
        showMarkers(false);
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to show or hide all markers on the Map.
     *
     * @param flag True (Show markers) or False (Hide markers).
     */
    private void showMarkers(boolean flag) {
        int size = markerList.size();
        for (int i = 0; i < size; i++) {
            markerList.get(i).setVisible(flag);
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to check title of fence is blank.
     *
     * @param title The title of the fence on the map.
     * @return boolean True (Not blank) or False (Blank).
     */
    private boolean checkTitleFence(String title) {
        if (title.trim().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Edit Mode.
     * Editing fences.
     *
     * @param pos The position of the fence in Lists.
     */
    private void clickEditButton(int pos) {

        fenceLayout.setVisibility(View.VISIBLE);
        ibtnMapCenter.setVisibility(View.GONE);
        btnDelete.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        txvFenceMode.setText("EDIT MODE");
        editMode = true;
        showMarkers(false);

        curPosFence = pos;
        curTitleFence = strFencesList.get(pos);
        curCenFence = circlesList.get(pos).getCenter();
        curRadFence = circlesList.get(pos).getRadius();

        markerList.get(pos).setVisible(true);

        drawTempMarker(mMap, curCenFence, curTitleFence);
        drawTempFence(mMap, curCenFence, curRadFence);

        txtFenceTitle.setText(curTitleFence);
        int i = (int) curRadFence;
        skbFenceRadius.setProgress(i);
        String t = Integer.toString(i);
        txvFenceRadius.setText("Radius of fence : " + t + " meters.");
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Save Button.
     * Saving fence. There are 2 mode: Add mode and Edit mode.
     */
    private void clickSaveButton() {
        if (addMode) {
            try {
                if ((checkTitleFence(txtFenceTitle.getText().toString()) == true)
                        && (skbFenceRadius.isEnabled())) {

                    String title = txtFenceTitle.getText().toString();
                    LatLng center = mTempCircle.getCenter();
                    double radius = mTempCircle.getRadius();

                    new PatientFence(patient, mTempCircle.getCenter(),
                            mTempCircle.getRadius(),
                            txtFenceTitle.getText().toString())
                            .save()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    patientFence -> {
                                        // updates the object in the list
                                        patientFenceList.add(patientFence);
                                        strFencesList.add(patientFence.getDescription().toString());
                                        markerList.add(drawMarker(mMap, patientFence.getCenter(),
                                                patientFence.getDescription().toString()));
                                        circlesList.add(drawCircle(mMap, patientFence.getCenter(),
                                                patientFence.getRadius()));

                                        mTempMarker.remove();
                                        mTempCircle.remove();

                                        fenceLayout.setVisibility(View.GONE);
                                        ibtnMapCenter.setVisibility(View.VISIBLE);
                                        btnAdd.setVisibility(View.VISIBLE);
                                        btnCancel.setVisibility(View.GONE);
                                        addMode = false;

                                        toast("Saved fence successfully.");
                                    },
                                    throwable -> {
                                        Log.e(TAG, throwable.getMessage());
                                    }
                            );
                } else {
                    toast("The title is blank. Please type the title of the fence.");
                }
            } catch (Exception e) {
                Log.e(TAG, "clickSaveButton: addMode is wrong.");
            }
        }

        if (editMode) {
            try {
                if (checkTitleFence(txtFenceTitle.getText().toString()) == true) {
                    saveEditMode();
                } else {
                    toast("The title is blank. Please type the title of the fence.");
                }
            } catch (Exception e) {
                Log.e(TAG, "clickSaveButton: editMode is wrong.");
            }
        }
        showMarkers(false);
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Save Button.
     * Saving fence in Edit mode.
     */
    private void saveEditMode() {
        //toast("saveEditMode");

        String title = txtFenceTitle.getText().toString();
        LatLng center = mTempCircle.getCenter();
        double radius = mTempCircle.getRadius();

        PatientFence fence = patientFenceList.get(curPosFence);

        fence.setDescription(title);
        fence.setCenter(center);
        fence.setRadius(radius);
        fence.save()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        patientFence -> {
                            // updates the object in the list
                            strFencesList.set(curPosFence, patientFence.getDescription());
                            markerList.get(curPosFence).setPosition(patientFence.getCenter());
                            markerList.get(curPosFence).setTitle(patientFence.getDescription());
                            circlesList.get(curPosFence).setCenter(patientFence.getCenter());
                            circlesList.get(curPosFence).setRadius(patientFence.getRadius());

                            patientFenceList.set(curPosFence, patientFence);

                            mTempMarker.remove();
                            mTempCircle.remove();

                            fenceLayout.setVisibility(View.GONE);
                            ibtnMapCenter.setVisibility(View.VISIBLE);
                            btnAdd.setVisibility(View.VISIBLE);
                            btnCancel.setVisibility(View.GONE);
                            editMode = false;

                            toast("Edited fence successfully.");
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        }
                );
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Delete Button.
     * Deleting fence in Edit mode.
     */
    private void clickDeleteButton() {
        try {
            AlertDialog.Builder b = new AlertDialog.Builder(MapActivity.this);

            b.setTitle("Delete fence");
            b.setMessage("Are you sure you want to delete this fence?");
            b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PatientFence fence = patientFenceList.get(curPosFence);
                    fence.delete()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    patientFence -> {
                                        if (patientFence == null) {
                                            mTempMarker.remove();
                                            mTempCircle.remove();

                                            markerList.get(curPosFence).remove();
                                            circlesList.get(curPosFence).remove();

                                            markerList.remove(curPosFence);
                                            circlesList.remove(curPosFence);

                                            strFencesList.remove(curPosFence);

                                            patientFenceList.remove(curPosFence);

                                            fenceLayout.setVisibility(View.GONE);
                                            ibtnMapCenter.setVisibility(View.VISIBLE);
                                            btnAdd.setVisibility(View.VISIBLE);
                                            btnCancel.setVisibility(View.GONE);
                                            editMode = false;

                                            toast("Deleted successfully.");

                                            showMarkers(false);
                                        }
                                    },
                                    throwable -> {
                                        Log.e(TAG, throwable.getMessage());
                                    }
                            );
                }
            });

            b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            b.create().show();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Clear Button.
     * Clearing textbox's content of the fence title.
     */
    private void clickClearButton() {
        txtFenceTitle.setText("");
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Cancel Button.
     * Canceling Add mode and Edit mode to go back the Map View.
     */
    private void clickCancelButton() {
        fenceLayout.setVisibility(View.GONE);
        ibtnMapCenter.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        addMode = false;
        editMode = false;

        if (mTempMarker != null) {
            mTempMarker.remove();
        }

        if (mTempCircle != null) {
            mTempCircle.remove();
        }

        btnCancel.setVisibility(View.GONE);
        showMarkers(false);
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Radius Seek Bar.
     * Changing radius and title of fence when seek bar changes.
     *
     * @param progress The value of Seekbar progress.
     */
    private void changedSeekBar(int progress) {
        if (progress >= 0) {
            mTempCircle.setRadius((double) progress);
            txvFenceRadius.setText("Radius of fence : " + Integer.toString(progress)
                    + " meters.");
        } else {
            mTempCircle.setRadius(0.0);
            txvFenceRadius.setText("Radius of fence : 0 meters.");
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of clicking on map.
     * Set location to draw or edit fence.
     *
     * @param latLng The location which user clicked on the map.
     */
    public void clickMap(LatLng latLng) {
        if (mTempMarker != null) {
            mTempMarker.remove();
        }

        if (mTempCircle != null) {
            mTempCircle.remove();
        }

        if (!addMode && !editMode) {
            int pos = posFenceWhenClicked(latLng);
            if (pos != -1) {
                showMarkers(false);
                markerList.get(pos).setVisible(true);
            }
        }

        if (addMode) {
            try {
                skbFenceRadius.setEnabled(true);

                drawTempMarker(mMap, latLng, TITLE_DEFAULT);
                drawTempFence(mMap, latLng, RADIUS_DEFAULT);

                int i = (int) mTempCircle.getRadius();
                skbFenceRadius.setProgress(i);
                String t = Integer.toString(i);
                txvFenceRadius.setText("Radius of fence : " + t + " meters.");
            } catch (Exception e) {
                Log.e(TAG, "clickMap: addMode is Wrong.");
            }
        }

        if (editMode) {
            try {
                drawTempMarker(mMap, latLng, curTitleFence);
                drawTempFence(mMap, latLng, curRadFence);

                int i = (int) mTempCircle.getRadius();
                skbFenceRadius.setProgress(i);
                String t = Integer.toString(i);
                txvFenceRadius.setText("Radius of fence : " + t + " meters.");
            } catch (Exception e) {
                Log.e(TAG, "clickMap: editMode is Wrong.");
            }

        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran
     * This method is used to draw temporary marker for adding and editing fence.
     *
     * @param mMap   An object of Google map.
     * @param latLng The center of the temporary marker on the map.
     * @param title  The title of the temporary marker.
     */
    private void drawTempMarker(GoogleMap mMap, LatLng latLng, String title) {
        mTempMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    /**
     * Created by Nguyen Nam Cuong Tran
     * This method is used to draw temporary circle for adding and editing fence.
     *
     * @param mMap   An object of Google map.
     * @param latLng The center of the temporary circle on the map.
     * @param radius The radius of the temporary circle.
     */
    private void drawTempFence(GoogleMap mMap, LatLng latLng, double radius) {
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
                patientMarker = mMap.addMarker(new MarkerOptions()
                                .position(patientCurrentLocation)
                                .title(getPatientName())
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                );
            } else {
                updatePatientLocationOnMap(patientMarker, patientCurrentLocation, false);
            }
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        } catch (Exception e) {
            toast("A Marker could not be placed.");
        }
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This method updates the patient marker to a new location
     */
    public void updatePatientLocationOnMap(final Marker marker, final LatLng toPosition,
                                           final boolean hideMarker) {
        if (hideMarker) {
            marker.setVisible(false);
        } else {
            marker.setVisible(true);
            marker.setPosition(new LatLng(toPosition.latitude, toPosition.longitude));
        }
    }

    /**
     * Created by Tristan Dubois
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

    /**
     * Created by Tristan Dubois
     * <p>
     * This is used to create a toast message using a string.
     */
    private void toast(String toastMessage) {
        try {
            Toast.makeText(MapActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
        } catch (Exception toast) {
        }
    }
}