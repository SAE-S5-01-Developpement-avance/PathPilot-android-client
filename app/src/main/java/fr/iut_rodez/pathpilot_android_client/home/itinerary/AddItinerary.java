package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;

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
        listClientsToAdd = new ArrayList<>(); // TODO stub get the data from intent

        // TODO write in the string file
        listClientsToAdd.add(new Client("Select clients", 0,0,"",true,"","",""));

        listClientsToAdd.addAll((ArrayList<Client>) intent.getSerializableExtra(FragmentItineraries.CLE_LIST_CLIENT));

        clientsToAddAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,listClientsToAdd) {
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
                textView.setText(getItem(position).getCompanyName());
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
                } else if (position != 0){
                    popup.showAlertDialog("error","Max 8 clients"); //TODO add to the strings files
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
            // TODO Add the right error message and add it to the strings files
            popup.showAlertDialog("ERROR","You have to add a client");
        } else {
            try {
                ItineraryService.addItinerary(this,listClientsAdded);
            } catch (JSONException e) {
                // TODO i18n
                popup.showAlertDialog("Error","Création de la requete d'ajout d'un itinéraire");
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
