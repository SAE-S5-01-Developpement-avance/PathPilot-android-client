package fr.iut_rodez.pathpilot_android_client.home.routes.service;

import android.content.Context;
import android.widget.ListView;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.InfoItinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;

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
     */
    void getNextPageRoutes(Context context, ListView listRoutesView, String nextPageUrl, Route.RouteArrayAdapter adapter);

    /**
     * Create a route from an itinerary.
     * <p>
     * Send a request to the server to create a route from an itinerary.<br>
     * If the request is successful, it redirects the user to the player activity with the new route.
     * If the request fails, it shows an error message.
     * </p>
     *
     * @param activity The activity that calls the service
     * @param itinerary The itinerary to create the route from
     */
    void createRoute(InfoItinerary activity, Itinerary itinerary);
}
