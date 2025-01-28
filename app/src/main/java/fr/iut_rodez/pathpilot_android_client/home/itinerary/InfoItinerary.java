package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

public class InfoItinerary extends AppCompatActivity {

    private static final String TAG = InfoItinerary.class.getSimpleName();
    public static final String ITINERARY_KEY = "itinerary";

    private ClientArrayAdapter clientsAddedAdapter;
    private Itinerary itinerary;
    private Popup popup;
    private ListView listItemsClientsAdded;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_info_itineray);
        findViewById(R.id.button_start_itinerary).setOnClickListener(v -> createAndStartRoute());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        popup = new Popup(this);

        setUpListClient();
    }

    /**
     * Set up the list of clients of the itinerary.
     * <p>
     *     Retrieve the itinerary from the intent and the list of clients from the itinerary.
     *     Set the adapter of the list view with the list of clients.
     * </p>
     */
    private void setUpListClient() {
        listItemsClientsAdded = findViewById(R.id.list_items_clients_added);
        Intent intent = getIntent();
        itinerary = intent.getParcelableExtra(FragmentItineraries.ITINERARY_KEY);

        if (itinerary == null) {
            Log.e(TAG, "onCreate: No itinerary found in the intent");
            popup.showAlertDialog("Error", "No itinerary found in the intent"); // TODO i18n
            finish();
        }
        List<Client> clients = itinerary.getClients();

        for (Client client : clients) {
            client.setAddressDisplayName(this);
            Log.d(TAG, "setUpListClient: Client: " + client);
        }

        clientsAddedAdapter = new ClientArrayAdapter(this, clients);
        listItemsClientsAdded.setAdapter(clientsAddedAdapter);
        ((TextView) findViewById(R.id.header_text)).setText(itinerary.getDisplayName());
    }

    private void createAndStartRoute() {
        Intent intent = new Intent(this, PlayerItinerary.class);
        intent.putExtra(ITINERARY_KEY, itinerary);
        startActivity(intent);
    }
}
