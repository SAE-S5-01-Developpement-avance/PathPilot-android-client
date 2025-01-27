package fr.iut_rodez.pathpilot_android_client.util;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.NonUiContext;

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

    @NonNull
    public static List<Client> getClientsPageable(JSONObject response) {
        ArrayList<Client> listClients = new ArrayList<>();

        // Parse the JSON response and create a list of clients
        try {
            JSONObject embedded = response.getJSONObject("_embedded");
            if (embedded.has("clientList")) {
                JSONArray embeddedListClients = embedded.getJSONArray("clientList");
                for (int i = 0; i < embeddedListClients.length(); i++) {
                    JSONObject clientJson = embeddedListClients.getJSONObject(i);

                    Client client = new Client(clientJson);
                    listClients.add(client);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error while parsing the JSON response", e);
        }

        return listClients;
    }
}
