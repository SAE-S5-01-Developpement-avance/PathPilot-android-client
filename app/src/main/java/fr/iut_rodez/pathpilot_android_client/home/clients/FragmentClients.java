package fr.iut_rodez.pathpilot_android_client.home.clients;

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
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientPage;
import fr.iut_rodez.pathpilot_android_client.util.Link;

/**
 * Display all the clients
 * Handle all the clients related actions
 */
public class FragmentClients extends Fragment {

    /**
     * The icon of the tab.
     *
     * @see fr.iut_rodez.pathpilot_android_client.home.Home
     */
    public static final int ICON = R.drawable.icon_clients;
    private static final String TAG = FragmentClients.class.getSimpleName();
    public static final String TOKEN_KEY = "token";

    private ListView listClientsView;
    private boolean isLoading = false;
    private Home homeActivity;
    private final IClientService clientService = ServiceFactory.getClientService();



    public static FragmentClients newInstance() {
        return new FragmentClients();
    }

    private FragmentClients() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clients, container, false);
        homeActivity = (Home) getActivity();

        //Set header text to itineraries
        ((TextView) view.findViewById(R.id.header_text)).setText(R.string.header_clients_list);

        listClientsView = view.findViewById(R.id.clients_list);

        // Set OnScrollListener to load more clients when reaching the bottom
        listClientsView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // No action needed here
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount && totalItemCount > 0 && !isLoading) {
                    // Check if there is a next page link
                    Link nextLink = homeActivity.getClientPage().getNext();
                    if (nextLink != null) {
                        isLoading = true;
                        clientService.getNextPageClients(homeActivity, listClientsView, nextLink.href(), (ClientArrayAdapter) listClientsView.getAdapter(), () -> isLoading = false);
                    }
                }
            }
        });

        // Get the clients from the API
        loadClients();

        registerForContextMenu(listClientsView);
        view.findViewById(R.id.button_add).setOnClickListener(v -> gotoCreateClient());

        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(getActivity()).inflate(R.menu.client_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Client clientSelected = (Client) listClientsView.getItemAtPosition(info.position);
        int optionSelected = item.getItemId();

        if (optionSelected == R.id.delete_client) {
            Log.d(TAG, "onContextItemSelected: Delete client");
            clientService.deleteClient(homeActivity, clientSelected, listClientsView);
        } else {
            Log.e(TAG, "onContextItemSelected: Unknown option selected");
        }
        return (super.onContextItemSelected(item));
    }

    public void loadClients() {
        clientService.getClients(homeActivity, listClientsView);
    }

    private void gotoCreateClient() {
        Log.d(TAG, "gotoCreateClient: Goto create client");
        Intent intent = new Intent(getActivity(), AddClient.class);
        intent.putExtra(TOKEN_KEY, homeActivity.getJWTToken());

        homeActivity.getAddClientLauncher().launch(intent);
    }

    public interface FragmentClientsActions {
        ActivityResultLauncher<Intent> getAddClientLauncher();
        ClientPage getClientPage();
    }

    /**
     * Get the list of clients displayed in the list view
     * <p>
     *     It's use in the {@link fr.iut_rodez.pathpilot_android_client.home.itinerary.AddItinerary} activity
     * </p>
     * @return The list of clients displayed in the list view
     */
    public ArrayList<Client> getListClients() {
        ArrayList<Client> listClients = new ArrayList<>();
        for (int i = 0; listClientsView.getAdapter().getCount() > i; i++) {
            listClients.add((Client) listClientsView.getAdapter().getItem(i));
        }
        return listClients;
    }
}
