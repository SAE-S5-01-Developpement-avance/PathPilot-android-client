package fr.iut_rodez.pathpilot_android_client.home.clients;

import android.content.Context;
import android.widget.ListView;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientArrayAdapter;

public interface IClientService {
    String API_BASE_URL = BuildConfig.API_BASE_URL + "clients";
    String TAG = ClientService.class.getSimpleName();

    /**
     * Request to the API the clients.
     * If the request is successful, it adds clients to the adapter and link it to the view
     * If not it displays the error encounter.
     *
     * @param context         Context of the application
     * @param listClientsView The view where the clients will be displayed
     */
    void getClients(Context context, ListView listClientsView);

    /**
     * Request to the API to fetch the next page of clients.
     * If the request is successful, it adds clients to the adapter and link it to the view
     * If not it displays the error encounter.
     *
     * @param context         Context of the application
     * @param listClientsView The view where the clients will be displayed
     * @param nextPageUrl     The URL of the next page
     * @param adapter         The adapter to add the clients to
     */
    void getNextPageClients(Context context, ListView listClientsView, String nextPageUrl, ClientArrayAdapter adapter);

    /**
     * Request to the API to add a client.
     * If the request is successful, it goes back to the previous activity.
     *
     * @param context Context of the application
     * @param client  The client to add
     */
    void addClient(Context context, Client client);

    /**
     * Request to the API to delete a client.
     * If the request is successful, it removed the client from the adapter
     *
     * @param homeActivity    Context of the application
     * @param clientSelected  The client to delete
     * @param listClientsView The view where the clients will be displayed
     */
    void deleteClient(Home homeActivity, Client clientSelected, ListView listClientsView);
}
