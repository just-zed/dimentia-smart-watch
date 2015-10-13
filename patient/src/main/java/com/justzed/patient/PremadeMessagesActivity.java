package com.justzed.patient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.justzed.common.NotificationMessage;
import com.justzed.common.model.Person;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * This class is used to send a pre-made message to the caretaker
 *
 * @author Hiroki Takahashi
 * @since 6-10-2015
 */
public class PremadeMessagesActivity extends Activity {

    @Bind(android.R.id.list)
    ListView list1;

    // temp token
    private ArrayAdapter<String> adapter;
    private Person patient;

    @OnItemClick(android.R.id.list)
    @SuppressWarnings("unused")
    void onListItemClick(AdapterView<?> arg0, View view, int position, long id) {
        if (patient != null) {
            String message = adapter.getItem(position);

            // push to channel, channel name is patient- + patient's unique id
            String channelName = "patient-" + patient.getUniqueToken();
            NotificationMessage.sendMessage(channelName, patient.getName() + ": " + message);
            Toast.makeText(getApplicationContext(), String.format(getString(R.string.message_sent), message), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premade_messages);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        patient = data.getParcelable(Person.PARCELABLE_KEY);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.premade_messages));
        list1.setAdapter(adapter);
    }
}
