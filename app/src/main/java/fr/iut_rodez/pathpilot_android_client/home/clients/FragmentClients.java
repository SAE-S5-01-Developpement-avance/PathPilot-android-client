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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.model.Client;

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
    public static final String CLE_TOKEN = "token";

    private ImageButton addClientButton;
    private ListView listClientsView;
    private Home homeActivity;

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

        addClientButton = view.findViewById(R.id.add_client);
        listClientsView = view.findViewById(R.id.clients_list);

        // Get the clients from the API
        loadClients();

        registerForContextMenu(listClientsView);

        addClientButton.setOnClickListener(v -> gotoCreateClient());

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
            ClientService.deleteClient(homeActivity, clientSelected, listClientsView);
        } else {
            Log.e(TAG, "onContextItemSelected: Unknown option selected");
        }
        return (super.onContextItemSelected(item));
    }

    public void loadClients() {
        ClientService.getClients(homeActivity, listClientsView);
    }

    private void gotoCreateClient() {
        Log.d(TAG, "gotoCreateClient: Goto create client");
        Intent intent = new Intent(getActivity(), fr.iut_rodez.pathpilot_android_client.home.clients.AddClient.class);
        intent.putExtra(CLE_TOKEN, homeActivity.getJWTToken());

        homeActivity.getAddClientLauncher().launch(intent);
    }

    public interface AddClient {
        ActivityResultLauncher<Intent> getAddClientLauncher();
    }
}
