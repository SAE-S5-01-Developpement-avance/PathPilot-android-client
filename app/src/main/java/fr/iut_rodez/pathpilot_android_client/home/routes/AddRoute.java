package fr.iut_rodez.pathpilot_android_client.home.routes;

import static fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client.getClientsDisplay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.AddItinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.FragmentItineraries;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.service.IRouteService;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class AddRoute extends AppCompatActivity {
    public static final String ADDED_ROUTE_KEY = "routeAdded";
    private Spinner selectItinerary;
    private ArrayList<Itinerary> listItineraries;
    private JWTToken jwtToken;
    private Itinerary selectedItinerary;
    private ArrayAdapter<Itinerary> itinerariesArrayAdapter;
    private Popup popup;

    IRouteService routeService = ServiceFactory.getRouteService();

    private static final String TAG = AddRoute.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_create_route);

        ((TextView) findViewById(R.id.header_text)).setText(R.string.header_create_route);

        selectItinerary = findViewById(R.id.select_itinerary);
        popup = new Popup(this);
        listItineraries = new ArrayList<>();

        Intent intent = getIntent();
        Serializable serializableExtra = intent.getSerializableExtra(FragmentRoutes.LIST_ITINERARIES_KEY);
        ArrayList<Itinerary> itineraries = serializableExtra == null ? new ArrayList<>() : (ArrayList<Itinerary>) serializableExtra;
        for (int position = 0; position < itineraries.size(); position++) {
            Itinerary itinerary = itineraries.get(position);
            itinerary.getClients().forEach(client -> client.setAddressDisplayName(this));
            itinerary.setDisplayName(getString(R.string.itinerary_item_add_route, position + 1, getClientsDisplay(itinerary.getClients())));
        }

        listItineraries.addAll(itineraries);

        itinerariesArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, listItineraries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(R.id.spinner_item_text);
                textView.setText(getItem(position).getDisplayName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(R.id.spinner_item_text);
                Itinerary itinerary = getItem(position);
                textView.setText(itinerary.getDisplayName());
                return view;
            }
        };
        selectItinerary.setAdapter(itinerariesArrayAdapter);

        selectItinerary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItinerary = listItineraries.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedItinerary = null;
            }
        });

        findViewById(R.id.button_create_route).setOnClickListener(v -> createRoute());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        jwtToken = intent.getParcelableExtra(FragmentItineraries.TOKEN_KEY);
    }

    /**
     * Create a route with the itinerary selected.
     * The route can be created if it has one itinerary attached.
     */
    public void createRoute() {
        if (selectedItinerary == null) {
            popup.showAlertDialog(getString(R.string.error_title), getString(R.string.error_select_itinerary));
        } else {
            routeService.createRoute(this, jwtToken, selectedItinerary,
                    response -> {
                        popup.dismissProgressDialog();
                        Log.d(TAG, "createRoute: " + response);

                        Intent returnIntent = new Intent(this, Home.class);
                        setResult(AddItinerary.RESULT_OK, returnIntent);
                        returnIntent.putExtra(AddRoute.ADDED_ROUTE_KEY, true);
                        finish();
                    },
                    error -> {
                        popup.dismissProgressDialog();
                        VolleyErrorHandler.handleError(this, error);
                    }
            );
            popup.showProgressDialog();
        }
    }
}
