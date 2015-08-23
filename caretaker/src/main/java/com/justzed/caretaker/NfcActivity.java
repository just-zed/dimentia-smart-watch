package com.justzed.caretaker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import com.justzed.common.model.PatientLink;
import com.justzed.common.model.Person;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NfcActivity extends Activity {

    public static final String INTENT_TOKEN_KEY = "personUniqueToken";
    private static final int REQ_CODE_MAIN = 1;
    private static final String TAG = NfcActivity.class.getName();

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNdefExchangeFilters;
    private TextView text;

    private String patientToken;
    private String caretakerToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_caretaker_nfc);
        text = (TextView) findViewById(R.id.text_view);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if ((checkNFCHardware()) && (checkNFCEnabled())) {

            // Handle all of our received NFC intents in this activity.
            mNfcPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            // Intent filters for reading a note from a tag or exchanging over p2p.
            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndefDetected.addDataType("text/plain");
            } catch (IntentFilter.MalformedMimeTypeException e) {
            }
            mNdefExchangeFilters = new IntentFilter[]{ndefDetected};

        }

        if (!mPrefs.contains(MainActivity.PREF_PERSON_KEY)) {
            // start main activity if pref key not present
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, REQ_CODE_MAIN);
        } else {
            caretakerToken = mPrefs.getString(MainActivity.PREF_PERSON_KEY, "");
        }


    }

    private void enableNdefExchangeMode() {
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
            setIntent(new Intent()); // Consume this intent.
        }
        enableNdefExchangeMode();
    }

    private void setNoteBody(String body) {
        text.setText(body);
        // do stuff

        patientToken = body;

        saveLink();
    }

    private void saveLink() {
        if (patientToken != null && caretakerToken != null) {
            Observable.combineLatest(
                    Person.getByUniqueToken(patientToken),
                    Person.getByUniqueToken(caretakerToken),
                    (PatientLink::new))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(patientLink -> {
                        patientLink
                                .save()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(patientLink1 -> {
                                    Toast.makeText(this, "Link Saved!", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    });
//                    .flatMap(PatientLink::save)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(patientLink -> {
//                        Toast.makeText(this, "Link Saved!", Toast.LENGTH_SHORT);
//                        finish();
//                    }, throwable -> {
//                        Log.e(TAG, throwable.getMessage());
//                    });
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] msgs = getNdefMessages(intent);
            byte[] payload = msgs[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
        }
    }

    private NdefMessage[] getNdefMessages(Intent intent) {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
        }
        return msgs;
    }

    // Check whether NFC hardware is available on device
    private boolean checkNFCHardware() {
        PackageManager pm = this.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            toast("The device does not has NFC hardware.");
            return false;
        } else {
            // NFC and Android Beam file transfer is supported.
            toast("NFC and Android Beam are supported on your device.");
        }
        return true;
    }

    // Check whether NFC is enabled on device
    private boolean checkNFCEnabled() {
        if (!mNfcAdapter.isEnabled()) {
            toast("Please enable NFC.");

            // NFC is disabled, show the settings UI to enable NFC
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            return false;
        }

        // Check whether Android Beam feature is enabled on device
        else if (!mNfcAdapter.isNdefPushEnabled()) {
            toast("Please enable Android Beam.");

            // Android Beam is disabled, show the settings UI to enable Android Beam
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            return false;
        } else {
            // NFC and Android Beam are enabled on device
            toast("NFC and Android Beam are supported on your device.");
        }
        return true;
    }

    // Toast method
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQ_CODE_MAIN) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                caretakerToken = getIntent().getStringExtra(INTENT_TOKEN_KEY);

                saveLink();


            }
        }
    }

}
