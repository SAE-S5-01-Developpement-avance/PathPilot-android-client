package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.home.routes.Route;
import fr.iut_rodez.pathpilot_android_client.map.CurrentPosition;
import fr.iut_rodez.pathpilot_android_client.map.CurrentPosition.ActivityWithCurrentPosition;
import fr.iut_rodez.pathpilot_android_client.util.LocationNameProvider;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;

public class PlayerItinerary extends ActivityWithCurrentPosition {

    private static final String TAG = PlayerItinerary.class.getSimpleName();

    private static final int ICON_PLAY = R.drawable.icon_start;
    private static final int ICON_PAUSE = R.drawable.icon_pause;

    private TextView clientName;
    private TextView clientAddress;
    private TextView clientDistance;
    private TextView counterVisitedClients;
    private MapView mapView;
    private ImageButton pauseBtn;

    private Route route;

    private final Popup popup = new Popup(this);
    private CurrentPosition currentPosition;
    private IMapController mapController;
    RoadManager roadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Important! Initialise the osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.view_player_itinerary);

        ImageButton detailClientBtn = findViewById(R.id.detail_client_btn);
        clientName = findViewById(R.id.client_name);
        clientAddress = findViewById(R.id.client_address);
        clientDistance = findViewById(R.id.client_distance);
        counterVisitedClients = findViewById(R.id.counter_visited_clients);
        mapView = findViewById(R.id.mapview);
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

        // Show a loading popup. The dialog is dismissed when the route is drawn
        popup.showProgressDialog();

        setRouteInformation();
        initialiseMap();
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
     * @see PlayerItinerary#requestPermissionAndCenter()
     */
    private void initialiseMap() {
        Log.d(TAG, "initialiseMap: Initialising the map");
        mapView = findViewById(R.id.mapview);
        mapController = mapView.getController();
        roadManager = new OSRMRoadManager(this, getString(R.string.app_name));

        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);
        mapController.setZoom(15.0);
        mapController.setCenter(route.getSalesmanHome());

        currentPosition = new CurrentPosition(this);
        requestPermissionAndCenter();

        setRoutePolyline(route.getExpectedClients(), route.getSalesmanHome());
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
                    setCenter();
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
     * Set the center of the map to the current position
     * Every time the position is updated, the map is centered on the current position
     */
    private void setCenter() {
        currentPosition.enableCenterOnLocation();
        currentPosition.runOnFirstFix(() -> runOnUiThread(() -> {
            GeoPoint currentPoint = currentPosition.getCurrentGeoPoint();
            mapController.setCenter(currentPoint);
            mapController.animateTo(currentPoint);
        }));
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
     * Draw a line that follows the road and link all the clients.
     * <p>
     * The line is drawn between the salesman home and the first client, then between each client.
     * The last line is drawn between the last client and the salesman home.
     * </p>
     *
     * @param expectedClients The list of expected clients
     * @param salesmanHome    The home of the salesman
     */
    private void setRoutePolyline(ArrayList<Client> expectedClients, GeoPoint salesmanHome) {
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(salesmanHome);
        expectedClients.forEach(client -> waypoints.add(client.getGeoPoint()));
        waypoints.add(salesmanHome);


        new Thread(() -> {
            // Get the road between the waypoints
            Road road = roadManager.getRoad(waypoints);
            popup.dismissProgressDialog();

            // if the road build process failed, show an error dialog
            if (road.mStatus != Road.STATUS_OK) {
                Log.e(TAG, "setRoutePolyline: Error while drawing the road");
                popup.showAlertDialogOK(getString(R.string.error), getString(R.string.error_while_drawing_the_road), DialogButton.okDismiss(this));
            }

            // Draw the road on the map
            Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
            Paint outlinePaint = roadOverlay.getOutlinePaint();
            outlinePaint.setStrokeWidth(10);
            outlinePaint.setColor(getColor(R.color.blue_1));
            mapView.getOverlays().add(roadOverlay);

            setExpectedClientMarker(route.getExpectedClients());
            addMarker(route.getSalesmanHome(), "Home", LocationNameProvider.getAddressName(this, route.getSalesmanHome()));
        }).start();
    }

    /**
     * Add a marker for each client in the list of expected clients
     *
     * @param expectedClients The list of expected clients
     */
    private void setExpectedClientMarker(ArrayList<Client> expectedClients) {
        for (int i = 0; i < expectedClients.size(); i++) {
            addClientMarker(expectedClients.get(i), i + 1);
        }
    }

    private void addClientMarker(Client client, int index) {
        addMarker(client.getGeoPoint(), String.format("(%d) - %s", index, client.getCompanyName()), client.getAddressDisplayName());
    }

    /**
     * Add a marker on the map
     * <p>
     * The marker is added with the given position, title and description
     * <br>
     * The title and description are displayed when the user click on the marker
     * </p>
     *
     * @param position    The position of the marker
     * @param title       The title of the marker
     * @param description The description of the marker
     */
    private void addMarker(GeoPoint position, String title, String description) {
        Marker marker = new Marker(mapView);
        marker.setPosition(position);
        marker.setTitle(title);
        marker.setSnippet(description);
        mapView.getOverlays().add(marker);
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
        return currentPosition.getCurrentGeoPoint().distanceToAsDouble(client.getGeoPoint()) / 1000;
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        currentPosition.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public MapView getMapView() {
        return mapView;
    }
}
