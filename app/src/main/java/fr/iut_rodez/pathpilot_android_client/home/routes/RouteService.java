package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import fr.iut_rodez.pathpilot_android_client.home.itinerary.InfoItinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.Itinerary;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;

public class RouteService implements IRouteService {

    /**
     * Create a route from an itinerary.
     * <p>
     * Send a request to the server to create a route from an itinerary.<br>
     * If the request is successful, it redirects the user to the player activity with the new route.
     * If the request fails, it shows an error message.
     * </p>
     *
     * @param activity  The activity that calls the service
     * @param itinerary The itinerary to create the route from
     */
    @Override
    public void createRoute(InfoItinerary activity, Itinerary itinerary) {
        JWTToken jwtToken = activity.getJwtToken();
        RequestQueue requestQueue = NetworkUtils.getRequestQueue(activity);
        Popup popup = activity.getPopup();
        RouteRequestModel routeRequestModel = new RouteRequestModel(itinerary.getId());

        JsonObjectRequest request = NetworkUtils.createAuthenticatedRequest(Request.Method.POST, ROUTES_API_ENDPOINT, routeRequestModel.toJson(), jwtToken.getToken(),
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "createRoute: " + response);

                    Route route = Parser.getRoute(response);
                    activity.redirectToPlayerActivity(route);
                },
                error -> {
                    popup.dismissProgressDialog();
                    // TODO better message
                    popup.showErrorDialog("Error while creating the route");
                });

        popup.showProgressDialog();
        requestQueue.add(request);
    }

    record RouteRequestModel(@NonNull String itineraryId) {
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("itineraryId", itineraryId);
            } catch (Exception ignored) {
                // This should never happen, has the valu isn't a Number
            }
            return json;
        }
    }
}
