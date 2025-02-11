package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;

public class SaveItinerary extends AppCompatActivity {
    private ListView orderedClientsListView;
    private List<Client> clientsList;
    private ClientArrayAdapter orderedClientsListAdapter;
    private JWTToken jwtToken;
    private Itinerary itinerary;
    private IItineraryService itineraryService = ServiceFactory.getItineraryService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_save_itinerary);
        ((TextView) findViewById(R.id.header_text)).setText(R.string.header_save_itinerary);
        findViewById(R.id.backButton).setOnClickListener(v -> cancelItineraryCreation());

        Intent intent = getIntent();
        itinerary =(Itinerary) intent.getParcelableExtra(AddItinerary.KEY_ITINERARY_OBJECT);
        jwtToken = intent.getParcelableExtra(FragmentItineraries.TOKEN_KEY);
        orderedClientsListView = findViewById(R.id.list_items_clients_ordered);

        clientsList = new ArrayList<>();
        clientsList.addAll(itinerary.getClients());

        orderedClientsListAdapter = new ClientArrayAdapter(this, clientsList);
        orderedClientsListView.setAdapter(orderedClientsListAdapter);

        findViewById(R.id.button_save_itinerary).setOnClickListener(v -> saveItinerary());
        findViewById(R.id.button_cancel_itinerary_creation).setOnClickListener(v -> cancelItineraryCreation());
    }

    public JWTToken getJWTToken() {
        return jwtToken;
    }

    public void saveItinerary() {
        Intent intent = new Intent(this, AddItinerary.class);
        setResult(AddItinerary.RESULT_OK, intent);
        intent.putExtra(AddItinerary.ITINERARY_ADDED_KEY, true);
        finish();
    }

    public void cancelItineraryCreation() {
        itineraryService.deleteItineraryToCancelTheCreation(
                this,itinerary.getId());
        Intent returnIntent = new Intent(this, AddItinerary.class);
        this.setResult(AddItinerary.RESULT_OK, returnIntent);
        returnIntent.putExtra(AddItinerary.ITINERARY_ADDED_KEY, false);
        this.finish();
    }
}
