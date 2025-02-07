package fr.iut_rodez.pathpilot_android_client.player;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.InfoItinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.Route;
import fr.iut_rodez.pathpilot_android_client.map.ActivityWithCurrentPosition;
import fr.iut_rodez.pathpilot_android_client.util.map.CurrentPosition;
import fr.iut_rodez.pathpilot_android_client.util.map.LocationNameProvider;
import fr.iut_rodez.pathpilot_android_client.util.map.MapMarker;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class PlayerItinerary extends ActivityWithCurrentPosition {

    private static final String TAG = PlayerItinerary.class.getSimpleName();

    private static final int ICON_PLAY = R.drawable.icon_start;
    private static final int ICON_PAUSE = R.drawable.icon_pause;
    private final Popup popup = new Popup(this);
    private TextView clientName;
    private TextView clientAddress;
    private TextView clientDistance;
    private TextView counterVisitedClients;
    private ImageButton pauseBtn;
    private Route route;
    private MapMarker mapMarker;
    private IMapController mapController;
    private GeoPoint startTrace;
    private Polyline salesmanTrace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //The parent class ActivityWithCurrentPosition,
        // need the view to be set because it uses findViewById
        setContentView(R.layout.view_player_itinerary);
        super.onCreate(savedInstanceState);

        mapMarker = new MapMarker(this);

        ImageButton detailClientBtn = findViewById(R.id.detail_client_btn);
        clientName = findViewById(R.id.client_name);
        clientAddress = findViewById(R.id.client_address);
        clientDistance = findViewById(R.id.client_distance);
        counterVisitedClients = findViewById(R.id.counter_visited_clients);
        ImageButton stopBtn = findViewById(R.id.stop_btn);
        pauseBtn = findViewById(R.id.pause_btn);
        ImageButton clientVisitedBtn = findViewById(R.id.client_visited_btn);
        ImageButton listClientsBtn = findViewById(R.id.clients_setting_btn);

        // Set onClickListener
        detailClientBtn.setOnClickListener(v -> Log.d(TAG, "onCreate: detailClientBtn"));
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
        stopBtn.setOnClickListener(v -> stop());
        pauseBtn.setOnClickListener(v -> pauseResume());
        clientVisitedBtn.setOnClickListener(v -> clientVisited());
        listClientsBtn.setOnClickListener(v -> listClients());

        // Show a loading popup.
        popup.showProgressDialog();

        setRouteInformation();
        initialiseMap();
        popup.dismissProgressDialog();
    }


    /**
     * Initialise the map
     * <p>
     * Set the tile source, the zoom controller, the multi touch controls and the zoom level
     * Set the center of the map to the salesman home
     * Draw the road between the salesman home and the clients
     * <br>
     * Also request the location permission and center the map
     * </p>
     *
     * @see PlayerItinerary#requestPermissionAndCenter()
     */
    private void initialiseMap() {
        Log.d(TAG, "initialiseMap: Initialising the map");
        mapController = mapView.getController();

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);
        mapController.setZoom(15.0);
        mapController.setCenter(route.getSalesmanHome());

        requestPermissionAndCenter();

        setExpectedClientMarker(route.getExpectedClients(), route.getNextClient());
        mapMarker.addMarker(getString(R.string.home), LocationNameProvider.getAddressName(this, route.getSalesmanHome()), route.getSalesmanHome(), MapMarker.MarkerType.SALESMAN_HOME);
        mapView.invalidate(); // Refresh the map
    }

    /**
     * Request the location permission and center the map
     * <p>
     * If the location permission is granted, the map is centered on the current position
     * If the location permission is denied, a popup is shown to ask the user to allow the location permission
     */
    private void requestPermissionAndCenter() {
        // Request location permission
        currentPosition.requestLocationPermission(
                // If the permission is granted, set the center with the current position
                () -> {
                    currentPosition.followLocation(true);
                    startTrace = currentPosition.getCurrentGeoPoint();
                    mapController.setCenter(startTrace);

                    salesmanTrace = new Polyline();
                    salesmanTrace.getOutlinePaint().setColor(getColor(R.color.blue_0));
                    salesmanTrace.getOutlinePaint().setStrokeWidth(5);
                    salesmanTrace.addPoint(startTrace);
                    mapView.getOverlayManager().add(salesmanTrace);

                    currentPosition.startLocationUpdates(location -> {
                        GeoPoint currentPoint = new GeoPoint(location);
                        Log.d(TAG, "requestPermissionAndCenter: " + currentPoint);
                        salesmanTrace.addPoint(currentPoint);
                        mapView.invalidate();
                        setNextClientInfo(route.getNextClient());
                    });
                    setNextClientInfo(route.getNextClient());
                },
                // If the permission is denied,
                // show a popup to ask the user to allow the location permission
                () -> {
                    DialogButton no = new DialogButton(getString(R.string.no_you_can_t_use_this_feature), DialogButton.getFinishListener(this));
                    DialogButton yes = new DialogButton(getString(R.string.yes_give_access), DialogButton.getFinishListener(this));
                    popup.showAlertDialog(getString(R.string.warning), getString(R.string.need_to_allow_location_permission), yes, null, no);
                }
        );
    }

    /**
     * Retrieve the route from the intent and set the information, like :
     * <ul>
     *     <li>The next client</li>
     *     <li>The distance to the next client</li>
     *     <li>The address to the next client</li>
     * </ul>
     */
    private void setRouteInformation() {
        Intent intent = getIntent();
        route = intent.getParcelableExtra(InfoItinerary.ROUTE_KEY);

        if (route == null) {
            Log.e(TAG, "onCreate: No itinerary found in the intent");
            popup.showAlertDialogOK(getString(R.string.error), getString(R.string.no_itinerary_retrieve), DialogButton.okFinish(this));
        }
        route.getExpectedClients().forEach(client -> client.setAddressDisplayName(this));
    }

    /**
     * Add a marker for each client in the list of expected clients
     *
     * @param expectedClients The list of expected clients
     */
    private void setExpectedClientMarker(ArrayList<Client> expectedClients, @NonNull Client nextClient) {
        for (int i = 0; i < expectedClients.size(); i++) {
            Client client = expectedClients.get(i);
            String title = MessageFormat.format("({0}) - {1}", i + 1, client.getCompanyName());
            mapMarker.addMarker(title, client.getAddressDisplayName(), client.getGeoPoint(), nextClient.equals(client) ? MapMarker.MarkerType.NEXT_CLIENT : MapMarker.MarkerType.EXPECTED_CLIENT);
        }
    }

    /**
     * Set the information of the next client, like :
     * <ul>
     *     <li>The company name</li>
     *     <li>The address</li>
     *     <li>The distance to the client</li>
     *     <li>The number of visited clients</li>
     * </ul>
     * Each of this information is set in the corresponding TextView
     * <p>
     *     This method needs the current position to calculate the distance to the client
     *
     * @param client The next client
     */
    private void setNextClientInfo(Client client) {
        Log.d(TAG, client.toString());
        client.setAddressDisplayName(this);
        clientName.setText(client.getCompanyName());
        clientAddress.setText(client.getAddressDisplayName());
        clientDistance.setText(getString(R.string.distance_in_km, distanceToClient(client)));
        counterVisitedClients.setText(getString(R.string.counter_visited_clients, 0, route.getNumberOfClientsExpected()));
    }

    /**
     * Calculate the distance between the current position and the client
     *
     * @param client The client to calculate the distance
     * @return The distance in kilometers
     */
    private double distanceToClient(Client client) {
        // TODO calculate with the roads and not in a straight line
        double distance = Double.NaN;
        try {
            distance = currentPosition.getCurrentGeoPoint().distanceToAsDouble(client.getGeoPoint()) / 1000;
        } catch (Exception e) {
            // Do nothing
        }
        return distance;
    }

    private void listClients() {
        Log.d(TAG, "listClients: ");
    }

    private void clientVisited() {
        Log.d(TAG, "clientVisited: ");
    }

    private void pauseResume() {
        Log.d(TAG, "pause: ");
        // Toggle the icon
        Drawable icon = AppCompatResources.getDrawable(this, route.isPaused() ? ICON_PLAY : ICON_PAUSE);
        pauseBtn.setBackground(icon);
        route.setPaused(!route.isPaused());
    }

    private void stop() {
        Log.d(TAG, "stop: ");
    }
}
