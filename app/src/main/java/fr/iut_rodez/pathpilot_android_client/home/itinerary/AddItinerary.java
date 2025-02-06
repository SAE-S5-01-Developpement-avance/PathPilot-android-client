package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class AddItinerary extends AppCompatActivity {
    public static final String ITINERARY_ADDED_KEY = "itineraryAdded";
    private Spinner selectClientToAdd;
    private ListView listClientsAddedView;
    private ArrayList<Client> listClientsToAdd;
    private JWTToken jwtToken;
    private ArrayList<Client> listClientsAdded;
    private ArrayAdapter<Client> clientsToAddAdapter;
    private ClientArrayAdapter clientsAddedAdapter;
    private Popup popup;
    private final IItineraryService itineraryService = ServiceFactory.getItineraryService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_create_itinerary);
        ((TextView) findViewById(R.id.header_text)).setText(R.string.header_create_itinerary);
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        selectClientToAdd = findViewById(R.id.list_add_clients);
        listClientsAddedView = findViewById(R.id.list_items_clients_added);
        registerForContextMenu(listClientsAddedView);

        popup = new Popup(this);
        listClientsAdded = new ArrayList<>();
        listClientsToAdd = new ArrayList<>();

        // Add a default client to the list of clients to add
        // This client is used to display a hint in the spinner
        listClientsToAdd.add(new Client(getString(R.string.select_client_to_create_itinerary), 0, 0, "", true, "", "", ""));

        Intent intent = getIntent();
        Serializable serializableExtra = intent.getSerializableExtra(FragmentItineraries.LIST_CLIENT_KEY);

        ArrayList<Client> clients = serializableExtra == null ? new ArrayList<>() : (ArrayList<Client>) serializableExtra;
        listClientsToAdd.addAll(clients);

        clientsToAddAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listClientsToAdd) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(getItem(position).getCompanyName());
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                Client client = getItem(position);
                if (position != 0) {
                    textView.setText(client.getCompanyName() + " - " + client.getAddressDisplayName());
                } else {
                    textView.setText(client.getCompanyName());
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
                    popup.showAlertDialog(getString(R.string.error_title), getString(R.string.error_max_clients_per_itinerary));
                    selectClientToAdd.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });
        findViewById(R.id.button_create_itinerary).setOnClickListener(v -> createItinerary());
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        jwtToken = intent.getParcelableExtra(FragmentItineraries.TOKEN_KEY);
    }


    /**
     * Create an itinerary with the clients selected.
     * The itinerary can be created if it has one or more clients attached.
     */
    public void createItinerary() {
        if (listClientsAdded.isEmpty()) {
            popup.showAlertDialog(getString(R.string.error_title), getString(R.string.error_min_clients_per_itinerary));
        } else {
            try {
                itineraryService.addItinerary(this, listClientsAdded);
            } catch (JSONException e) {
                popup.showAlertDialog(getString(R.string.error_title), getString(R.string.internal_server_error));
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
}
