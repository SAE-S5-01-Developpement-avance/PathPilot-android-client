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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.ItineraryPage;
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
    public static final String TOKEN_KEY = "token";
    public static final String LIST_CLIENT_KEY = "listClient";
    public static final String ITINERARY_KEY = "itinerary";

    private ListView listItinerariesView;
    private Home homeActivity;
    private final IItineraryService itineraryService = ServiceFactory.getItineraryService();

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


        //Set header text to itineraries
        ((TextView) view.findViewById(R.id.header_text)).setText(R.string.header_itineraries_list);

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
                        itineraryService.getNextPageItineraries(homeActivity, listItinerariesView, nextLink.href(), (Itinerary.ItineraryArrayAdapter) listItinerariesView.getAdapter());
                    }
                }
            }
        });

        // Get all itineraries from the API
        loadItineraries();

        registerForContextMenu(listItinerariesView);
        view.findViewById(R.id.button_add).setOnClickListener(v -> gotoCreateItinerary());

        listItinerariesView.setOnItemClickListener((parent, view1, position, id) -> {
            Itinerary itinerary = (Itinerary) parent.getItemAtPosition(position);
            Log.d(TAG, "onItemClick: Itinerary: " + itinerary);
            Intent intent = new Intent(getActivity(), InfoItinerary.class);
            intent.putExtra(ITINERARY_KEY, itinerary);
            intent.putExtra(TOKEN_KEY, homeActivity.getJWTToken());
            startActivity(intent);
        });

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
            itineraryService.deleteItinerary(homeActivity, itinerarySelected, listItinerariesView);
        } else {
            Log.e(TAG, "onContextItemSelected: Unknown option selected");
        }
        return (super.onContextItemSelected(item));
    }

    /**
     * Call the service to load all the itineraries from the API.
     */
    public void loadItineraries() {
        itineraryService.getItineraries(homeActivity, listItinerariesView);
    }

    /**
     * Go to the {@link fr.iut_rodez.pathpilot_android_client.home.itinerary.AddItinerary} activity.
     * <p>
     *     Pass the JWT token and the list of clients to the activity.
     */
    private void gotoCreateItinerary() {
        Log.d(TAG, "gotoCreateItinerary: Goto create itinerary");

        Intent intent = new Intent(getActivity(), fr.iut_rodez.pathpilot_android_client.home.itinerary.AddItinerary.class);
        intent.putExtra(TOKEN_KEY, homeActivity.getJWTToken());
        intent.putExtra(LIST_CLIENT_KEY, homeActivity.getClients());
        homeActivity.getAddItineraryLauncher().launch(intent);
    }

    public interface FragmentItineraryActions {
        ActivityResultLauncher<Intent> getAddItineraryLauncher();
        ItineraryPage getItineraryPage();
    }
}
