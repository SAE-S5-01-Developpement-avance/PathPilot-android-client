package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Context;
import android.widget.ListView;

import org.json.JSONException;

import java.util.List;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.Home;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary.ItineraryArrayAdapter;

/**
 * Interface for ItineraryService class.
 */
public interface IItineraryService {

    String API_BASE_URL = BuildConfig.API_BASE_URL + "itineraries";
    String TAG = ItineraryService.class.getSimpleName();

    /**
     * Request to the API to add an itinerary.
     * If the request is successful, it goes back to the previous activity.
     *
     * @param context     Context of the application
     * @param listClients The list of clients to create an itinerary
     */
    void addItinerary(Context context, List<Client> listClients) throws JSONException;

    /**
     * Request to the API the itineraries.
     * If the request is successful, it add itineraries to the adapter and link them to the view
     * If not it displays the error encounter.
     *
     * @param context             Context of the application
     * @param listItinerariesView The view where the itineraries will be displayed
     */
    void getItineraries(Context context, ListView listItinerariesView);

    /**
     * Request to the API to fetch the next page of clients.
     * If the request is successful, it adds clients to the adapter and link it to the view
     * If not it displays the error encounter.
     *
     * @param context             Context of the application
     * @param listItinerariesView The view where the clients will be displayed
     * @param nextPageUrl         The URL of the next page
     * @param adapter             The adapter to add the clients to
     * @param callback            The callback to put the flag to false when finish loading the next page
     */
    void getNextPageItineraries(Context context, ListView listItinerariesView, String nextPageUrl, ItineraryArrayAdapter adapter, Runnable callback);

    /**
     * Request to the API to delete an itinerary.
     * If the request is successful, it removed the itinerary from the adapter
     *
     * @param homeActivity        Context of the application
     * @param itinerarySelected   The itinerary to delete
     * @param listItinerariesView The view where the itineraries will be displayed
     */
    void deleteItinerary(Home homeActivity, Itinerary itinerarySelected, ListView listItinerariesView);

    void deleteItineraryToCancelTheCreation(Context context, String idItinerary);
}