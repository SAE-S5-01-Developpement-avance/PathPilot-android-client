package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.util.Parser;

public class Route implements Parcelable {

    public static final int RATIO_MILLI_SECOND = 1000;
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;

    /**
     * The id of the route
     */
    private String id;

    /**
     * The salesman home address
     */
    private GeoPoint salesmanHome;

    /**
     * The ordered list of clients to visit
     */
    private ArrayList<Client> expectedClients;

    /**
     * The date when the route starts
     */
    private LocalDateTime startDate;

    /**
     * The index that points to the current client in the list of expected clients
     */
    private int indexCurrentClient;

    /**
     * Tell if the route is paused
     */
    private boolean isPaused;

    /**
     * The list of clients already visited
     */
    private ArrayList<Client> visitedClients;

    /**
     * The current position of the salesman
     */
    private GeoPoint currentSalesmanPosition;

    public Route(JSONObject routeJson) throws JSONException {
        // Parse the JSON object and create a route
        id = routeJson.getString("id");
        salesmanHome = Parser.getGeoPointFromGeoJSONPoint(routeJson.getJSONObject("salesman_home"));
        startDate = Parser.getLocalDateTimeFromString(routeJson.getString("startDate"));
        expectedClients = Parser.getShortClients(routeJson.getJSONArray("expected_clients"));
        visitedClients = Parser.getShortClients(routeJson.getJSONArray("visited_clients"));
        currentSalesmanPosition = Parser.getGeoPointFromGeoJSONPoint(routeJson.getJSONObject("salesman_current_position"));
    }

    protected Route(Parcel in) {
        salesmanHome = in.readParcelable(GeoPoint.class.getClassLoader());
        expectedClients = in.createTypedArrayList(Client.CREATOR);
        long timeInMillis = in.readLong();
        startDate = LocalDateTime.ofEpochSecond(timeInMillis / RATIO_MILLI_SECOND, 0, ZONE_OFFSET);
        indexCurrentClient = in.readInt();
        isPaused = in.readByte() != 0;
        visitedClients = in.createTypedArrayList(Client.CREATOR);
        currentSalesmanPosition = in.readParcelable(GeoPoint.class.getClassLoader());
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        long timeInMillis = startDate.toEpochSecond(ZONE_OFFSET) * RATIO_MILLI_SECOND;
        dest.writeParcelable(salesmanHome, flags);
        dest.writeTypedList(expectedClients);
        dest.writeLong(timeInMillis);
        dest.writeInt(indexCurrentClient);
        dest.writeByte((byte) (isPaused ? 1 : 0));
        dest.writeTypedList(visitedClients);
        dest.writeParcelable(currentSalesmanPosition, flags);
    }

    public String getId() {
        return id;
    }

    public GeoPoint getSalesmanHome() {
        return salesmanHome;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public ArrayList<Client> getExpectedClients() {
        return expectedClients;
    }

    public ArrayList<Client> getVisitedClients() {
        return visitedClients;
    }

    public int getIndexCurrentClient() {
        return indexCurrentClient;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public GeoPoint getCurrentSalesmanPosition() {
        return currentSalesmanPosition;
    }

    public Client getCurrentClient() {
        return expectedClients.get(indexCurrentClient);
    }

    /**
     * Update the route to represent that the current client has been visited
     * <p>
     * The current client is added to the list of visited clients and the index of the current client is incremented.
     * </p>
     */
    public void clientHasBeenVisited() {
        visitedClients.add(expectedClients.get(indexCurrentClient));
        indexCurrentClient++;
    }

    public boolean hasClientToVisit() {
        return indexCurrentClient < expectedClients.size();
    }
}
