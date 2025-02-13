package fr.iut_rodez.pathpilot_android_client.home.routes.service;

import android.content.Context;
import android.widget.ListView;

import com.android.volley.Response;

import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;

public interface IRouteService {
    String ROUTES_API_ENDPOINT = BuildConfig.API_BASE_URL + "routes";
    String TAG = IRouteService.class.getSimpleName();

    /**
     * Request to the API the routes.
     * If the request is successful, it add routes to the adapter and link them to the view
     * If not it displays the error encounter.
     *
     * @param context             Context of the application
     * @param listRoutesView The view where the routes will be displayed
     */
    void getRoutes(Context context, ListView listRoutesView);

    /**
     * Request to the API to fetch the next page of clients.
     * If the request is successful, it adds clients to the adapter and link it to the view
     * If not it displays the error encounter.
     *
     * @param context         Context of the application
     * @param listRoutesView The view where the clients will be displayed
     * @param nextPageUrl     The URL of the next page
     * @param adapter         The adapter to add the clients to
     * @param callback        The callback to put the flag to false when finish loading the next page
     */
    void getNextPageRoutes(Context context, ListView listRoutesView, String nextPageUrl, Route.RouteArrayAdapter adapter, Runnable callback);

    /**
     * Create a route from an itinerary.
     * <p>
     * Send a request to the server to create a route from an itinerary.<br>
     * If the request is successful, it redirects the user to the player activity with the new route.
     * If the request fails, it shows an error message.
     * </p>
     *
     * @param context The context of the application
     * @param jwtToken The JWT token of the user
     * @param itinerary The itinerary to create the route from
     * @param onResponse The response listener
     * @param onErrorResponse The error listener
     */
    void createRoute(Context context, JWTToken jwtToken, Itinerary itinerary, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse);


    /**
     * Update salesman position
     */
    void updateSalesmanPosition(Context context, JWTToken jwtToken, GeoPoint currentPosition, Route route, Response.Listener<JSONObject> onResponse, Response.ErrorListener onErrorResponse);

    void stopRoute(Context context, Route route);

}
