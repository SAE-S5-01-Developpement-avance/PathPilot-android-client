package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.InfoRoute;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.service.IRouteService;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.player.PlayerItinerary;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class InfoItinerary extends AppCompatActivity {

    private static final String TAG = InfoItinerary.class.getSimpleName();
    public static final String ROUTE_KEY = "route";
    public static final String JWT_TOKEN_KEY = "jwtToken";

    private final IRouteService routeService = ServiceFactory.getRouteService();
    private Itinerary itinerary;
    private Popup popup;
    private ListView listItemsClientsAdded;
    private JWTToken jwtToken;
    private ActivityResultLauncher<Intent> playerItineraryLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_info_itineray);

        Intent intent = getIntent();
        if (!intent.hasExtra(FragmentItineraries.TOKEN_KEY)) {
            // This should never happen
            Log.e(TAG, "setUpToken: No token found in the intent");
            popup.showAlertDialogOK(getString(R.string.error), getString(R.string.no_token_retrieve), DialogButton.okFinish(this));
        } else {
            jwtToken = intent.getParcelableExtra(FragmentItineraries.TOKEN_KEY);

            findViewById(R.id.button_start_itinerary).setOnClickListener(v -> createNewRoute());
            findViewById(R.id.backButton).setOnClickListener(v -> finish());

            listItemsClientsAdded = findViewById(R.id.list_items_clients_added);

            popup = new Popup(this);

            playerItineraryLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::returnFromPlayerItinerary);

            setUpListClient();
        }
    }

    /**
     * Set up the list of clients of the itinerary.
     * <p>
     * Retrieve the itinerary from the intent and the list of clients from the itinerary.
     * Set the adapter of the list view with the list of clients.
     * </p>
     */
    private void setUpListClient() {
        Intent intent = getIntent();
        itinerary = intent.getParcelableExtra(FragmentItineraries.ITINERARY_KEY);

        if (itinerary == null) {
            Log.e(TAG, "onCreate: No itinerary found in the intent");
            popup.showAlertDialog(getString(R.string.error), getString(R.string.no_itinerary_retrieve));
            finish();
        }
        List<Client> clients = itinerary.getClients();

        for (Client client : clients) {
            client.setAddressDisplayName(this);
            Log.d(TAG, "setUpListClient: Client: " + client);
        }

        ClientArrayAdapter clientsAddedAdapter = new ClientArrayAdapter(this, clients);
        listItemsClientsAdded.setAdapter(clientsAddedAdapter);
        ((TextView) findViewById(R.id.header_text)).setText(itinerary.getDisplayName());
    }

    public JWTToken getJwtToken() {
        return jwtToken;
    }

    private void createNewRoute() {
        routeService.createRoute(this, jwtToken, itinerary,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "createRoute: " + response);

                    Route route = Parser.getRoute(response);
                    redirectToPlayerActivity(route);
                },
                error -> {
                    popup.dismissProgressDialog();
                    VolleyErrorHandler.handleError(this, error);
                }
        );
    }

    public Popup getPopup() {
        return popup;
    }

    public void redirectToPlayerActivity(Route route) {
        Intent intent = new Intent(this, InfoRoute.class);

        intent.putExtra(InfoRoute.ROUTE_KEY, route);
        intent.putExtra(InfoRoute.JWT_TOKEN_KEY, jwtToken);

        startActivity(intent);
        finish();
    }

    /**
     * Data returned by the player itinerary
     *
     * @param result result returned by the intent
     */
    private void returnFromPlayerItinerary(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Log.d(TAG, "onCreate: Player itinerary");
            Log.d(TAG, "onCreate: " + result.getData());

            Intent intent = new Intent(this, Home.class);
            setResult(RESULT_OK, intent);
            if (result.getData() != null
                    && result.getData().hasExtra(PlayerItinerary.ROUTE_STOPPED_KEY)) {

                intent.putExtra(PlayerItinerary.ROUTE_STOPPED_KEY, true);
            }
            intent.putExtra(Home.INDEX_FRAGMENT_KEY, Home.INDEX_FRAGMENT_ITINERARY);
            finish();
        }
    }
}
