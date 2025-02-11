package fr.iut_rodez.pathpilot_android_client.util;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientPage;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.Itinerary;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.entity.ItineraryPage;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RouteClient;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RoutePage;

/**
 * Utility class that abstracts the parsing of Volley responses
 */
public class Parser {

    private static final String TAG = Parser.class.getSimpleName();

    /**
     * Parse the JSON response of the GET clients request and return a list of clients
     * <p>
     * Request URL example:
     * <a href="http://localhost:8080/api/clients">/api/clients</a>
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
     * Parse an JSON array of clients and return a list of clients
     * <p>
     * This method while search the value to create a "short" client. That mean that only the id, the company name and the company location will be parsed.
     * This kind of format is found inside the itinerary and the route object.
     * </p><p>
     * Here an example of the JSON object:
     * <pre>
     * {@code
     * {
     *  "client": {
     *      "id": 1,
     *      "companyLocation": {
     *         "x": 2.3522,
     *         "y": 48.8566,
     *         "coordinates": [
     *             2.3522,
     *             48.8566
     *         ],
     *         "type": "Point"
     *      },
     *      "companyName": "hp"
     *   },
     *   "state": "EXPECTED"
     * }
     * }
     * </pre>
     * </p>
     *
     * @param jsonArray the JSON array to parse
     * @return the list of clients parsed from the JSON array
     * @see Client#createClientFromShortJson(JSONObject)
     */
    public static ArrayList<Client> getShortClients(JSONArray jsonArray) {
        ArrayList<Client> listClients = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject clientJson = jsonArray.getJSONObject(i);
                listClients.add(Client.createClientFromShortJson(clientJson));
            } catch (JSONException e) {
                Log.e(TAG, "Error while parsing the JSON response", e);
            }
        }
        return listClients;
    }

    /**
     * Parse a JSON array of route clients and return a list of RouteClient objects.
     * <p>
     * This method will parse the JSON array to create RouteClient objects, which include
     * a Client object and a state string.
     * </p>
     *
     * @param jsonArray the JSON array to parse
     * @return the list of RouteClient objects parsed from the JSON array
     */
    public static ArrayList<RouteClient> getClientRoutes(JSONArray jsonArray) {
        ArrayList<RouteClient> listRouteClients = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject clientJson = jsonArray.getJSONObject(i);
                Client client = Client.createClientFromShortJson(clientJson.getJSONObject("client"));
                String state = clientJson.getString("state");
                listRouteClients.add(new RouteClient(client, ClientState.valueOf(state)));
            } catch (JSONException e) {
                Log.e(TAG, "Error while parsing the JSON response", e);
            }
        }
        return listRouteClients;
    }

    /**
     * Parse the JSON response of the GET itineraries request and return a list of itineraries
     * <p>
     * Request URL example:
     * <a href="http://localhost:8080/api/itineraries">/api/itineraries</a>
     *
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
     * Parse the JSON response of the GET routes request and return a list of routes
     * <p>
     * Request URL example:
     * <a href="http://localhost:8080/api/routes">/api/routes</a>
     *
     * @param response JSON response of the GET routes request
     * @return List of routes parsed from the JSON response. If an error occurs, an empty list is returned
     */
    public static RoutePage getRoutesPageable(JSONObject response) {
        ArrayList<Route> listRoutes = new ArrayList<>();
        ArrayList<Link> listLinks = new ArrayList<>();

        try {
            JSONArray embeddedListRoutes = response
                    .getJSONObject("_embedded")
                    .getJSONArray("routeResponseModelList");

            for (int i = 0; i < embeddedListRoutes.length(); i++) {
                JSONObject routeJson = embeddedListRoutes.getJSONObject(i);
                listRoutes.add(new Route(routeJson));
            }
            getPaginationLinks(response, listLinks);

        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the JSON response", e);
        }

        return new RoutePage(listRoutes, listLinks);
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
     * @param geoJsonPoint JSON object containing the x and y coordinates
     * @return The GeoPoint parsed from the JSON object. If an error occurs, null is returned
     */
    @NonNull
    public static GeoPoint getGeoPointFromGeoJSONPoint(JSONObject geoJsonPoint) throws JSONException {
        return new GeoPoint(
                geoJsonPoint.getDouble("y"),
                geoJsonPoint.getDouble("x")
        );
    }

    public static Route getRoute(JSONObject response) {
        Route route = null;
        try {
            route = new Route(response);
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the JSON response", e);
        }
        return route;
    }

    /**
     * Parse a string to a LocalDateTime
     * <p>
     * For example <pre>2025-01-31T08:17:18.392+00:00</pre> while be parsed as the 31th of January 2025 at 8:17:18.392 in the UTC timezone (GMT+0)
     * <br>
     * If the string is not in the correct format, the method will return null
     * </p>
     *
     * @param date the string to parse
     * @return the LocalDateTime parsed from the string
     */
    public static LocalDateTime getLocalDateTimeFromString(String date) {
        LocalDateTime parsedDateTime = null;
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);
            parsedDateTime = offsetDateTime.toLocalDateTime();
        } catch (Exception ignored) {
            // If the date is not in the correct format, the method will return null
        }
        return parsedDateTime;
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
    private Parser() {
    }
}
