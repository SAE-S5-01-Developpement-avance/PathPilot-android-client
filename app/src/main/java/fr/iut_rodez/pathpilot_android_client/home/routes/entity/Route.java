package fr.iut_rodez.pathpilot_android_client.home.routes.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.ClientState;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.map.LocationNameProvider;

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
    private ArrayList<RouteClient> clients;

    /**
     * The date when the route starts
     */
    private LocalDateTime startDate;

    /**
     * The index that points to the current client in the list of expected clients
     */
    private int nextClientIndex;

    /**
     * The display name of the route date
     */
    private String dateDisplayName;

    /**
     * State of the route
     * <p>
     * The state of the route is determined by the start date, the pause state and the completion state.
     * </p>
     */
    private RouteState state;

    /**
     * The positions of the salesman.
     * <p>
     *     The last position is at the end of the list.
     */
    private List<GeoPoint> salesmanPositions;

    public Route(JSONObject routeJson) throws JSONException {
        // Parse the JSON object and create a route
        id = routeJson.getString("id");
        salesmanHome = Parser.getGeoPointFromGeoJSONPoint(routeJson.getJSONObject("salesman_home"));
        startDate = Parser.getLocalDateTimeFromString(routeJson.getString("startDate"));
        clients = (ArrayList<RouteClient>) Parser.getClientRoutes(routeJson.getJSONArray("clients"));

        // When a route have just been create, then there is no salesman position to track
        JSONObject salesmanCurrentPosition = routeJson.optJSONObject("salesmanPositions");
        if (salesmanCurrentPosition != null) {
            salesmanPositions = Parser.getGeoLineFromGeoJSONLine(salesmanCurrentPosition);
        }

        state = RouteState.fromString(routeJson.getString("state"));

        // find the index of the current client
        nextClientIndex = 0;
        boolean found = false;
        for (int i = 0; i < clients.size() && !found; i++) {
            if (clients.get(i).getState() != ClientState.VISITED) {
                nextClientIndex = i;
                found = true;
            }
        }
    }

    protected Route(Parcel in) {
        id = in.readString();
        salesmanHome = in.readParcelable(GeoPoint.class.getClassLoader());
        clients = in.createTypedArrayList(RouteClient.CREATOR);
        long timeInMillis = in.readLong();
        startDate = timeInMillis == Long.MIN_VALUE ? null : LocalDateTime.ofEpochSecond(timeInMillis / RATIO_MILLI_SECOND, 0, ZONE_OFFSET);
        nextClientIndex = in.readInt();
        salesmanPositions = in.createTypedArrayList(GeoPoint.CREATOR);
        String state = in.readString();
        if (state != null) {
            this.state = RouteState.fromString(state);
        }
    }

    public static final Creator<Route> CREATOR = new Creator<>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    protected Route() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        long timeInMillis = startDate == null ? Long.MIN_VALUE : startDate.toEpochSecond(ZONE_OFFSET) * RATIO_MILLI_SECOND;
        dest.writeParcelable(salesmanHome, flags);
        dest.writeTypedList(clients);
        dest.writeLong(timeInMillis);
        dest.writeInt(nextClientIndex);
        dest.writeTypedList(salesmanPositions);
        dest.writeString(state.getValue());
    }

   /**
     * Get the display name of the route date
     *
     * @return The display name of the route date
     */
    public void setDateDisplayName(Locale currentLocale) {
        DateTimeFormatter localeFormatter = DateTimeFormatter
                .ofPattern("dd/MM/yyyy hh:mm")
                .withLocale(currentLocale);
        this.dateDisplayName = startDate == null ? null : startDate.format(localeFormatter);
    }

    /**
     * Get the display name of the route date
     *
     * @return The display name of the route date
     */
    public String getDateDisplayName() {
        return dateDisplayName == null ? "None" : dateDisplayName;
    }

    /**
     * @return the positions of the salesman
     */
    public List<GeoPoint> getSalesmanPositions() {
        return salesmanPositions;
    }

    public void setNextClientIndex(int nextClientIndex) {
        this.nextClientIndex = nextClientIndex;
    }

    /**
     * Skip the client.
     * @param clientToSkip the client that we want skipped.
     */
    public void skippedClient(RouteClient clientToSkip) {
        RouteClient nextClientBeforeSkip = getNextClient();
        // Update the clients list
        clients.stream()
                .filter(client -> client.equals(clientToSkip))
                .findFirst()
                .ifPresent(client -> {
                    client.setState(ClientState.SKIPPED);
                });

        // If the current client is the client to skip, we go to the next client. Unless the next client is null.
        if (nextClientBeforeSkip.equals(clientToSkip)) {
            RouteClient firstNextExpectedClient = clients.stream()
                    .filter(routeClient -> routeClient.getState().equals(ClientState.EXPECTED) && !routeClient.equals(clientToSkip))
                    .findFirst()
                    .orElse(null);

            if (firstNextExpectedClient != null) {
                nextClientIndex = clients.indexOf(firstNextExpectedClient);
            } else {
                nextClientIndex = clients.size() - 1;
            }
        }
    }

    /**
     * Adapter to display the routes in a ListView.
     */
    public static class RouteArrayAdapter extends ArrayAdapter<Route> {

        private final Context context;
        private final List<Route> routes;

        public RouteArrayAdapter(@NonNull Context context, List<Route> routes) {
            super(context, -1, routes);
            this.context = context;
            this.routes = routes;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.route_list_item, parent, false);

            TextView routeNumber = rowView.findViewById(R.id.route_number);
            TextView routeAddress = rowView.findViewById(R.id.route_address);
            TextView routeClientNames = rowView.findViewById(R.id.route_client_names);
            TextView routeBeginDate = rowView.findViewById(R.id.route_begin_date);
            TextView routeState = rowView.findViewById(R.id.route_state);

            Route route = routes.get(position);

            String routeCoordinatesString = context.getString(R.string.route_address) + LocationNameProvider.getAddressName(context, route.getSalesmanHome());
            routeAddress.setText(routeCoordinatesString);

            ArrayList<Client> clients = new ArrayList<>();
            route.getClients().forEach(routeClient -> clients.add(routeClient.getClient()));
            routeClientNames.setText(Client.getClientsDisplay(clients));

            String firstLetterOfClients = route.getClients().stream().map(client -> client.getClient().getCompanyName().charAt(0)).map(Object::toString).collect(Collectors.joining());
            routeNumber.setText(MessageFormat.format("{0} - {1}", context.getString(R.string.route), firstLetterOfClients));

            routeBeginDate.setText(MessageFormat.format("{0}{1}", context.getString(R.string.route_begin_date), route.getDateDisplayName()));

            String state = switch (route.getState()) {
                case NOT_STARTED -> context.getString(R.string.route_state_not_started);
                case PAUSED -> context.getString(R.string.route_state_paused);
                case FINISHED -> context.getString(R.string.route_state_completed);
                case STOPPED -> context.getString(R.string.route_state_stopped);
                case IN_PROGRESS -> "In progress";
                default -> context.getString(R.string.route_state_not_started);
            };
            routeState.setText(state);

            return rowView;
        }
    }

    /**
     * Get the state of the route
     * <p>
     * The state of the route is determined by the start date, the pause state and the completion state.
     * </p>
     *
     * @return The state of the route
     */
    public RouteState getState() {
        return state;
    }

    public void setState(RouteState state) {
        this.state = state;
    }

    /**
     * Start the route
     * <p>
     * The start date is set to the current date and time.
     * </p>
     */
    private boolean isStarted() {
        return startDate != null;
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

    public int getNextClientIndex() {
        return nextClientIndex;
    }

    public GeoPoint getCurrentSalesmanPosition() {
        GeoPoint geoPoint = null;
        if (salesmanPositions != null) {
            geoPoint = salesmanPositions.get(salesmanPositions.size() - 1);
        }
        return geoPoint;
    }

    /**
     * Update the route to represent that the current client has been visited
     * <p>
     * The current client is added to the list of visited clients and the index of the current client is incremented.
     * </p>
     */
    public void clientHasBeenVisited() {
        clients.get(nextClientIndex).setState(ClientState.VISITED);
        if (getNextClient() != null) {
            nextClientIndex = clients.indexOf(getNextClient());
        } else {
            nextClientIndex = clients.size() - 1;
        }
    }

    /**
     * Get the next client to visit
     *
     * @return The next client to visit
     */
    public RouteClient getNextClient() {
        RouteClient routeClient = null;
        if (nextClientIndex < clients.size() && !state.equals(RouteState.STOPPED)) {
            for (int i = nextClientIndex; i < clients.size() && routeClient == null; i++) {
                if (clients.get(i).getState() == ClientState.EXPECTED) {
                    routeClient = clients.get(i);
                }
            }
        }
        return routeClient;
    }

    /**
     * Get the number of clients expected
     *
     * @return The number of clients expected
     */
    public int countClientsExpected() {
        return (int) clients.stream().filter(client -> client.getState() != ClientState.SKIPPED).count();
    }

    /**
     * Get the number of clients visited
     *
     * @return The number of clients visited
     */
    public int countClientsVisited() {
        return (int) clients.stream().filter(client -> client.getState() == ClientState.VISITED).count();
    }

    /**
     * Check if the route is completed
     * <p>
     * The route is completed if the index of the current client is equal to the number of expected clients.
     * </p>
     *
     * @return {@code true} if the route is completed, {@code false} otherwise
     */
    public boolean isCompleted() {
        return nextClientIndex == clients.size();
    }

    public ArrayList<RouteClient> getClients() {
        return clients;
    }

    public void setClients(ArrayList<RouteClient> clients) {
        this.clients = clients;
    }

    @NonNull
    @Override
    public String toString() {
        return "Route{" +
                "id='" + id + '\'' +
                ", salesmanHome=" + salesmanHome +
                ", clients=" + clients +
                ", startDate=" + startDate +
                ", indexCurrentClient=" + nextClientIndex +
                ", state=" + state +
                ", salesmanPositions=" + salesmanPositions +
                ", dateDisplayName='" + dateDisplayName + '\'' +
                '}';
    }
}
