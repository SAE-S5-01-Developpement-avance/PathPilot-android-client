package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;
import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import java.util.HashMap;
import java.util.Map;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.clients.AddClient;
import fr.iut_rodez.pathpilot_android_client.model.Itinerary;

public class ItineraryService {

    public static final String API_BASE_URL = BuildConfig.API_BASE_URL + "api/"; // TODO Complete the URL
    private static final String TAG = ItineraryService.class.getSimpleName();
    /**
     * Request to the API to add an itinerary.
     * If the request is successful, it goes back to the previous activity.
     * @param context Context of the application
     * @param itinerary The itinerary to add
     */
    public static void addItinerary(Context context, Itinerary itinerary) {
        Log.d(TAG, "API URL: " + API_BASE_URL);

        AddItinerary addItineraryActivity = (AddItinerary) context;
        RequestQueue requestQueue = getRequestQueue(context);
        String jwtToken = addItineraryActivity.getJWTToken().getToken();

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, API_BASE_URL, itinerary.toJson(),
                response -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "onResponse: " + response);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(AddClient.CLE_CLIENT_ADDED, true);
                    addItineraryActivity.setResult(AddClient.RESULT_OK, returnIntent);

                    addItineraryActivity.finish();
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

                        ItineraryArrayAdapter adapter = new ItineraryArrayAdapter(homeActivity, itineraryArray);
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
}
