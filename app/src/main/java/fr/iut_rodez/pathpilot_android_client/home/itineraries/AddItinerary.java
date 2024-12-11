package fr.iut_rodez.pathpilot_android_client.home.itineraries;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;

public class AddItinerary extends AppCompatActivity {
    private AutoCompleteTextView selectClientToAdd;
    private Button createItinerary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_create_itinerary);

        selectClientToAdd = findViewById(R.id.list_add_clients);
        findViewById(R.id.button_create_itinerary).setOnClickListener(v -> createItinerary());
    }

    /**
     * Create an itinerary with the clients selected.
     */
    public void createItinerary() {
        // TODO Create itinerary
        // TODO Create and send to the API
    }
}
