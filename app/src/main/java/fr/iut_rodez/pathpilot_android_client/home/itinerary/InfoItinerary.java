package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

public class InfoItinerary extends AppCompatActivity {

    private static final String TAG = InfoItinerary.class.getSimpleName();

    private ClientArrayAdapter clientsAddedAdapter;
    private Itinerary itinerary;
    private Popup popup;
    private ListView listItemsClientsAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_info_itineray);

        popup = new Popup(this);
        listItemsClientsAdded = findViewById(R.id.list_items_clients_added);

        findViewById(R.id.button_start_itinerary).setOnClickListener(v -> startItinerary());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        Intent intent = getIntent();
        itinerary = intent.getParcelableExtra("itinerary");
        if (itinerary == null) {
            Log.e(TAG, "onCreate: No itinerary found in the intent");
            popup.showAlertDialog("Error", "No itinerary found in the intent"); // TODO i18n
            finish();
        }

        clientsAddedAdapter = new ClientArrayAdapter(this, itinerary.getClients());
        listItemsClientsAdded.setAdapter(clientsAddedAdapter);
        ((TextView) findViewById(R.id.header_text)).setText(getString(R.string.itinerary_number) + itinerary.getId());
    }

    private void startItinerary() {
        Log.d(TAG, "startItinerary: Start the itinerary");
    }
}
