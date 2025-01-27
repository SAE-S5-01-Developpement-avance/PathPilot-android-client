package fr.iut_rodez.pathpilot_android_client.util;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.home.clients.Client;

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
    public static List<Client> getClientsPageable(JSONObject response) {
        ArrayList<Client> listClients = new ArrayList<>();

        // Parse the JSON response and create a list of clients
        try {
            JSONArray embeddedListClients = response
                    .getJSONObject("_embedded")
                    .getJSONArray("clientList");

            for (int i = 0; i < embeddedListClients.length(); i++) {
                JSONObject clientJson = embeddedListClients.getJSONObject(i);
                listClients.add(new Client(clientJson));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the JSON response", e);
        }

        return listClients;
    }
}
