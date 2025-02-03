package fr.iut_rodez.pathpilot_android_client.home.itinerary;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.Itinerary.ItineraryArrayAdapter;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;

/**
 * Service to handle all itinerary related requests
 */
public class ItineraryService {

    public static final String API_BASE_URL = BuildConfig.API_BASE_URL + "itineraries";
    private static final String TAG = ItineraryService.class.getSimpleName();

    /**
     * Request to the API to add an itinerary.
     * If the request is successful, it goes back to the previous activity.
     *
     * @param context     Context of the application
     * @param listClients The list of clients to create an itinerary
     */
    public static void addItinerary(Context context, List<Client> listClients) throws JSONException {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        AddItinerary addItineraryActivity = (AddItinerary) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = addItineraryActivity.getJWTToken().getToken();
        JSONArray listIdClient = new JSONArray();
        JSONObject itinerariesInput = new JSONObject();

        for (Client client : listClients) {
            listIdClient.put(client.getId());
        }
        itinerariesInput.put("clients_schedule", listIdClient);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.POST, API_BASE_URL, itinerariesInput, jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);

                    Intent returnIntent = new Intent(addItineraryActivity, Home.class);
                    addItineraryActivity.setResult(AddItinerary.RESULT_OK, returnIntent);
                    returnIntent.putExtra(AddItinerary.CLE_ITINERARY_ADDED, true);
                    addItineraryActivity.finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                });
        requestQueue.add(request);
    }

    /**
     * Request to the API the itineraries.
     * If the request is successful, it add itineraries to the adapter and link them to the view
     * If not it displays the error encounter.
     *
     * @param context             Context of the application
     * @param listItinerariesView The view where the itineraries will be displayed
     */
    public static void getItineraries(Context context, ListView listItinerariesView) {
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

                    ItineraryPage itineraryPage = Parser.getItinerariesPageable(response);
                    Log.d(TAG, "getItineraries: " + itineraryPage.itineraries());

                    ItineraryArrayAdapter adapter = new ItineraryArrayAdapter(homeActivity, itineraryPage.itineraries());
                    listItinerariesView.post(() -> {
                        listItinerariesView.setAdapter(adapter);
                    });

                    // Save the client page to the activity
                    ((Home) context).setItineraryPage(itineraryPage);
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
     * @param listItinerariesView The view where the clients will be displayed
     * @param nextPageUrl     The URL of the next page
     * @param adapter         The adapter to add the clients to
     */
    public static void getNextPageItineraries(Context context, ListView listItinerariesView, String nextPageUrl, ItineraryArrayAdapter adapter) {
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

                    ItineraryPage itineraryPage = Parser.getItinerariesPageable(response);
                    Log.d(TAG, "getNextItineraryPageable: " + itineraryPage);

                    adapter.addAll(itineraryPage.itineraries());
                    adapter.notifyDataSetChanged();

                    // Save the client page to the activity
                    ((Home) context).setItineraryPage(itineraryPage);
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
     * Request to the API to delete an itinerary.
     * If the request is successful, it removed the itinerary from the adapter
     *
     * @param homeActivity    Context of the application
     * @param itinerarySelected  The itinerary to delete
     * @param listItinerariesView The view where the itineraries will be displayed
     */
    public static void deleteItinerary(Home homeActivity, Itinerary itinerarySelected, ListView listItinerariesView) {

        String apiURLDelete = API_BASE_URL + "/" + itinerarySelected.getId();

        RequestQueue requestQueue = getRequestQueue(homeActivity);
        String jwtToken = homeActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(homeActivity);
        progressDialog.show();

        Log.d(TAG, "deleteItinerary: " + itinerarySelected.getId());

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.DELETE, apiURLDelete, null, jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    listItinerariesView.post(() -> {
                        ItineraryArrayAdapter itineraryArrayAdapter = (ItineraryArrayAdapter) listItinerariesView.getAdapter();
                        itineraryArrayAdapter.remove(itinerarySelected);
                        itineraryArrayAdapter.notifyDataSetChanged();
                        Log.d(TAG, "deleteClient: Itinerary" + itinerarySelected.getId() + "removed");
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
