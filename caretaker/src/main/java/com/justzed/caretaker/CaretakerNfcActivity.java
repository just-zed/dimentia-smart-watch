package com.justzed.caretaker;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class CaretakerNfcActivity extends Activity {

    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNdefExchangeFilters;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            } catch (IntentFilter.MalformedMimeTypeException e) { }
            mNdefExchangeFilters = new IntentFilter[] { ndefDetected };

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
    private boolean checkNFCHardware(){
        PackageManager pm = this.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            toast("The device does not has NFC hardware.");
            return false;
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            toast("Android Beam is not supported.");
            return false;
        }
        else {
            // NFC and Android Beam file transfer is supported.
            toast("NFC and Android Beam are supported on your device.");
        }
        return true;
    }

    // Check whether NFC is enabled on device
    private boolean checkNFCEnabled(){
        if (!mNfcAdapter.isEnabled()){
            toast("Please enable NFC.");

            // NFC is disabled, show the settings UI to enable NFC
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            return false;
        }

        // Check whether Android Beam feature is enabled on device
        else if(!mNfcAdapter.isNdefPushEnabled()) {
            toast("Please enable Android Beam.");

            // Android Beam is disabled, show the settings UI to enable Android Beam
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            return false;
        }
        else {
            // NFC and Android Beam are enabled on device
            toast("NFC and Android Beam are supported on your device.");
        }
        return true;
    }

    // Toast method
    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
