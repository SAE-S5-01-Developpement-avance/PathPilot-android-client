package fr.iut_rodez.pathpilot_android_client.home.clients;

import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;
import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.model.Client;
import fr.iut_rodez.pathpilot_android_client.model.Client.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;

/**
 * Service to handle all client related requests
 */
public class ClientService {

    public static final String API_BASE_URL = BuildConfig.API_BASE_URL + "api/clients";
    private static final String TAG = ClientService.class.getSimpleName();

    /**
     * Request to the API the clients.
     * If the request is successful, it adds the client to the adapter and links it to the view
     * If not it displays the error encounter.
     *
     * @param context         Context of the application
     * @param listClientsView The view where the clients will be displayed
     */
    public static void getClients(Context context, ListView listClientsView) {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        Home homeActivity = (Home) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = homeActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_BASE_URL, null,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        List<Client> clientsArray = new ArrayList<>();
                        if (response.has("_embedded")) {
                            JSONArray clients = response.getJSONObject("_embedded").getJSONArray("clientList");
                            Log.d(TAG, "getClients: " + clients);
                            for (int i = 0; i < clients.length(); i++) {
                                clientsArray.add(new Client(clients.getJSONObject(i)));
                            }
                        }

                        Log.d(TAG, "getClients: " + clientsArray);

                        ClientArrayAdapter adapter = new ClientArrayAdapter(homeActivity, clientsArray);
                        listClientsView.post(() -> {
                            listClientsView.setAdapter(adapter);
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Request to the API to add a client.
     * If the request is successful, it goes back to the previous activity.
     *
     * @param context Context of the application
     * @param client  The client to add
     */
    public static void addClient(Context context, Client client) {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        AddClient addClientActivity = (AddClient) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = addClientActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_BASE_URL, client.toJson(),
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AddClient.CLE_CLIENT_ADDED, true);
                    addClientActivity.setResult(AddClient.RESULT_OK, returnIntent);

                    addClientActivity.finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken);
                return headers;
            }
        };

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
    public static void deleteClient(Home homeActivity, Client clientSelected, ListView listClientsView) {

        String apiURLDelete = API_BASE_URL + "/" + clientSelected.getId();

        RequestQueue requestQueue = getRequestQueue(homeActivity);
        String jwtToken = homeActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(homeActivity);
        progressDialog.show();

        Log.d(TAG, "deleteClient: " + clientSelected.getId());

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.DELETE, apiURLDelete, null, jwtToken,
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
