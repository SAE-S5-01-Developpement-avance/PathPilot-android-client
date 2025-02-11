package fr.iut_rodez.pathpilot_android_client.home.routes;

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
import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.FragmentItineraries;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.service.IRouteService;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class AddRoute extends AppCompatActivity {
    public static final String CLE_ROUTE_ADDED = "routeAdded";
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
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        selectItinerary = findViewById(R.id.select_itinerary);
        popup = new Popup(this);
        listItineraries = new ArrayList<>();

        Intent intent = getIntent();
        Serializable serializableExtra = intent.getSerializableExtra(FragmentItineraries.ITINERARY_KEY);
        ArrayList<Itinerary> itineraries = serializableExtra == null ? new ArrayList<>() : (ArrayList<Itinerary>) serializableExtra;
        listItineraries.addAll(itineraries);

        itinerariesArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listItineraries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getDisplayName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getDisplayName());
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
        jwtToken = intent.getParcelableExtra(FragmentItineraries.CLE_TOKEN);
    }

    public void createRoute() {
        if (selectedItinerary == null) {
            popup.showAlertDialog(getString(R.string.error_title), getString(R.string.error_select_itinerary));
        } else {
            routeService.createRoute(this, jwtToken, selectedItinerary,
                    response -> {
                        popup.dismissProgressDialog();
                        Log.d(TAG, "createRoute: " + response);

                        Route route = Parser.getRoute(response);
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
