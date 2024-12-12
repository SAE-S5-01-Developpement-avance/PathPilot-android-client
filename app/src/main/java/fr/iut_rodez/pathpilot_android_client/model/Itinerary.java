package fr.iut_rodez.pathpilot_android_client.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class representing an itinerary.
 */
public class Itinerary {
    private int id;
    private ArrayList<Client> clients;

    public Itinerary(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public Itinerary () {
        this.clients = new ArrayList<>();
    }

    public Itinerary(JSONObject itineraryJson) throws JSONException {
        //this.id = itineraryJson.getInt("");
        //this.clients = itineraryJson.getJSONArray("");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }
}
