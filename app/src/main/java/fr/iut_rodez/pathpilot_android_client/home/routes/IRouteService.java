package fr.iut_rodez.pathpilot_android_client.home.routes;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.InfoItinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.Itinerary;

public interface IRouteService {
    String ROUTES_API_ENDPOINT = BuildConfig.API_BASE_URL + "routes";
    static final String TAG = IRouteService.class.getSimpleName();

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
