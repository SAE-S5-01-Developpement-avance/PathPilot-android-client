package fr.iut_rodez.pathpilot_android_client.home.itinerary.entity;

import static fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client.getClientsDisplay;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.util.Parser;

/**
 * Class representing an itinerary.
 */
public class Itinerary implements Parcelable {
    private String displayName;
    private String id;
    private ArrayList<Client> clients;
    private final GeoPoint salesmanHome;

    public Itinerary(ArrayList<Client> clients, double salesmanLatitude, double salesmanLongitude) {
        this.clients = clients;
        this.salesmanHome = new GeoPoint(salesmanLatitude, salesmanLongitude);
    }

    public Itinerary() {
        this.clients = new ArrayList<>();
        this.salesmanHome = new GeoPoint(0.0, 0.0);
    }

    public Itinerary(JSONObject itineraryJson) throws JSONException {
        this.id = itineraryJson.getString("id");
        this.clients = new ArrayList<>();
        this.salesmanHome = Parser.getGeoPointFromGeoJSONPoint(itineraryJson.getJSONObject("salesman_home"));

        JSONArray clientsSchedule = itineraryJson.getJSONArray("clients_schedule");
        List<Client> clientsParsed = new ArrayList<>();
        for (int i = 0; i < clientsSchedule.length(); i++) {
            JSONObject clientJson = clientsSchedule.getJSONObject(i);
            clientsParsed.add(Client.createClientFromShortJson(clientJson));
        }
        this.clients.addAll(clientsParsed);
    }

    protected Itinerary(Parcel in) {
        id = in.readString();
        clients = in.createTypedArrayList(Client.CREATOR);
        salesmanHome = in.readParcelable(GeoPoint.class.getClassLoader());
        displayName = in.readString();
    }

    public static final Creator<Itinerary> CREATOR = new Creator<>() {
        @Override
        public Itinerary createFromParcel(Parcel in) {
            return new Itinerary(in);
        }

        @Override
        public Itinerary[] newArray(int size) {
            return new Itinerary[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSalesmanLongitude(double salesmanLongitude) {
        this.salesmanHome.setLongitude(salesmanLongitude);
    }

    public void setSalesmanLatitude(double salesmanLatitude) {
        this.salesmanHome.setLatitude(salesmanLatitude);
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public double getSalesmanLongitude() {
        return salesmanHome.getLongitude();
    }

    public double getSalesmanLatitude() {
        return salesmanHome.getLatitude();
    }

    public GeoPoint getSalesmanHome() {
        return salesmanHome;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public JSONObject toJson() {
        JSONObject itineraryJson = new JSONObject();
        try {
            itineraryJson.put("clients_schedule", clients);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return itineraryJson;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeTypedList(clients);
        dest.writeParcelable(salesmanHome, flags);
        dest.writeString(displayName);
    }

    /**
     * Adapter to display the itineraries in a ListView.
     */
    public static class ItineraryArrayAdapter extends ArrayAdapter<Itinerary> {

        private final Context context;
        private final List<Itinerary> itineraries;

        public ItineraryArrayAdapter(@NonNull Context context, List<Itinerary> itineraries) {
            super(context, -1, itineraries);
            this.context = context;
            this.itineraries = itineraries;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            // Inflate le layout personnalisé
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.itinerary_list_item, parent, false);

            // Récupérer les TextView du layout
            TextView itineraryNumber = rowView.findViewById(R.id.itinerary_number);
            TextView itineraryCoordinates = rowView.findViewById(R.id.itinerary_coordinates);
            TextView itineraryClientNames = rowView.findViewById(R.id.itinerary_client_names);
            TextView itineraryTotalStops = rowView.findViewById(R.id.itinerary_total_stops);

            // Récupérer la route à cette position
            Itinerary itinerary = itineraries.get(position);

            // Définir les valeurs des TextView
            itineraryNumber.setText(MessageFormat.format("{0}° - {1}", position + 1, itinerary.getId()));

            String itineraryCoordinatesString = context.getString(R.string.itinerary_coordinates) + itinerary.getCoordinates();
            itineraryCoordinates.setText(itineraryCoordinatesString);

            itineraryClientNames.setText(getClientsDisplay(itinerary.getClients()));
            itineraryTotalStops.setText(MessageFormat.format("{0}{1}", context.getString(R.string.itinerary_total_stops), itinerary.getClients().size()));

            return rowView;
        }
    }

    /**
     * Get the coordinates of the salesman.
     *
     * @return The coordinates of the salesman
     */
    public String getCoordinates() {
        String nOrS = getSalesmanLatitude() > 0 ? "N" : "S";
        String eOrW = getSalesmanLongitude() > 0 ? "E" : "W";

        return String.format("%.2f°%s, %.2f°%s", getSalesmanLatitude(), nOrS, getSalesmanLongitude(), eOrW);
    }
}
