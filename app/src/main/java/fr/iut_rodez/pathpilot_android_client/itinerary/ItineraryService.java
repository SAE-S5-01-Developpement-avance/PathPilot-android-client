package fr.iut_rodez.pathpilot_android_client.itinerary;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.AddClient;
import fr.iut_rodez.pathpilot_android_client.model.Client;
import fr.iut_rodez.pathpilot_android_client.model.Client.ClientArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;

/**
 * Service to handle all itineraries related requests
 */
public class ItineraryService {

    public static final String API_BASE_URL = BuildConfig.API_BASE_URL + "api/routes";
    private static final String TAG = ItineraryService.class.getSimpleName();

    /**
     * Request to the API the itineraries.
     * If the request is successful, it add itineraries to the adapter and link them to the view
     * If not it displays the error encounter.
     *
     * @param context         Context of the application
     * @param listItinerariesView The view where the itineraries will be displayed
     */
    public static void getItineraries(Context context, ListView listItinerariesView) {
//        Log.d(TAG, "API URL: " + API_BASE_URL);
//
//        Home homeActivity = (Home) context;
//        RequestQueue requestQueue = getRequestQueue(context);
//        String jwtToken = homeActivity.getJWTToken().getToken();
//
//        ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.show();
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_BASE_URL, null,
//                response -> {
//                    progressDialog.dismiss();
//                    Log.d(TAG, "onResponse: " + response);
//                    try {
//                        List<Client> itineraryArray = new ArrayList<>();
//                        if (response.has("_embedded")) {
//                            JSONArray itineraries = response.getJSONObject("_embedded").getJSONArray("clientList");
//                            Log.d(TAG, "getClients: " + itineraries);
//                            for (int i = 0; i < itineraries.length(); i++) {
//                                itineraryArray.add(new Client(itineraries.getJSONObject(i)));
//                            }
//                        }
//
//                        Log.d(TAG, "getClients: " + itineraryArray);
//
//                        ClientArrayAdapter adapter = new ClientArrayAdapter(homeActivity, itineraryArray);
//                        listItinerariesView.post(() -> {
//                            listItinerariesView.setAdapter(adapter);
//                        });
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                },
//                error -> {
//                    progressDialog.dismiss();
//                    Log.e(TAG, "onErrorResponse: ", error);
//                    handleError(context, error);
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer " + jwtToken);
//                return headers;
//            }
//        };
//
//        requestQueue.add(request);
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
