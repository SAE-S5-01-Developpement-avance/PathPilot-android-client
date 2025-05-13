package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.ItineraryPage;
import fr.iut_rodez.pathpilot_android_client.util.Link;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

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
    private boolean isLoading = false;
    private Home homeActivity;
    private Popup popup;

    private List<Itinerary> itineraries;
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
        popup = new Popup(homeActivity);


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
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0 && !isLoading) {
                    // Check if there is a next page link
                    Link nextLink = homeActivity.getItineraryPage().getNext();
                    if (nextLink != null) {
                        isLoading = true;
                        itineraryService.getNextPageItineraries(homeActivity, listItinerariesView, nextLink.href(),
                                (Itinerary.ItineraryArrayAdapter) listItinerariesView.getAdapter(),
                                () -> isLoading = false);
                    }
                }
            }
        });
        view.findViewById(R.id.refresh_itineraries_btn).setOnClickListener(v -> {
            ((Itinerary.ItineraryArrayAdapter) listItinerariesView.getAdapter()).clear();
            loadItineraries();
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
            homeActivity.getInfoItineraryLauncher().launch(intent);
        });
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(getActivity()).inflate(R.menu.itinerary_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        int optionSelected = item.getItemId();

        if (optionSelected == R.id.delete_itinerary) {
            Itinerary itinerarySelected = (Itinerary) listItinerariesView.getItemAtPosition(info.position);
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
        popup.showProgressDialog();
        itineraryService.getItineraries(homeActivity, response -> {
            popup.dismissProgressDialog();
            Log.d(TAG, "onResponse: " + response);

            ItineraryPage itineraryPage = Parser.getItinerariesPageable(response);
            Log.d(TAG, "getItineraries: " + itineraryPage.itineraries());

            itineraryPage.itineraries().forEach(
                    itinerary -> itinerary.getClients().forEach(client -> client.setAddressDisplayName(homeActivity))
            );
            Itinerary.ItineraryArrayAdapter adapter = new Itinerary.ItineraryArrayAdapter(homeActivity,
                    itineraryPage.itineraries());
            listItinerariesView.post(() -> {
                listItinerariesView.setAdapter(adapter);
            });

            // Save the client page to the activity
            ((Home) homeActivity).setItineraryPage(itineraryPage);
        }, error -> {
            popup.dismissProgressDialog();
            Log.e(TAG, "onErrorResponse: ", error);
            handleError(homeActivity, error);
        });
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
    /**
     * Get the list of itineraries displayed in the list view
     * <p>
     *     It's use in the {@link fr.iut_rodez.pathpilot_android_client.home.routes.AddRoute} activity
     * </p>
     * @return The list of itineraries displayed in the list view
     */
    public ArrayList<Itinerary> getListItineraries() {
        Log.d(TAG, "getListItineraries: Get list of itineraries");
        ArrayList<Itinerary> listItineraries = new ArrayList<>();
        ListAdapter adapter =  listItinerariesView.getAdapter();
        if (adapter != null) {
            for (int i = 0; adapter.getCount() > i; i++) {
                listItineraries.add((Itinerary) adapter.getItem(i));
            }
            Log.d(TAG, "getListItineraries: List of itineraries: " + listItineraries);
        } else {
            Log.e(TAG, "getListItineraries: Adapter is null");

        }

        return listItineraries;
    }

    public interface FragmentItineraryActions {
        ActivityResultLauncher<Intent> getAddItineraryLauncher();
        ItineraryPage getItineraryPage();
    }
}
