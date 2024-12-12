package fr.iut_rodez.pathpilot_android_client.home.itineraries;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientService;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients;
import fr.iut_rodez.pathpilot_android_client.model.Client;
import fr.iut_rodez.pathpilot_android_client.model.Itinerary;

public class AddItinerary extends AppCompatActivity {
    private AutoCompleteTextView selectClientToAdd;
    private ListView listClientsAddedView;
    private ArrayList<Client> listClientsToAdd;
    private JWTToken jwtToken;
    private ArrayList<Client> listClientsAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_create_itinerary);

        selectClientToAdd = findViewById(R.id.list_add_clients);
        listClientsAddedView = findViewById(R.id.list_items_clients_added);

        listClientsAdded = new ArrayList<>();
        listClientsToAdd = new ArrayList<>();
        
        findViewById(R.id.button_create_itinerary).setOnClickListener(v -> createItinerary());
        Intent intent = getIntent();
        jwtToken = null;// TODO intent.getParcelableExtra(FragmentItinerary.CLE_TOKEN);
    }

    /**
     * Create an itinerary with the clients selected.
     */
    public void createItinerary() {
        // TODO Send the Itinerary to the API
        new Itinerary(listClientsAdded);
        resetField();
    }

    /**
     * Reset the content of "add itinerary" interface.
     */
    public void resetField() {
        listClientsAddedView.removeAllViews();
        selectClientToAdd.clearListSelection();
    }

    public JWTToken getJWTToken() {
        return jwtToken;
    }
}
