package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;

/**
 * Class representing an itinerary.
 */
public class Itinerary {
    private String id;
    private ArrayList<Client> clients;
    private double salesmanLatitude;
    private double salesmanLongitude;

    public Itinerary(ArrayList<Client> clients, double salesmanLatitude, double salesmanLongitude) {
        this.clients = clients;
        this.salesmanLatitude = salesmanLatitude;
        this.salesmanLongitude = salesmanLongitude;
    }

    public Itinerary() {
        this.clients = new ArrayList<>();
        this.salesmanLatitude = 0;
        this.salesmanLongitude = 0;
    }

    public Itinerary(JSONObject itineraryJson) throws JSONException {
        this.id = itineraryJson.getString("id");
        this.clients = new ArrayList<>();

        JSONObject coordinates = itineraryJson.getJSONObject("salesman_home");
        this.salesmanLongitude = coordinates.getDouble("x");
        this.salesmanLatitude = coordinates.getDouble("y");

        JSONArray clientsSchedule = itineraryJson.getJSONArray("clients_schedule");
        for (int i = 0; i < clientsSchedule.length(); i++) {
            JSONObject clientJson = clientsSchedule.getJSONObject(i);
            JSONObject companyLocation = clientJson.getJSONObject("companyLocation");
            Client client = new Client(
                    clientJson.getInt("id"),
                    clientJson.getString("companyName"),
                    clientCoordinates.getDouble("x"),
                    clientCoordinates.getDouble("y")
            );
            this.clients.add(client);
        }
    }

    protected Itinerary(Parcel in) {
        id = in.readInt();
        clients = in.createTypedArrayList(Client.CREATOR);
        salesmanLatitude = in.readDouble();
        salesmanLongitude = in.readDouble();
    }

    public static final Creator<Itinerary> CREATOR = new Creator<Itinerary>() {
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
        this.salesmanLongitude = salesmanLongitude;
    }

    public void setSalesmanLatitude(double salesmanLatitude) {
        this.salesmanLatitude = salesmanLatitude;
    }

    public ArrayList<Client> getClients() {
        return clients;
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

    public double getSalesmanLongitude() {
        return salesmanLongitude;
    }

    public double getSalesmanLatitude() {
        return salesmanLatitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeTypedList(clients);
        dest.writeDouble(salesmanLatitude);
        dest.writeDouble(salesmanLongitude);
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
            String itineraryNumberString = context.getString(R.string.itinerary_number) + itinerary.getId();
            itineraryNumber.setText(itineraryNumberString);

            String itineraryCoordinatesString = context.getString(R.string.itinerary_coordinates) + itinerary.getCoordinates();
            itineraryCoordinates.setText(itineraryCoordinatesString);

            itineraryClientNames.setText(itinerary.convertClients(itinerary.getClients()));
            itineraryTotalStops.setText(MessageFormat.format("{0}{1}", context.getString(R.string.itinerary_total_stops), itinerary.getClients().size()));

            return rowView;
        }
    }

    /**
     * Convert the list of clients to a string list used in itinerary.
     *
     * @param clients The list of clients
     * @return The string representation of the list of clients
     */
    public String convertClients(ArrayList<Client> clients) {
        StringBuilder clientNames = new StringBuilder();
        for (int i = 0; i < clients.size(); i++) {
            clientNames.append(i + 1).append(". ").append(clients.get(i).toShortString());
            if (i < clients.size() - 1) {
                clientNames.append("\n");
            }
        }
        return clientNames.toString();
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
