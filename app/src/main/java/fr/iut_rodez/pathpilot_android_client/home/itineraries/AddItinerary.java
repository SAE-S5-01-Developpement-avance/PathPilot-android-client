package fr.iut_rodez.pathpilot_android_client.home.itineraries;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.home.clients.FragmentClients;

public class AddItinerary extends AppCompatActivity {
    private AutoCompleteTextView selectClientToAdd;
    private Button createItinerary;
    private JWTToken jwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_create_itinerary);

        selectClientToAdd = findViewById(R.id.list_add_clients);
        findViewById(R.id.button_create_itinerary).setOnClickListener(v -> createItinerary());
        Intent intent = getIntent();
        jwtToken = intent.getParcelableExtra(FragmentClients.CLE_TOKEN);
    }

    /**
     * Create an itinerary with the clients selected.
     */
    public void createItinerary() {
        // TODO Create itinerary
        // TODO Create and send to the API
    }
}
