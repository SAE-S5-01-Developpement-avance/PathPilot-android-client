package fr.iut_rodez.pathpilot_android_client.home.routes;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import fr.iut_rodez.pathpilot_android_client.home.clients.Client;

public class Route implements Parcelable {

    public static final int RATIO_MILLI_SECOND = 1000;
    public static final ZoneOffset ZONE_OFFSET = ZoneOffset.UTC;
    /**
     * The salesman home address
     */
    private GeoPoint salesmanHome;

    /**
     * The ordered list of clients to visit
     */
    private Client[] expectedClients;

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
    private Client[] visitedClients;

    /**
     * The current position of the salesman
     */
    private GeoPoint currentSalesmanPosition;

    protected Route(Parcel in) {
        salesmanHome = in.readParcelable(GeoPoint.class.getClassLoader());
        expectedClients = in.createTypedArray(Client.CREATOR);
        long timeInMillis = in.readLong();
        startDate = LocalDateTime.ofEpochSecond(timeInMillis / RATIO_MILLI_SECOND, 0, ZONE_OFFSET);
        indexCurrentClient = in.readInt();
        isPaused = in.readByte() != 0;
        visitedClients = in.createTypedArray(Client.CREATOR);
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
        dest.writeTypedArray(expectedClients, flags);
        dest.writeLong(timeInMillis);
        dest.writeInt(indexCurrentClient);
        dest.writeByte((byte) (isPaused ? 1 : 0));
        dest.writeTypedArray(visitedClients, flags);
        dest.writeParcelable(currentSalesmanPosition, flags);
    }

    public Client getCurrentClient() {
        Client currentClient = null;
        if (indexCurrentClient < expectedClients.length) {
            currentClient = expectedClients[indexCurrentClient];
        }
        return currentClient;
    }

    /**
     * Update the route to represent that the current client has been visited
     * <p>
     *     The current client is added to the list of visited clients and the index of the current client is incremented.
     * </p>
     */
    public void clientHasBeenVisited() {
        visitedClients[indexCurrentClient] = expectedClients[indexCurrentClient];
        indexCurrentClient++;
    }

    public boolean hasClientToVisit() {
        return indexCurrentClient < expectedClients.length;
    }
}
