package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;

/**
 * Display all itineraries
 * Handle all the itineraries related actions
 */
public class FragmentItineraries extends Fragment {

    /**
     * The tab's icon.
     *
     * @see fr.iut_rodez.pathpilot_android_client.home.Home
     */
    public static final int ICON = R.drawable.icon_list;
    private static final String TAG = FragmentItineraries.class.getSimpleName();
    public static final String CLE_TOKEN = "token";

    private ImageButton addItineraryButton;
    private ListView listItinerariesView;
    private Home homeActivity;
    private TextView textHeader;

    public static FragmentItineraries newInstance() {
        return new FragmentItineraries();
    }

    private FragmentItineraries() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_itineraries, container, false);
        homeActivity = (Home) getActivity();

        addItineraryButton = view.findViewById(R.id.button_add);

        //Set header text to itineraries
        textHeader = view.findViewById(R.id.header_text);
        textHeader.setText(R.string.header_itineraries_list);
        listItinerariesView = view.findViewById(R.id.itineraries_list);

        // Get all itineraries from the API
        loadItineraries();

        registerForContextMenu(listItinerariesView);

        addItineraryButton.setOnClickListener(v -> gotoCreateItinerary());

        return view;
    }

    public void loadItineraries() {
        ItineraryService.getItineraries(homeActivity, listItinerariesView);
    }

    private void gotoCreateItinerary() {
        Log.d(TAG, "gotoCreateItinerary: Goto create itinerary");

        Intent intent = new Intent(getActivity(), AddItinerary.class);
        intent.putExtra(CLE_TOKEN, homeActivity.getJWTToken());
        homeActivity.getAddClientLauncher().launch(intent);
    }
}
