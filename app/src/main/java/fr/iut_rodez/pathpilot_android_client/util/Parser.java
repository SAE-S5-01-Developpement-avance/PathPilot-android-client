package fr.iut_rodez.pathpilot_android_client.util;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;

import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.ClientPage;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.ItineraryPage;

/**
 * Utility class that abstracts the parsing of Volley responses
 */
public class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    /**
     * Parse the JSON response of the GET clients request and return a list of clients
     * <p>
     *     Request URL example:
     *     <a href="http://localhost:8080/api/clients">/api/clients</a>
     *
     * @param response JSON response of the GET clients request
     * @return List of clients parsed from the JSON response. If an error occurs, an empty list is returned
     */
    @NonNull
    public static ClientPage getClientsPageable(JSONObject response) {
        ArrayList<Client> listClients = new ArrayList<>();
        ArrayList<Link> listLinks = new ArrayList<>();

        // Parse the JSON response and create a list of clients
        try {
            JSONArray embeddedListClients = response
                    .getJSONObject("_embedded")
                    .getJSONArray("clientResponseModelList");

            for (int i = 0; i < embeddedListClients.length(); i++) {
                JSONObject clientJson = embeddedListClients.getJSONObject(i);
                listClients.add(new Client(clientJson));
            }
            getPaginationLinks(response, listLinks);

        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the JSON response", e);
        }

        return new ClientPage(listClients, listLinks);
    }

    /**
     * Parse the JSON response of the GET itineraries request and return a list of itineraries
     * <p>
     *     Request URL example:
     *     <a href="http://localhost:8080/api/routes">/api/routes</a>
     * @param response JSON response of the GET itineraries request
     * @return List of itineraries parsed from the JSON response. If an error occurs, an empty list is returned
     */
    public static ItineraryPage getItinerariesPageable(JSONObject response) {
        ArrayList<Itinerary> listItineraries = new ArrayList<>();
        ArrayList<Link> listLinks = new ArrayList<>();

        try {
            JSONArray embeddedListItineraries = response
                    .getJSONObject("_embedded")
                    .getJSONArray("itineraryResponseModelList");

            for (int i = 0; i < embeddedListItineraries.length(); i++) {
                JSONObject itineraryJson = embeddedListItineraries.getJSONObject(i);
                listItineraries.add(new Itinerary(itineraryJson));
            }
            getPaginationLinks(response, listLinks);

        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the JSON response", e);
        }

        return new ItineraryPage(listItineraries, listLinks);
    }

    /**
     * Parse the GeoPoint from a JSON object
     * <p>
     *     JSON object example:
     *     <pre>
     *         {
     *         "x": 44.837789,
     *         "y": -0.57918
     *         }
     *     </pre>
     * </p>
     * @param jsonObject JSON object containing the x and y coordinates
     * @return The GeoPoint parsed from the JSON object. If an error occurs, null is returned
     */
    public static GeoPoint getGeoPoint(JSONObject jsonObject) {
        try {
            return new GeoPoint(jsonObject.getDouble("x"), jsonObject.getDouble("y"));
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the JSON response", e);
            return null;
        }
    }

    /**
     * Parse the JSON response of a page of objects requests and return a list of links
     * <p>
     *     Request URL example:
     *     <a href="http://localhost:8080/api/clients?page=1&size=10">/api/clients?page=1&size=10</a>
     *
     * @param response JSON response of the GET object request
     * @param listLinks List of links to be filled with the pagination links
     * @throws JSONException If an error occurs while parsing the JSON response
     */
    private static void getPaginationLinks(JSONObject response, ArrayList<Link> listLinks) throws JSONException {
        // Retrieve pagination links
        JSONObject links = response.getJSONObject("_links");
        if (links.has("next")) {
            listLinks.add(new Link( "next", links.getJSONObject("next").getString("href")));
        }
        if (links.has("prev")) {
            listLinks.add(new Link( "prev", links.getJSONObject("prev").getString("href")));
        }
        Log.d(TAG, "Page links: " + Arrays.toString(listLinks.toArray()));
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Parser() {}
}
