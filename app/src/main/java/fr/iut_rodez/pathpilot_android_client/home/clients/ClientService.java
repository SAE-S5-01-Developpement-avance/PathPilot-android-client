package fr.iut_rodez.pathpilot_android_client.home.clients;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.createAuthenticatedRequest;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.util.Parser;

/**
 * Service to handle all client related requests
 */
public class ClientService implements IClientService {

    /**
     * Request to the API the clients.
     * If the request is successful, it adds clients to the adapter and link it to the view
     * If not it displays the error encounter.
     *
     * @param context         Context of the application
     * @param listClientsView The view where the clients will be displayed
     */
    @Override
    public void getClients(Context context, ListView listClientsView) {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        Home homeActivity = (Home) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = homeActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.GET, API_BASE_URL, null, jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);

                    ClientPage clientPage = Parser.getClientsPageable(response);
                    Log.d(TAG, "getClients: " + clientPage);

                    ClientArrayAdapter adapter = new ClientArrayAdapter(homeActivity, clientPage.clients());
                    listClientsView.post(() -> {
                        listClientsView.setAdapter(adapter);
                    });

                    // Save the client page to the activity
                    ((Home) context).setClientPage(clientPage);
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        );
        
        requestQueue.add(request);
    }

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
    @Override
    public void getNextPageClients(Context context, ListView listClientsView, String nextPageUrl, ClientArrayAdapter adapter) {
        Log.d(TAG, "Next Page URL: " + nextPageUrl);

        Home homeActivity = (Home) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = homeActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.GET, nextPageUrl, null, jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);

                    ClientPage clientPage = Parser.getClientsPageable(response);
                    Log.d(TAG, "getNextPageClients: " + clientPage);

                    adapter.addAll(clientPage.clients());
                    adapter.notifyDataSetChanged();

                    // Save the client page to the activity
                    ((Home) context).setClientPage(clientPage);
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        );

        requestQueue.add(request);
    }

    /**
     * Request to the API to add a client.
     * If the request is successful, it goes back to the previous activity.
     *
     * @param context Context of the application
     * @param client  The client to add
     */
    @Override
    public void addClient(Context context, Client client) {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        AddClient addClientActivity = (AddClient) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = addClientActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JSONObject body = client.toJson();

        Log.d(TAG, "addClient: " + body);

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.POST, API_BASE_URL, body, jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AddClient.ADDED_CLIENT_KEY, true);
                    addClientActivity.setResult(AddClient.RESULT_OK, returnIntent);

                    addClientActivity.finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        );

        requestQueue.add(request);
    }

    /**
     * Request to the API to delete a client.
     * If the request is successful, it removed the client from the adapter
     *
     * @param homeActivity    Context of the application
     * @param clientSelected  The client to delete
     * @param listClientsView The view where the clients will be displayed
     */
    @Override
    public void deleteClient(Home homeActivity, Client clientSelected, ListView listClientsView) {

        String apiURLDelete = API_BASE_URL + "/" + clientSelected.getId();

        RequestQueue requestQueue = getRequestQueue(homeActivity);
        String jwtToken = homeActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(homeActivity);
        progressDialog.show();

        Log.d(TAG, "deleteClient: " + clientSelected.getId());

        JsonObjectRequest request = createAuthenticatedRequest(Request.Method.DELETE, apiURLDelete, null, jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    listClientsView.post(() -> {
                        ClientArrayAdapter clientArrayAdapter = (ClientArrayAdapter) listClientsView.getAdapter();
                        clientArrayAdapter.remove(clientSelected);
                        clientArrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, "deleteClient: Client" + clientSelected.getId() + "removed");
                    });
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(homeActivity, error);
                }
        );
        requestQueue.add(request);
    }
}
