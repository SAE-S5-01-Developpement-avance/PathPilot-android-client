package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientService;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

public class AddItinerary extends AppCompatActivity {
    public static final String CLE_ITINERARY_ADDED = "itineraryAdded";
    private Spinner selectClientToAdd;
    private ListView listClientsAddedView;
    private ArrayList<Client> listClientsToAdd;
    private JWTToken jwtToken;
    private ArrayList<Client> listClientsAdded;
    private ArrayAdapter<Client> clientsToAddAdapter;
    private ClientArrayAdapter clientsAddedAdapter;
    private Popup popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_create_itinerary);
        Intent intent = getIntent();
        popup = new Popup(this);
        selectClientToAdd = findViewById(R.id.list_add_clients);
        listClientsAddedView = findViewById(R.id.list_items_clients_added);
        registerForContextMenu(listClientsAddedView);

        listClientsAdded = new ArrayList<>();
        listClientsToAdd = new ArrayList<>();

        listClientsToAdd.add(new Client(getString(R.string.select_client_to_create_itinerary), 0, 0, "", true, "", "", ""));

        listClientsToAdd.addAll((ArrayList<Client>) intent.getSerializableExtra(FragmentItineraries.CLE_LIST_CLIENT));

        clientsToAddAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listClientsToAdd) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getCompanyName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                if (position != 0) {
                    textView.setText(getItem(position).getCompanyName() + " - "
                            + getItem(position).getHomeAddress(AddItinerary.this));
                } else {
                    textView.setText(getItem(position).getCompanyName());
                }
                return view;
            }
        };
        clientsAddedAdapter = new ClientArrayAdapter(this, listClientsAdded);
        listClientsAddedView.setAdapter(clientsAddedAdapter);
        selectClientToAdd.setAdapter(clientsToAddAdapter);

        selectClientToAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (listClientsAdded.size() < 8) {
                    if (position != AdapterView.INVALID_POSITION && position != 0) {
                        Client selectedClient = listClientsToAdd.get(position);
                        listClientsAdded.add(selectedClient);
                        clientsAddedAdapter.notifyDataSetChanged();
                        listClientsToAdd.remove(position);
                        clientsToAddAdapter.notifyDataSetChanged();
                        if (!listClientsToAdd.isEmpty()) {
                            selectClientToAdd.setSelection(0);
                        }
                    }
                } else if (position != 0) {
                    popup.showAlertDialog(getString(R.string.error_title),getString(R.string.error_max_clients_per_itinerary));
                    selectClientToAdd.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });
        findViewById(R.id.button_create_itinerary).setOnClickListener(v -> createItinerary());
        jwtToken = intent.getParcelableExtra(FragmentItineraries.CLE_TOKEN);
    }



    /**
     * Create an itinerary with the clients selected.
     * The itinerary can be create if it has one or more clients attached.
     */
    public void createItinerary() {
        if (listClientsAdded.isEmpty()) {
            popup.showAlertDialog(getString(R.string.error_title),getString(R.string.error_min_clients_per_itinerary));
        } else {
            try {
                if (listClientsAdded.size() > 2) {
                    ArrayList<ArrayList<Double>> locations = new ArrayList<>();
                    // TODO Move to the API
                    //////////////////////////////
                    // TODO get the real location of the salesman
                    locations.add(new ArrayList<>(Arrays.asList(44.35385797, 2.4817177)));

                    // This is steeling here
                    for (Client client : listClientsAdded) {
                        locations.add(new ArrayList<>(Arrays.asList(client.getLatHomeAddress(), client.getLongHomeAddress())));
                    }
                    ItineraryService.getAllDurationsFromClientsOfItinerary(this,locations);

                    // TODO add this part to the function called by AddItinerary in the API
                    ArrayList<Integer> finalItinerary = new ArrayList<>();
                    double itineraryLength = 0;
                    finalItinerary.add(0);
                    navigation(locations.get(0),locations,finalItinerary,itineraryLength);
                    ArrayList<Client> orderedClientList = new ArrayList<>();

                    // Remove the salesman from the list
                    finalItinerary.remove(0);
                    finalItinerary.remove(finalItinerary.size()-1);
                    for (int i : finalItinerary) {
                        orderedClientList.add(listClientsAdded.get(i));
                    }
                    //////////////////////////////

                    // TODO change ordered for listClientsAdded
                    ItineraryService.addItinerary(this,orderedClientList);
                } else {
                    ItineraryService.addItinerary(this,listClientsAdded);
                }

            } catch (JSONException e) {
                popup.showAlertDialog(getString(R.string.error_title),getString(R.string.internal_server_error));
            }
        }
    }

    public JWTToken getJWTToken() {
        return jwtToken;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(this).inflate(R.menu.client_of_itinerary_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Client clientSelected = (Client) listClientsAddedView.getItemAtPosition(info.position);
        int optionSelected = item.getItemId();

        if (optionSelected == R.id.delete_client) {
            listClientsAdded.remove(clientSelected);
            clientsAddedAdapter.notifyDataSetChanged();

            listClientsToAdd.add(clientSelected);
        }
        return (super.onContextItemSelected(item));
    }

    /**
     * Algorithm that do a path by every vertex from one to the next.
     * TODO Move to the API
     * @param vertex
     * @param graph
     * @param finalItinerary
     * @param itineraryLength
     */
    public void navigation(ArrayList<Double> vertex, ArrayList<ArrayList<Double>> graph,
                           ArrayList<Integer> finalItinerary, double itineraryLength) {
        if (finalItinerary.size() != graph.size()+1) {
            int rangMinimum = 0;
            double minimum = Double.MAX_VALUE;
            for (int i = 0; i < vertex.size(); i++) {
                if (!finalItinerary.contains(i) && minimum > vertex.get(i)) {
                    minimum = vertex.get(i);
                    rangMinimum = i;
                }
            }
            finalItinerary.add(rangMinimum);
            itineraryLength += vertex.get(rangMinimum);
            navigation(graph.get(rangMinimum),graph,finalItinerary,itineraryLength);
        }
    }
}
