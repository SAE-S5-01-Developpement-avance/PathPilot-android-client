package fr.iut_rodez.pathpilot_android_client.home.itineraries;

import static fr.iut_rodez.pathpilot_android_client.util.NetworkUtils.getRequestQueue;
import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;

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
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientService;
import fr.iut_rodez.pathpilot_android_client.model.Client;
import fr.iut_rodez.pathpilot_android_client.model.Itinerary;

public class ItineraryService {

    public static final String API_BASE_URL = BuildConfig.API_BASE_URL + "api/"; // TODO Complete the URL
    private static final String TAG = ItineraryService.class.getSimpleName();
    /**
     * Request to the API to add a client.
     * If the request is successful, it goes back to the previous activity.
     * @param context Context of the application
     * @param itinerary The client to add
     */
    public static void addItinerary(Context context, Itinerary itinerary) {
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
}
