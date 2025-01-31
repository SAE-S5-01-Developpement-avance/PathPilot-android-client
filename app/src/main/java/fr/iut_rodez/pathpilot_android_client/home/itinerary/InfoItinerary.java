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
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.home.routes.IRouteService;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

public class InfoItinerary extends AppCompatActivity {

    private static final String TAG = InfoItinerary.class.getSimpleName();
    public static final String ITINERARY_KEY = "itinerary";

    private final IRouteService routeService = ServiceFactory.getRouteService();
    private ClientArrayAdapter clientsAddedAdapter;
    private Itinerary itinerary;
    private Popup popup;
    private ListView listItemsClientsAdded;
    private JWTToken jwtToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_info_itineray);
        findViewById(R.id.button_start_itinerary).setOnClickListener(v -> createAndStartRoute());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        popup = new Popup(this);

        setUpListClient();
        setUpToken();
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

    /**
     * Set up the token of the user.
     * <p>
     *     Retrieve the token from the intent and set it in the route service.
     * </p>
     */
    private void setUpToken() {
        Intent intent = getIntent();
        if (intent.hasExtra(FragmentItineraries.CLE_TOKEN)) {
            jwtToken = intent.getParcelableExtra(FragmentItineraries.CLE_TOKEN);
        } else {
            // This should never happen
            Log.e(TAG, "setUpToken: No token found in the intent");
            popup.showAlertDialog("Error", "No token found in the intent", new Popup.Button("Ok", (dialog, which) -> finish()),null, null); // TODO i18n
        }
    }

    public JWTToken getJwtToken() {
        return jwtToken;
    }

    private void createAndStartRoute() {
        routeService.createRoute(this, itinerary);
    }

    public Popup getPopup() {
        return popup;
    }
}
