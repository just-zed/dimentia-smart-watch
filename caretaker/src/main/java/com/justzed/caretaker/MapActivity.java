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
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.Calendar;
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
    private Button btnAddAdvance;
    private Button btnSave;
    private Button btnReset;
    private Button btnDelete;
    private Button btnClear;
    private Button btnCancel;
    private TextView txvFenceMode;
    private EditText txtFenceTitle;
    private TextView txvFenceRadius;
    private SeekBar skbFenceRadius;
    private ToggleButton togButton;


    private boolean addMode = false;
    private boolean addAdvanceMode = false;
    private boolean editMode = false;
    private boolean editAdvanceMode = false;

    private boolean tempAdvanceCircle1 = false;
    private boolean tempAdvanceCircle2 = false;
    private Circle mTempCircle1;
    private Circle mTempCircle2;
    private Marker mTempMarker1;
    private ArrayList<String> strAdvFencesList;
    private ArrayList<Marker> advMarkerList;
    private ArrayList<ArrayList<Circle>> advCircleList;
    private ArrayList<ArrayList<PatientFence>> patientAdvFenceList;
    private ArrayList<Circle> tempAdvCircleList;
    private boolean advEditFlag = false;
    private LatLng curCenFence1;
    private LatLng curCenFence2;
    private long largestGroupID = 0;

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

    // ===================== End Brian Tran ==================

    //Constants

    private static final int UPDATE_TIMER_NORMAL = 1000;
    private Subscription subscription;
    //shirin
    TextView textView;
    TextView textView2;
    TextView textView3;
    NumberPicker numberPicker;
    NumberPicker numberPicker2;

    Calendar Calendar;

    public MapActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle data = getIntent().getExtras();
        patient = data.getParcelable(Person.PARCELABLE_KEY);

        mMap = null;
        setContentView(R.layout.activity_map);

        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);

        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker2 = (NumberPicker) findViewById(R.id.numberPicker2);

        numberPicker.setFormatter(value -> String.format("%02d", value));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(23);
        numberPicker.setWrapSelectorWheel(true);

        numberPicker2.setFormatter(value -> String.format("%02d", value));
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setWrapSelectorWheel(true);


        textView.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        togButton = (ToggleButton) findViewById(R.id.toggleButton);


        initFenceActivitySetup();
        initFencesList();
    }

    //Shirin


    public void changeStates(View view) {

        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            textView.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.VISIBLE);
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker2.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            numberPicker.setVisibility(View.INVISIBLE);
            numberPicker2.setVisibility(View.INVISIBLE);
            numberPicker.setValue(0);
            numberPicker2.setValue(0);
        }
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
                case R.id.add_advance_button:
                    //do stuff
                    clickAddAdvanceButton();
                    break;
                case R.id.save_button:
                    //do stuff
                    clickSaveButton();
                    break;
                case R.id.reset_button:
                    //do stuff
                    clickResetButton();
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
        if ((!editMode) && (!editAdvanceMode)) {
            int pos = posFenceWhenClicked(latLng);
            if (pos > -1) {
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

        // Search in normal fences.
        int size = circlesList.size();
        for (int i = 0; i < size; i++) {
            LatLng center = circlesList.get(i).getCenter();
            double radius = circlesList.get(i).getRadius();
            float[] distance = new float[1];
            Location.distanceBetween(latLng.latitude, latLng.longitude, center.latitude,
                    center.longitude, distance);

            boolean clicked = distance[0] < radius;
            if (clicked) {
                pos = i;
                advEditFlag = false;
                return pos;
            }
        }

        // Search in advance fences.
        size = advCircleList.size();
        for (int i = 0; i < size; i++) {
            int size1 = advCircleList.get(i).size();
            for (int j = 0; j < size1; j++) {
                LatLng center = advCircleList.get(i).get(j).getCenter();
                double radius = advCircleList.get(i).get(j).getRadius();
                float[] distance = new float[1];
                Location.distanceBetween(latLng.latitude, latLng.longitude, center.latitude,
                        center.longitude, distance);

                boolean clicked = distance[0] < radius;
                if (clicked) {
                    pos = i;
                    advEditFlag = true;
                    return pos;
                }
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
        btnAddAdvance = (Button) findViewById(R.id.add_advance_button);
        btnSave = (Button) findViewById(R.id.save_button);
        btnReset = (Button) findViewById(R.id.reset_button);
        btnDelete = (Button) findViewById(R.id.delete_button);
        btnClear = (Button) findViewById(R.id.clear_button);
        btnCancel = (Button) findViewById(R.id.cancel_button);
        txvFenceMode = (TextView) findViewById(R.id.fence_mode_text_view);
        txtFenceTitle = (EditText) findViewById(R.id.fence_title_edit_text);
        txvFenceRadius = (TextView) findViewById(R.id.fence_radius_text_view);
        skbFenceRadius = (SeekBar) findViewById(R.id.fence_seek_bar);
        skbFenceRadius.setMax(RADIUS_MAX);
//shirin
        btnAdd.setOnClickListener(btnClickListener);
        btnAddAdvance.setOnClickListener(btnClickListener);
        btnSave.setOnClickListener(btnClickListener);
        btnReset.setOnClickListener(btnClickListener);
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

        advCircleList = new ArrayList<ArrayList<Circle>>();
        patientAdvFenceList = new ArrayList<ArrayList<PatientFence>>();
        tempAdvCircleList = new ArrayList<Circle>();
        strAdvFencesList = new ArrayList<String>();
        advMarkerList = new ArrayList<Marker>();

        PatientFence.findPatientFences(patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientFences -> {
                    ArrayList<PatientFence> prePatientAdvFenceList = new ArrayList<PatientFence>();
                    int size = patientFences.size();
                    for (int i = 0; i < size; i++) {
                        if (patientFences.get(i).getGroupId() == 0) {
                            patientFenceList.add(patientFences.get(i));
                        } else {
                            prePatientAdvFenceList.add(patientFences.get(i));
                        }
                    }

                    // Basic Fences
                    size = patientFenceList.size();
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            String title = patientFenceList.get(i).getDescription();
                            LatLng center = patientFenceList.get(i).getCenter();
                            double radius = patientFenceList.get(i).getRadius();

                            strFencesList.add(title);
                            markerList.add(drawMarker(mMap, center, title));
                            circlesList.add(drawCircle(mMap, center, radius));
                        }
                    }

                    // Advance Fences
                    size = prePatientAdvFenceList.size();
                    if (size > 0) {
                        long curTempGroupID = prePatientAdvFenceList.get(0).getGroupId();
                        ArrayList<PatientFence> childPatientAdvFenceList = new ArrayList<PatientFence>();

                        for (int i = 0; i < size; i++) {
                            if (curTempGroupID > largestGroupID) {
                                largestGroupID = curTempGroupID;
                            }

                            if (curTempGroupID == prePatientAdvFenceList.get(i).getGroupId()) {
                                childPatientAdvFenceList.add(prePatientAdvFenceList.get(i));
                                if (i == (size - 1)) {
                                    patientAdvFenceList.add(new ArrayList<PatientFence>(childPatientAdvFenceList));
                                }
                            } else {
                                curTempGroupID = prePatientAdvFenceList.get(i).getGroupId();
                                patientAdvFenceList.add(new ArrayList<PatientFence>(childPatientAdvFenceList));
                                childPatientAdvFenceList.clear();
                                childPatientAdvFenceList.add(prePatientAdvFenceList.get(i));
                            }
                        }

                        size = patientAdvFenceList.size();
                        advCircleList.ensureCapacity(size);

                        for (int i = 0; i < size; i++) {
                            int size1 = patientAdvFenceList.get(i).size();
                            String title = patientAdvFenceList.get(i).get(0).getDescription();
                            LatLng center = patientAdvFenceList.get(i).get(0).getCenter();
                            double radius = patientAdvFenceList.get(i).get(0).getRadius();

                            strAdvFencesList.add(title);
                            advMarkerList.add(drawMarker(mMap, center, title));

                            ArrayList<Circle> circleArrayList = new ArrayList<Circle>();
                            circleArrayList.ensureCapacity(size1);

                            for (int j = 0; j < size1; j++) {
                                circleArrayList.add(drawCircle(mMap,
                                        patientAdvFenceList.get(i).get(j).getCenter(),
                                        radius));
                            }
                            advCircleList.add(new ArrayList<Circle>(circleArrayList));
                        }
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
        btnAddAdvance.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        txvFenceMode.setText(R.string.add_fence_title);
        txtFenceTitle.setText("");
        skbFenceRadius.setProgress(RADIUS_MIN);
        skbFenceRadius.setEnabled(false);
        addMode = true;
        showMarkers(false);
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of clicking Add Advance button.
     * Going to Add Advance mode.
     */
    private void clickAddAdvanceButton() {
        fenceLayout.setVisibility(View.VISIBLE);
        ibtnMapCenter.setVisibility(View.GONE);
        btnReset.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.GONE);
        btnAdd.setVisibility(View.GONE);
        btnAddAdvance.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);
        txvFenceMode.setText(R.string.add_advance_fence_title);
        txtFenceTitle.setText("");
        skbFenceRadius.setProgress(RADIUS_MIN);
        skbFenceRadius.setEnabled(false);
        addAdvanceMode = true;
        showMarkers(false);

        btnSave.setEnabled(false);
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

        size = advMarkerList.size();
        for (int i = 0; i < size; i++) {
            advMarkerList.get(i).setVisible(flag);
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
        return title.trim().length() > 0;
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
        btnAddAdvance.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);

        showMarkers(false);

        curPosFence = pos;

        if (!advEditFlag) {
            skbFenceRadius.setEnabled(true);
            editMode = true;
            txvFenceMode.setText(R.string.edit_fence_title);

            curTitleFence = strFencesList.get(pos);
            curCenFence = circlesList.get(pos).getCenter();
            curRadFence = circlesList.get(pos).getRadius();

            markerList.get(pos).setVisible(true);

            drawTempMarker(mMap, curCenFence, curTitleFence);
            drawTempFence(mMap, curCenFence, curRadFence);
        } else {
            skbFenceRadius.setEnabled(false);
            editAdvanceMode = true;
            btnReset.setVisibility(View.VISIBLE);
            txvFenceMode.setText(R.string.edit_advance_fence_title);

            curTitleFence = strAdvFencesList.get(pos);

            curCenFence1 = advCircleList.get(pos).get(0).getCenter();
            int size = advCircleList.get(pos).size();
            curCenFence2 = advCircleList.get(pos).get(size - 1).getCenter();
            curRadFence = advCircleList.get(pos).get(0).getRadius();

            advMarkerList.get(pos).setVisible(true);

            if (mTempMarker1 != null) {
                mTempMarker1.remove();
            }

            if (mTempCircle1 != null) {
                mTempCircle1.remove();
            }

            if (mTempCircle2 != null) {
                mTempCircle2.remove();
            }

            drawTempAdvanceMarker1(mMap, curCenFence1, curTitleFence);
            drawTempAdvanceFence1(mMap, curCenFence1, curRadFence);
            drawTempAdvanceFence2(mMap, curCenFence2, curRadFence);

            int size1 = advCircleList.get(pos).size();
            for (int i = 0; i < size; i++) {
                tempAdvCircleList.add(drawCircle(mMap, advCircleList.get(pos).get(i).getCenter()
                        , advCircleList.get(pos).get(i).getRadius()));
            }
        }

        txtFenceTitle.setText(curTitleFence);
        int i = (int) curRadFence;
        skbFenceRadius.setProgress(i);
        String t = Integer.toString(i);
        txvFenceRadius.setText("Radius of fence : " + t + " meters.");
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Save Button.
     * Saving fence. There are 4 modes: Add mode, Edit mode, Add Advance mode and Edit Advance mode.
     */
    private void clickSaveButton() {

        if (addMode) {
            try {
                if ((checkTitleFence(txtFenceTitle.getText().toString()))
                        && (skbFenceRadius.isEnabled())) {
                    saveAddMode();
                } else {
                    toast("The title is blank. Please type the title of the fence.");
                }
            } catch (Exception e) {
                Log.e(TAG, "clickSaveButton: addMode is wrong.");
            }
        }

        if (addAdvanceMode) {
            try {
                if (checkTitleFence(txtFenceTitle.getText().toString())) {
                    saveAddAdvanceMode();
                } else {
                    toast("The title is blank. Please type the title of the fence.");
                }
            } catch (Exception e) {
                Log.e(TAG, "clickSaveButton: addAdvanceMode is wrong.");
            }
        }

        if (editMode) {
            try {
                if (checkTitleFence(txtFenceTitle.getText().toString().trim())) {
                    saveEditMode();
                } else {
                    toast("The title is blank. Please type the title of the fence.");
                }
            } catch (Exception e) {
                Log.e(TAG, "clickSaveButton: editMode is wrong.");
            }
        }

        if (editAdvanceMode) {
            try {
                if (checkTitleFence(txtFenceTitle.getText().toString().trim())) {
                    saveEditAdvanceMode();
                } else {
                    toast("The title is blank. Please type the title of the fence.");
                }
            } catch (Exception e) {
                Log.e(TAG, "clickSaveButton: editAdvanceMode is wrong. " + e.getMessage());
            }
        }

        showMarkers(false);
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Save Button.
     * Saving advance fence in Edit Advance mode.
     */
    private void saveEditAdvanceMode() {
        //Saved for preparing Delete database.
        ArrayList<PatientFence> delDatabase = new ArrayList<PatientFence>();
        delDatabase.addAll(new ArrayList<PatientFence>(patientAdvFenceList.get(curPosFence)));

        //Delete advance fences in database.
        int sizeDel = delDatabase.size();
        List<Observable<PatientFence>> delObservables = new ArrayList<>();

        for (int i = 0; i < sizeDel; i++) {
            PatientFence fence = delDatabase.get(i);
            delObservables.add(fence.delete());
        }

        int size = advCircleList.get(curPosFence).size();
        for (int i = 0; i < size; i++) {
            advCircleList.get(curPosFence).get(i).remove();
        }
        advCircleList.remove(curPosFence);
        patientAdvFenceList.remove(curPosFence);
        strAdvFencesList.remove(curPosFence);
        advMarkerList.get(curPosFence).remove();
        advMarkerList.remove(curPosFence);

        String title = txtFenceTitle.getText().toString().trim();
        LatLng center = mTempCircle1.getCenter();
        double radius = mTempCircle1.getRadius();
        largestGroupID = largestGroupID + 1;

        strAdvFencesList.add(title);
        advMarkerList.add(drawMarker(mMap, center, title));

        size = tempAdvCircleList.size();

        ArrayList<Circle> circleArrayList = new ArrayList<Circle>();
        circleArrayList.ensureCapacity(size);
        ArrayList<PatientFence> childPatientAdvFenceList = new ArrayList<PatientFence>();
        childPatientAdvFenceList.ensureCapacity(size);

        for (int i = 0; i < size; i++) {
            center = tempAdvCircleList.get(i).getCenter();
            circleArrayList.add(drawCircle(mMap, center, radius));
            childPatientAdvFenceList.add(new PatientFence(patient, center, radius, title
                    , largestGroupID));
        }
        advCircleList.add(new ArrayList<Circle>(circleArrayList));
        patientAdvFenceList.add(new ArrayList<PatientFence>(childPatientAdvFenceList));

        //Saved for preparing Add database.
        ArrayList<PatientFence> addDatabase = new ArrayList<PatientFence>();
        int sizeList = patientAdvFenceList.size();
        addDatabase.addAll(new ArrayList<PatientFence>(patientAdvFenceList.get(sizeList - 1)));

        //Add advance fences in database.
        int sizeAdd = addDatabase.size();
        List<Observable<PatientFence>> addObservables = new ArrayList<>();

        for (int i = 0; i < sizeAdd; i++) {
            addObservables.add(new PatientFence(patient, addDatabase.get(i).getCenter(),
                    addDatabase.get(i).getRadius(), addDatabase.get(i).getDescription(),
                    largestGroupID)
                    .save());
        }

        Observable.concat(
                Observable.zip(delObservables, args -> null),
                Observable.zip(addObservables, args1 -> null))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        patientFence -> {
                            // TODO
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        }
                );

        // call back after add
        //int sizeTemp = tempAdvCircleList.size();
        for (int i = 0; i < size; i++) {
            tempAdvCircleList.get(i).remove();
        }

        tempAdvCircleList.clear();

        mTempMarker1.remove();
        mTempCircle1.remove();
        mTempCircle2.remove();

        tempAdvanceCircle1 = false;
        tempAdvanceCircle2 = false;

        fenceLayout.setVisibility(View.GONE);
        ibtnMapCenter.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
        btnAddAdvance.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);
        editAdvanceMode = false;

        toast("Edited advance fence successfully.");
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Save Button.
     * Saving advance fence in Add Advance mode.
     */
    private void saveAddAdvanceMode() {
        String title = txtFenceTitle.getText().toString().trim();
        LatLng center = mTempCircle1.getCenter();
        double radius = mTempCircle1.getRadius();
        largestGroupID = largestGroupID + 1;

        strAdvFencesList.add(title);
        advMarkerList.add(drawMarker(mMap, center, title));

        int size = tempAdvCircleList.size();
        ArrayList<Circle> circleArrayList = new ArrayList<Circle>();
        circleArrayList.ensureCapacity(size);
        ArrayList<PatientFence> childPatientAdvFenceList = new ArrayList<PatientFence>();
        childPatientAdvFenceList.ensureCapacity(size);

        for (int i = 0; i < size; i++) {
            center = tempAdvCircleList.get(i).getCenter();
            circleArrayList.add(drawCircle(mMap, center, radius));
            childPatientAdvFenceList.add(new PatientFence(patient, center, radius, title
                    , largestGroupID));
        }
        advCircleList.add(new ArrayList<Circle>(circleArrayList));
        patientAdvFenceList.add(new ArrayList<PatientFence>(childPatientAdvFenceList));

        //Saved for preparing Add database.
        ArrayList<PatientFence> addDatabase = new ArrayList<PatientFence>();
        int sizeList = patientAdvFenceList.size();
        addDatabase.addAll(new ArrayList<PatientFence>(patientAdvFenceList.get(sizeList - 1)));

        //Add advance fences in database.
        int sizeAdd = addDatabase.size();
        List<Observable<PatientFence>> addObservables = new ArrayList<>();

        for (int i = 0; i < sizeAdd; i++) {
            addObservables.add(new PatientFence(patient, addDatabase.get(i).getCenter(),
                    addDatabase.get(i).getRadius(), addDatabase.get(i).getDescription(),
                    largestGroupID)
                    .save());
        }

        Observable.zip(addObservables, args -> null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        patientFence -> {
                            // TODO
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        }
                );

/*
        //Add advance fences in database.
        int sizeAdd = addDatabase.size();
        for (int i = 0; i < sizeAdd; i++) {
            new PatientFence(patient, addDatabase.get(i).getCenter(),
                    addDatabase.get(i).getRadius(), addDatabase.get(i).getDescription(),
                    largestGroupID)
                    .save()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            patientFence -> {
                                // TODO
                            },
                            throwable -> {
                                Log.e(TAG, throwable.getMessage());
                            }
                    );
        }
*/
        for (int i = 0; i < size; i++) {
            tempAdvCircleList.get(i).remove();
        }

        tempAdvCircleList.clear();
        mTempMarker1.remove();
        mTempCircle1.remove();
        mTempCircle2.remove();

        fenceLayout.setVisibility(View.GONE);
        ibtnMapCenter.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
        btnAddAdvance.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);

        btnSave.setEnabled(true);
        tempAdvanceCircle1 = false;
        tempAdvanceCircle2 = false;

        addAdvanceMode = false;

        toast("Saved advance fence successfully.");
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Save Button.
     * Saving fence in Add mode.
     */
    private void saveAddMode() {
        String title = txtFenceTitle.getText().toString().trim();
        LatLng center = mTempCircle.getCenter();
        double radius = mTempCircle.getRadius();

        //
        int ChosenHour = numberPicker.getValue();
        int ChosenMin = numberPicker2.getValue();

        Calendar calendarStartTime = Calendar.getInstance();
        Calendar calendarEndTime = Calendar.getInstance();
        // int CurrentMinute = calendarEndTime.get(Calendar.MINUTE);

        //24 hour format
        //int  CurrentHour = calendarEndTime.get(Calendar.HOUR_OF_DAY);
        calendarEndTime.add(Calendar.MINUTE, ChosenMin);
        calendarEndTime.add(Calendar.HOUR_OF_DAY, ChosenHour);


        textView3.setVisibility(View.VISIBLE);
        textView3.setText(Integer.toString(calendarEndTime.get(Calendar.HOUR_OF_DAY))
                + ""
                + Integer.toString(calendarEndTime.get(Calendar.MINUTE)));


        PatientFence newFence = new PatientFence(patient, center, radius, title);

        if (togButton.isChecked()) {
            newFence.setStartTime(calendarStartTime);
            newFence.setEndTime(calendarEndTime);
        }

        newFence.save()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        patientFence -> {
                            // updates the object in the list
                            patientFenceList.add(patientFence);

                            strFencesList.add(patientFence.getDescription());
                            markerList.add(drawMarker(mMap, patientFence.getCenter(),
                                    patientFence.getDescription()));
                            circlesList.add(drawCircle(mMap, patientFence.getCenter(),
                                    patientFence.getRadius()));

                            mTempMarker.remove();
                            mTempCircle.remove();

                            fenceLayout.setVisibility(View.GONE);
                            ibtnMapCenter.setVisibility(View.VISIBLE);
                            btnAdd.setVisibility(View.VISIBLE);
                            btnAddAdvance.setVisibility(View.VISIBLE);
                            btnCancel.setVisibility(View.GONE);
                            addMode = false;

                            toast("Saved fence successfully.");
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getMessage());
                        }
                );
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Save Button.
     * Saving fence in Edit mode.
     */
    private void saveEditMode() {
        String title = txtFenceTitle.getText().toString().trim();
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
                            btnAddAdvance.setVisibility(View.VISIBLE);
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
     * This method is used to implement actions of Reset Button.
     * Reset all actions in Add Advance mode or Edit Advance mode.
     */
    private void clickResetButton() {
        if ((addAdvanceMode) || (editAdvanceMode)) {
            tempAdvanceCircle1 = false;
            tempAdvanceCircle2 = false;

            btnSave.setEnabled(false);

            if (mTempMarker1 != null) {
                mTempMarker1.remove();
            }

            if (mTempCircle1 != null) {
                mTempCircle1.remove();
            }

            if (mTempCircle2 != null) {
                mTempCircle2.remove();
            }

            clearTempAdvCircleList();
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Delete Button.
     * Deleting advance fence in Edit Advance mode.
     */
    private void deleteAdvanceFence() {
        mTempMarker1.remove();
        mTempCircle1.remove();
        mTempCircle2.remove();

        //Saved for preparing Delete database.
        ArrayList<PatientFence> delDatabase = new ArrayList<PatientFence>();
        delDatabase.addAll(new ArrayList<PatientFence>(patientAdvFenceList.get(curPosFence)));

        //Delete advance fences in database.
        int sizeDel = delDatabase.size();
        for (int i = 0; i < sizeDel; i++) {
            PatientFence fence = delDatabase.get(i);
            fence.delete()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            patientFence -> {
                                // TODO
                            },
                            throwable -> {
                                Log.e(TAG, throwable.getMessage());
                            }
                    );
        }

        int size = tempAdvCircleList.size();
        for (int i = 0; i < size; i++) {
            tempAdvCircleList.get(i).remove();
            advCircleList.get(curPosFence).get(i).remove();
        }

        tempAdvCircleList.clear();
        advCircleList.remove(curPosFence);
        patientAdvFenceList.remove(curPosFence);

        advMarkerList.get(curPosFence).remove();
        advMarkerList.remove(curPosFence);

        strAdvFencesList.remove(curPosFence);

        fenceLayout.setVisibility(View.GONE);
        ibtnMapCenter.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.VISIBLE);
        btnAddAdvance.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        editAdvanceMode = false;

        toast("Deleted advance fence successfully.");

        showMarkers(false);
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Delete Button.
     * Deleting normal fence in Edit mode.
     */
    private void deleteNormalFence() {
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
                                btnAddAdvance.setVisibility(View.VISIBLE);
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
                    if (!advEditFlag) {
                        deleteNormalFence();
                    } else {
                        deleteAdvanceFence();
                    }
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
        btnAddAdvance.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.GONE);
        btnReset.setVisibility(View.GONE);

        btnSave.setEnabled(true);

        addMode = false;
        addAdvanceMode = false;
        editMode = false;
        editAdvanceMode = false;

        tempAdvanceCircle1 = false;
        tempAdvanceCircle2 = false;

        if (mTempMarker != null) {
            mTempMarker.remove();
        }

        if (mTempMarker1 != null) {
            mTempMarker1.remove();
        }

        if (mTempCircle != null) {
            mTempCircle.remove();
        }

        if (mTempCircle1 != null) {
            mTempCircle1.remove();
        }

        if (mTempCircle2 != null) {
            mTempCircle2.remove();
        }

        clearTempAdvCircleList();

        showMarkers(false);
    }

    private void clearTempAdvCircleList() {
        if (!tempAdvCircleList.isEmpty()) {
            int size = tempAdvCircleList.size();
            for (int i = 0; i < size; i++) {
                tempAdvCircleList.get(i).remove();
            }
            tempAdvCircleList.clear();
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Radius Seek Bar.
     * Changing radius and title of fence when seek bar changes.
     *
     * @param progress The value of Seekbar progress.
     */
    private void changedSeekBar(int progress) {
        double d = (double) progress;
        if (d >= 0) {
            if ((addMode) || (editMode)) {
                if (mTempCircle != null) {
                    mTempCircle.setRadius(d);
                }
            }

            if ((addAdvanceMode) || (editAdvanceMode)) {
                if (mTempCircle1 != null) {
                    mTempCircle1.setRadius(d);
                }

                if (mTempCircle2 != null) {
                    mTempCircle2.setRadius(d);
                }
            }
            txvFenceRadius.setText("Radius of fence : " + Integer.toString(progress)
                    + " meters.");
        } else {
            if ((addMode) || (editMode)) {
                if (mTempCircle != null) {
                    mTempCircle.setRadius(0.0);
                }
            }

            if ((addAdvanceMode) || (editAdvanceMode)) {
                if (mTempCircle1 != null) {
                    mTempCircle1.setRadius(0.0);
                }

                if (mTempCircle2 != null) {
                    mTempCircle2.setRadius(0.0);
                }
            }
            txvFenceRadius.setText("Radius of fence : 0 meters.");
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Add mode in clickMap Method.
     *
     * @param latLng The location which user clicked on the map.
     */
    private void clickMapAddEditMode(LatLng latLng) {
        try {
            if (!editMode) {
                skbFenceRadius.setEnabled(true);
            }

            drawTempMarker(mMap, latLng, TITLE_DEFAULT);
            drawTempFence(mMap, latLng, RADIUS_DEFAULT);

            int i = (int) mTempCircle.getRadius();
            skbFenceRadius.setProgress(i);
            String t = Integer.toString(i);
            txvFenceRadius.setText("Radius of fence : " + t + " meters.");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Edit Advance mode in clickMap Method.
     *
     * @param latLng The location which user clicked on the map.
     */
    private void clickMapEditAdvanceMode(LatLng latLng) {
        try {
            String st = "Edit Advance Fence";

            if (tempAdvanceCircle1 == false) {
                if (mTempMarker1 != null) {
                    mTempMarker1.remove();
                }

                if (mTempCircle1 != null) {
                    mTempCircle1.remove();
                }

                if (mTempCircle2 != null) {
                    mTempCircle2.remove();
                }

                clearTempAdvCircleList();

                AlertDialog.Builder b = new AlertDialog.Builder(MapActivity.this);

                b.setTitle(st);
                b.setMessage("Do you finish determining the fence 1 ?");
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        skbFenceRadius.setEnabled(true);
                        tempAdvanceCircle1 = true;
                        btnSave.setEnabled(false);

                        drawTempAdvanceMarker1(mMap, latLng, TITLE_DEFAULT);
                        drawTempAdvanceFence1(mMap, latLng, RADIUS_DEFAULT);

                        int i = (int) mTempCircle1.getRadius();
                        skbFenceRadius.setProgress(i);
                        String t = Integer.toString(i);
                        txvFenceRadius.setText("Radius of fence : " + t + " meters.");
                    }
                });

                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                b.create().show();
            } else {
                if (tempAdvanceCircle2 == false) {
                    AlertDialog.Builder b = new AlertDialog.Builder(MapActivity.this);

                    b.setTitle(st);
                    b.setMessage("Do you finish determining the fence 2 ?");
                    b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tempAdvanceCircle2 = true;
                            drawTempAdvanceFence2(mMap, latLng, mTempCircle1.getRadius());

                            // Draw all Circles
                            drawAdvanceCircles(mTempCircle1, mTempCircle2, tempAdvCircleList);

                            skbFenceRadius.setEnabled(false);
                            btnSave.setEnabled(true);
                        }
                    });

                    b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    b.create().show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of Add Advance mode in clickMap Method.
     *
     * @param latLng The location which user clicked on the map.
     */
    private void clickMapAddAdvanceMode(LatLng latLng) {
        try {
            String st = "Add Advance Fence";

            if (tempAdvanceCircle1 == false) {
                if (mTempMarker1 != null) {
                    mTempMarker1.remove();
                }

                if (mTempCircle1 != null) {
                    mTempCircle1.remove();
                }

                if (mTempCircle2 != null) {
                    mTempCircle2.remove();
                }

                clearTempAdvCircleList();

                AlertDialog.Builder b = new AlertDialog.Builder(MapActivity.this);

                b.setTitle(st);
                b.setMessage("Do you finish determining the fence 1 ?");
                b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        skbFenceRadius.setEnabled(true);
                        tempAdvanceCircle1 = true;

                        drawTempAdvanceMarker1(mMap, latLng, TITLE_DEFAULT);
                        drawTempAdvanceFence1(mMap, latLng, RADIUS_DEFAULT);

                        int i = (int) mTempCircle1.getRadius();
                        skbFenceRadius.setProgress(i);
                        String t = Integer.toString(i);
                        txvFenceRadius.setText("Radius of fence : " + t + " meters.");
                    }
                });

                b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                b.create().show();
            } else {
                if (tempAdvanceCircle2 == false) {
                    AlertDialog.Builder b = new AlertDialog.Builder(MapActivity.this);

                    b.setTitle(st);
                    b.setMessage("Do you finish determining the fence 2 ?");
                    b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tempAdvanceCircle2 = true;
                            drawTempAdvanceFence2(mMap, latLng, mTempCircle1.getRadius());

                            // Draw all Circles
                            drawAdvanceCircles(mTempCircle1, mTempCircle2, tempAdvCircleList);

                            skbFenceRadius.setEnabled(false);
                            btnSave.setEnabled(true);
                        }
                    });

                    b.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    b.create().show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to draw Advance Fences.
     */
    private void drawAdvanceCircles(Circle circle1, Circle circle2, ArrayList<Circle> mList) {
        if ((circle1 != null) && (circle2 != null)) {
            LatLng center1 = circle1.getCenter();
            LatLng center2 = circle2.getCenter();
            double radius = circle1.getRadius();
            double rate = rateOfFenceToDistanceTwoFences(center1, center2, radius);
            int number = numberOfFencesBetweenTwoFences(center1, center2, radius);

            mList.clear();
            mList.add(drawCircle(mMap, center1, radius));

            for (int i = 1; i < number + 1; i++) {
                double x = coordinateOfFence(center1, center2, rate, true, i);
                double y = coordinateOfFence(center1, center2, rate, false, i);

                mList.add(drawCircle(mMap, new LatLng(x, y), radius));
            }

            mList.add(drawCircle(mMap, center2, radius));
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to calculate distance of two fences on map.
     *
     * @param latLng1 The location of fence 1 on the map.
     * @param latLng2 The location of fence 2 on the map.
     * @return float Distance of two fences.
     */
    private float distanceOf2Fences(LatLng latLng1, LatLng latLng2) {
        Location location1 = new Location("Location 1");
        location1.setLatitude(latLng1.latitude);
        location1.setLongitude(latLng1.longitude);

        Location location2 = new Location("Location 2");
        location2.setLatitude(latLng2.latitude);
        location2.setLongitude(latLng2.longitude);

        float distance = location1.distanceTo(location2);

        return distance;
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to calculate the number of the fences between two fences on the map.
     *
     * @param latLng1 The location of fence 1 on the map.
     * @param latLng2 The location of fence 2 on the map.
     * @param radius  The radius of the fence on the map.
     * @return int The rate of fence to distance of two fences on the map.
     */
    private int numberOfFencesBetweenTwoFences(LatLng latLng1, LatLng latLng2, double radius) {
        float distance = distanceOf2Fences(latLng1, latLng2);
        double temp = distance / radius;
        int number = (int) temp;
        return number;
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to calculate the distance rate of fence to distance of two fences
     * on the map.
     *
     * @param latLng1 The location of fence 1 on the map.
     * @param latLng2 The location of fence 2 on the map.
     * @param radius  The radius of the fence on the map.
     * @return double The rate of fence to distance of two fences on the map.
     */
    private double rateOfFenceToDistanceTwoFences(LatLng latLng1, LatLng latLng2, double radius) {
        float distance = distanceOf2Fences(latLng1, latLng2);
        double rate = radius / distance;
        return rate;
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to calculate coordinate of fence which is between two fences on the map.
     *
     * @param latLng1 The location of fence 1 on the map.
     * @param latLng2 The location of fence 2 on the map.
     * @param rate    The rate of fence to distance of two fences on the map.
     * @param flag    The coordinate X (TRUE) or Y (FALSE) of the fence which is between two fences
     *                on the map.
     * @param number  The order number of the fence which is between two fences on the map.
     * @return double coordinate of the fence.
     */
    private double coordinateOfFence(LatLng latLng1, LatLng latLng2,
                                     double rate, boolean flag, int number) {
        double coordinate;
        if (flag) {
            coordinate = (latLng2.latitude - latLng1.latitude) * number * rate + latLng1.latitude;
        } else {
            coordinate = (latLng2.longitude - latLng1.longitude) * number * rate + latLng1.longitude;
        }
        return coordinate;
    }

    /**
     * Created by Nguyen Nam Cuong Tran.
     * This method is used to implement actions of clicking on map.
     * Set location to draw or edit fence.
     *
     * @param latLng The location which user clicked on the map.
     */
    public void clickMap(LatLng latLng) {
        if (!addMode && !editMode && !addAdvanceMode && !editAdvanceMode) {
            int pos = posFenceWhenClicked(latLng);
            if (pos != -1) {
                showMarkers(false);
                if (!advEditFlag) {
                    markerList.get(pos).setVisible(true);
                } else {
                    advMarkerList.get(pos).setVisible(true);
                }
            }
        }

        if (addMode) {
            if (mTempMarker != null) {
                mTempMarker.remove();
            }

            if (mTempCircle != null) {
                mTempCircle.remove();
            }

            clickMapAddEditMode(latLng);
        }

        if (addAdvanceMode) {
            clickMapAddAdvanceMode(latLng);
        }

        if (editMode) {
            if (mTempMarker != null) {
                mTempMarker.remove();
            }

            if (mTempCircle != null) {
                mTempCircle.remove();
            }

            clickMapAddEditMode(latLng);
        }

        if (editAdvanceMode) {
            clickMapEditAdvanceMode(latLng);
        }
    }

    /**
     * Created by Nguyen Nam Cuong Tran
     * This method is used to draw temporary advance marker 1 for adding and editing advance fence.
     *
     * @param mMap   An object of Google map.
     * @param latLng The center of the temporary marker on the map.
     * @param title  The title of the temporary marker.
     */
    private void drawTempAdvanceMarker1(GoogleMap mMap, LatLng latLng, String title) {
        mTempMarker1 = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    /**
     * Created by Nguyen Nam Cuong Tran
     * This method is used to draw temporary advance circle 1 for adding and editing advance fence.
     *
     * @param mMap   An object of Google map.
     * @param latLng The center of the temporary circle on the map.
     * @param radius The radius of the temporary circle.
     */
    private void drawTempAdvanceFence1(GoogleMap mMap, LatLng latLng, double radius) {
        mTempCircle1 = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(0x7044BBBB)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2));
    }

    /**
     * Created by Nguyen Nam Cuong Tran
     * This method is used to draw temporary advance circle 2 for adding and editing advance fence.
     *
     * @param mMap   An object of Google map.
     * @param latLng The center of the temporary circle on the map.
     * @param radius The radius of the temporary circle.
     */
    private void drawTempAdvanceFence2(GoogleMap mMap, LatLng latLng, double radius) {
        mTempCircle2 = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .fillColor(0x7044BBBB)
                .strokeColor(Color.TRANSPARENT)
                .strokeWidth(2));
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
                .subscribe(this::updatePatientOnMap,
                        throwable -> Log.e(TAG, throwable.getMessage()));
    }

    /**
     * Update patient's location on map, add a marker and move to marker if it is not present
     *
     * @param patientCurrentLocation current location of patient
     */
    private void updatePatientOnMap(LatLng patientCurrentLocation) {
        if (patientMarker == null) {
            patientMarker = mMap.addMarker(new MarkerOptions()
                            .position(patientCurrentLocation)
                            .title(getPatientName())
                            .icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            );
            mMap.moveCamera(newLatLngZoom(patientMarker.getPosition(), 15.0f));
        }
        updatePatientLocationOnMap(patientMarker, patientCurrentLocation, false);
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This method updates the patient marker to a new location
     */
    public void updatePatientLocationOnMap(Marker marker, LatLng toPosition,
                                           boolean hideMarker) {
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
                .subscribe(this::updatePatientOnMap);

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
        return PatientLocation.findLatestPatientLocation(patient)
                .filter(patientLocation1 -> patientLocation1 != null)
                .map(PatientLocation::getLatLng);
    }

    /**
     * Created by Tristan Dubois
     * <p>
     * This gets the name of the patient from the database.
     */
    private String getPatientName() {
        patientMarkerName = patient.getName();
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