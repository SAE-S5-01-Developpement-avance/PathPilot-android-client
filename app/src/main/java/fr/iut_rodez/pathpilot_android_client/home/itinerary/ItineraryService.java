package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.createAuthenticatedRequest;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.AddClient;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;

public class ItineraryService {

    public static final String API_BASE_URL = BuildConfig.API_BASE_URL + "routes";
    public static final String API_ORS_MATRIX_URL = "https://api.openrouteservice.org/v2/matrix/driving-car?profile=driving-car";
    private static final String TAG = ItineraryService.class.getSimpleName();
    /**
     * Request to the API to add an itinerary.
     * If the request is successful, it goes back to the previous activity.
     * @param context Context of the application
     * @param listClients The list of clients to create an itinerary
     */
    public static void addItinerary(Context context, List<Client> listClients) throws JSONException {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        AddItinerary addItineraryActivity = (AddItinerary) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = addItineraryActivity.getJWTToken().getToken();
        JSONArray listIdClient = new JSONArray();
        JSONObject itinerariesInput = new JSONObject();

        for (Client client: listClients) {
            listIdClient.put(client.getId());
        }
        itinerariesInput.put("clients_schedule",listIdClient);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.POST,API_BASE_URL,itinerariesInput,jwtToken,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);

                    Intent returnIntent = new Intent(addItineraryActivity, Home.class);
                    addItineraryActivity.setResult(AddItinerary.RESULT_OK, returnIntent);
                    returnIntent.putExtra(AddItinerary.CLE_ITINERARY_ADDED,true);
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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_BASE_URL, null,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        List<Itinerary> itineraryArray = new ArrayList<>();
                        if (response.has("_embedded")) {
                            JSONArray itineraries = response.getJSONObject("_embedded").getJSONArray("routeList");
                            Log.d(TAG, "getItineraries: " + itineraries);
                            for (int i = 0; i < itineraries.length(); i++) {
                                itineraryArray.add(new Itinerary(itineraries.getJSONObject(i)));
                            }
                        }

                        Log.d(TAG, "getItineraries: " + itineraryArray);

                        Itinerary.ItineraryArrayAdapter adapter = new Itinerary.ItineraryArrayAdapter(homeActivity, itineraryArray);
                        listItinerariesView.post(() -> {
                            listItinerariesView.setAdapter(adapter);
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken);
                return headers;
            }
        };

        requestQueue.add(request);
    }

    /**
     * Ask to the ORS Matrix API the durations enter the sent locations.
     * @param context Context of the application.
     * @param locations The list of locations.
     */
    public static void getAllDurationsFromClientsOfItinerary(Context context, ArrayList<ArrayList<Double>> locations) {
        Log.d(TAG, "API URL: " + API_ORS_MATRIX_URL);

        // TODO Get out the API_KEY
        String API_KEY = "5b3ce3597851110001cf6248a7c14d937e0a4c0d850c723cff110a2b";

        RequestQueue requestQueue = getRequestQueue(context);
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JSONObject requestBody = new JSONObject();
        try {
            JSONArray locationsJson = new JSONArray();
            for (ArrayList<Double> location : locations){
                locationsJson.put(new JSONArray(location));
            }
            requestBody.put("locations", locationsJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_ORS_MATRIX_URL,requestBody,
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        locations.clear();
                        if (response.has("durations")) {
                            JSONArray clientsDurations = response.getJSONArray("durations");
                            for (int i = 0; i < clientsDurations.length(); i++) {
                                locations.add(new ArrayList<>());
                                JSONArray row = (JSONArray)clientsDurations.get(i);
                                for (int y = 0; y < row.length();y++) {
                                    locations.get(i).add(row.getDouble(y));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "onErrorResponse: ", error);
                    handleError(context, error);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + API_KEY);
                return headers;
            }
        };
        requestQueue.add(request);
    }
}
