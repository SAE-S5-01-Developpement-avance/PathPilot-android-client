package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import static fr.iut_rodez.pathpilot_android_client.home.itinerary.ItineraryService.getNextPageItineraries;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.util.Link;

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
    public static final String CLE_LIST_CLIENT = "listClient";

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

        // Set OnScrollListener to load more itineraries when reaching the bottom
        listItinerariesView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No action needed here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0) {
                    // Check if there is a next page link
                    Link nextLink = homeActivity.getItineraryPage().getNext();
                    if (nextLink != null) {
                        getNextPageItineraries(homeActivity, listItinerariesView, nextLink.href(), (Itinerary.ItineraryArrayAdapter) listItinerariesView.getAdapter());
                    }
                }
            }
        });

        // Get all itineraries from the API
        loadItineraries();

        registerForContextMenu(listItinerariesView);

        addItineraryButton.setOnClickListener(v -> gotoCreateItinerary());

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(getActivity()).inflate(R.menu.itineray_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Itinerary itinerarySelected = (Itinerary) listItinerariesView.getItemAtPosition(info.position);
        int optionSelected = item.getItemId();

        if (optionSelected == R.id.delete_itinerary) {
            Log.d(TAG, "onContextItemSelected: Delete itinerary");
            ItineraryService.deleteItinerary(homeActivity, itinerarySelected, listItinerariesView);
        } else {
            Log.e(TAG, "onContextItemSelected: Unknown option selected");
        }
        return (super.onContextItemSelected(item));
    }

    public void loadItineraries() {
        ItineraryService.getItineraries(homeActivity, listItinerariesView);
    }

    private void gotoCreateItinerary() {
        Log.d(TAG, "gotoCreateItinerary: Goto create itinerary");

        Intent intent = new Intent(getActivity(), fr.iut_rodez.pathpilot_android_client.home.itinerary.AddItinerary.class);
        intent.putExtra(CLE_TOKEN, homeActivity.getJWTToken());
        intent.putExtra(CLE_LIST_CLIENT, homeActivity.getClients());
        homeActivity.getAddItineraryLauncher().launch(intent);
    }

    public interface FragmentItineraryActions {
        ActivityResultLauncher<Intent> getAddItineraryLauncher();
        ItineraryPage getItineraryPage();
    }
}
