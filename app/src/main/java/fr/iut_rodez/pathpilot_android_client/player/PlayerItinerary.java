package fr.iut_rodez.pathpilot_android_client.player;

import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import org.json.JSONException;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.Polyline;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.home.clients.entity.Client;
import fr.iut_rodez.pathpilot_android_client.home.itinerary.InfoItinerary;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.Route;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RouteClient;
import fr.iut_rodez.pathpilot_android_client.home.routes.entity.RouteState;
import fr.iut_rodez.pathpilot_android_client.home.routes.service.IRouteService;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.map.ActivityWithCurrentPosition;
import fr.iut_rodez.pathpilot_android_client.util.Parser;
import fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler;
import fr.iut_rodez.pathpilot_android_client.util.map.LocationNameProvider;
import fr.iut_rodez.pathpilot_android_client.util.map.MapMarker;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class PlayerItinerary extends ActivityWithCurrentPosition {

    private static final String TAG = PlayerItinerary.class.getSimpleName();

    public static final String JWT_TOKEN_KEY = "Player_JWTToken";
    private static final int ICON_PLAY = R.drawable.icon_start;
    private static final int ICON_PAUSE = R.drawable.icon_pause;
    public static final String ROUTE_STOPPED_KEY = "route_stopped";
    private final Popup popup = new Popup(this);
    private final IRouteService routeService = ServiceFactory.getRouteService();
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
    private JWTToken jwtToken;
    /**
     * This list contains the clients that have already been notified to the salesman.
     */
    private final ArrayList<Client> clientAlreadyNotified = new ArrayList<>();
    private Vibrator vibrator;
    private ImageButton stopBtn;
    private ImageButton clientVisitedBtn;
    private ImageButton detailClientBtn;

    private JWTToken getJwtTokenFromIntent() {
        JWTToken jwtTokenFind = null;
        Intent intent = getIntent();

        if (intent.hasExtra(JWT_TOKEN_KEY)) {
            jwtTokenFind = intent.getParcelableExtra(JWT_TOKEN_KEY);
        }

        return jwtTokenFind;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //The parent class ActivityWithCurrentPosition,
        // need the view to be set because it uses findViewById
        setContentView(R.layout.view_player_itinerary);
        super.onCreate(savedInstanceState);

        jwtToken = getJwtTokenFromIntent();
        setRouteInformation();

        if (route == null) {
            popup.showAlertDialogOK(getString(R.string.error), getString(R.string.no_itinerary_retrieve), DialogButton.okFinish(this));
        } else if (jwtToken == null) {
            popup.showAlertDialogOK(getString(R.string.error), getString(R.string.no_token_retrieve), DialogButton.okFinish(this));
        } else {
            mapMarker = new MapMarker(this);
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            detailClientBtn = findViewById(R.id.detail_client_btn);
            clientName = findViewById(R.id.client_name);
            clientAddress = findViewById(R.id.client_address);
            clientDistance = findViewById(R.id.client_distance);
            counterVisitedClients = findViewById(R.id.counter_visited_clients);
            stopBtn = findViewById(R.id.stop_btn);
            pauseBtn = findViewById(R.id.pause_btn);
            clientVisitedBtn = findViewById(R.id.client_visited_btn);
            ImageButton listClientsBtn = findViewById(R.id.clients_setting_btn);


            // Set onClickListener
            detailClientBtn.setOnClickListener(v -> Log.d(TAG, "onCreate: detailClientBtn"));
            findViewById(R.id.back_btn).setOnClickListener(v -> {
                tryPauseRoute();
                finish();
            });
            stopBtn.setOnLongClickListener(v -> stop());
            stopBtn.setOnClickListener(v -> popup.showToastLong(getString(R.string.long_click_to_stop_the_route)));
            pauseBtn.setOnClickListener(v -> pauseResume());
            clientVisitedBtn.setOnClickListener(v -> clientVisited());
            listClientsBtn.setOnClickListener(v -> listClients());

            salesmanTrace = new Polyline();
            salesmanTrace.getOutlinePaint().setColor(getColor(R.color.blue_0));
            salesmanTrace.getOutlinePaint().setStrokeWidth(5);
            mapView.getOverlays().add(salesmanTrace);

            // Show a loading popup.
            popup.showProgressDialog();

            initialiseMap();
            popup.dismissProgressDialog();

            // update buttons with the route state
            if (route.getState() == RouteState.STOPPED || route.getState() == RouteState.FINISHED) {
                disableRouteActions();
            }
        }
    }

    private void disableRouteActions() {
        stopBtn.setOnClickListener(v -> popup.showToastLong(getString(R.string.route_is_stopped_action_unavailable)));
        pauseBtn.setOnClickListener(v -> popup.showToastLong(getString(R.string.route_is_stopped_action_unavailable)));
        clientVisitedBtn.setOnClickListener(v -> popup.showToastLong(getString(R.string.route_is_stopped_action_unavailable)));
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
        } else {
            route.getClients().forEach(routeClient -> routeClient.getClient().setAddressDisplayName(this));
        }
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

        setRouteMarkers();
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

                    enableTracer();
                    setNextClientInfo(route.getNextClient());
                    startOrResumeRouteIfNeeded();
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

    private void startOrResumeRouteIfNeeded() {
        if (route.getState() == RouteState.NOT_STARTED) {
            Log.d(TAG, "Start route");
            popup.showProgressDialog();

            routeService.startRoute(this, route, currentPosition.getCurrentGeoPoint(), jwtToken,
                    response -> {
                        popup.dismissProgressDialog();
                        route.setState(RouteState.IN_PROGRESS);
                    },
                    error -> {
                        popup.dismissProgressDialog();
                        VolleyErrorHandler.handleError(this, error);
                    }
            );
        } else if (route.getState() == RouteState.PAUSED) {
            resumeRoute();
        }
    }

    /**
     *
     */
    private void enableTracer() {
        route.getSalesmanPositions().forEach(position -> salesmanTrace.addPoint(position));
        Log.d(TAG, "enableTracer: " + route.getSalesmanPositions());

        currentPosition.startLocationUpdates(location -> {
            GeoPoint currentPoint = new GeoPoint(location);

            Log.d(TAG, "update position: " + currentPoint);

            routeService.updateSalesmanPosition(
                    this,
                    jwtToken,
                    currentPoint,
                    route,
                    response -> {
                        Log.d(TAG, "updateSalesmanPosition: " + response);
                        // Update the salesman trace
                        salesmanTrace.addPoint(currentPoint);
                        mapView.invalidate();

                        // Get client near the salesman if any
                        List<Client> clientNearSalesman = Collections.emptyList();
                        if (response.has("._embedded.clientResponseModelList")) {
                            try {
                                clientNearSalesman = Parser.getClient(response.getJSONArray("._embedded.clientResponseModelList"));
                                clientNearSalesman.forEach(client -> client.setAddressDisplayName(this));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        notifyIfClientNearSalesman(clientNearSalesman);
                    },
                    error -> Log.e(TAG, "updateSalesmanPosition: ", error)
            );
        });
    }

    /**
     * Send a push notification to the salesman if a client is near.
     * <p>
     *     The notification will show the name of the client and the distance to the client
     *     <br>
     *     Then the client will be added to the list of clients that have already been notified
     * </p>
     * @param clientNearSalesman The list of client near the salesman
     */
    private void notifyIfClientNearSalesman(List<Client> clientNearSalesman) {
        if (!clientNearSalesman.isEmpty()) {
            clientNearSalesman.stream()
                    .filter(client -> !clientAlreadyNotified.contains(client))
                    .findFirst()
                    .ifPresent(client -> {

                        if (vibrator != null && vibrator.hasVibrator()) {
                            vibrator.vibrate(VibrationEffect.createOneShot(
                                    200,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            ));
                        }

                        popup.showAutoDismissAlertDialog(
                                getString(R.string.client_near_you, client.getCompanyName()),
                                getString(R.string.client_near_you_description, client.getCompanyName(), client.getAddressDisplayName(), distanceToClient(client) * 1000),
                                Duration.ofSeconds(5)
                        );

                        clientAlreadyNotified.add(client);
                    });
        }
    }

    private void setClientMarkers(List<RouteClient> clients, RouteClient nextClient) {
        for (int i = 0, clientsSize = clients.size(); i < clientsSize; i++) {
            RouteClient client = clients.get(i);
            MapMarker.MarkerType markerType;
            switch (client.getState()) {
                case VISITED -> markerType = MapMarker.MarkerType.CLIENT_VISITED;
                case EXPECTED -> markerType = MapMarker.MarkerType.EXPECTED_CLIENT;
                case SKIPPED -> markerType = MapMarker.MarkerType.CLIENT_IGNORED;
                default ->
                        throw new IllegalStateException("Unexpected value: " + client.getState());
            }
            if (client.equals(nextClient)) {
                Log.d(TAG, "NextClient" + client);
                markerType = MapMarker.MarkerType.NEXT_CLIENT;
            } else {
                Log.d(TAG, "Not next client" + client);
            }
            mapMarker.addMarker(
                    MessageFormat.format("({0}) - {1}", i + 1, client.getClient().getCompanyName()),
                    client.getClient().getAddressDisplayName(),
                    client.getClient().getGeoPoint(),
                    markerType
            );
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
     * @param routeClient The next client
     */
    private void setNextClientInfo(RouteClient routeClient) {
        if (routeClient != null) {
            Log.d(TAG, routeClient.toString());
            Client client = routeClient.getClient();

            client.setAddressDisplayName(this);
            clientName.setText(client.getCompanyName());
            clientAddress.setText(client.getAddressDisplayName());
            clientDistance.setText(getString(R.string.distance_in_km, distanceToClient(client)));
            counterVisitedClients.setText(getString(R.string.counter_visited_clients, 0, route.getNumberOfClientsExpected()));
        } else {
            clientAddress.setText("-");
            clientName.setText("-");
            clientDistance.setText("");
            detailClientBtn.setOnClickListener(v -> popup.showToastLong(getString(R.string.route_is_stopped_action_unavailable)));
        }
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
        popup.showProgressDialog();
        routeService.clientVisited(
                this,
                route,
                route.getNextClient().getClient().getId(),
                jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "clientVisited: " + response);
                    route.clientHasBeenVisited();
                    RouteClient nextClient = route.getNextClient();
                    setNextClientInfo(nextClient);
                    if (nextClient == null) {
                        currentPosition.stopLocationUpdates();
                        route.setState(RouteState.FINISHED);
                        routeService.stopRoute(
                                this,
                                route,
                                jwtToken,
                                responseStop -> {
                                    Log.d(TAG, "stopRoute: " + responseStop);
                                    popup.showAlertDialogOK(getString(R.string.route_stopped), getString(R.string.route_stopped_description), DialogButton.okDismiss(this));
                                },
                                error -> {
                                    Log.e(TAG, "stopRoute: ", error);
                                    handleError(this, error);
                                }
                        );
                        disableRouteActions();
                    }
                    mapMarker.removeAllMarkers();
                    setRouteMarkers();
                    mapView.invalidate();
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "clientVisited: ", error);
                    VolleyErrorHandler.handleError(this, error);
                }
        );
    }

    private void setRouteMarkers() {
        setClientMarkers(route.getClients(), route.getNextClient());
        mapMarker.addMarker(getString(R.string.home), LocationNameProvider.getAddressName(this, route.getSalesmanHome()), route.getSalesmanHome(), MapMarker.MarkerType.SALESMAN_HOME);
    }

    private void pauseResume() {
        Log.d(TAG, "pause: ");
        // Toggle the icon
        Log.d(TAG, "State: " + route.getState());
        if (route.getState().equals(RouteState.PAUSED)) {
            resumeRoute();
        } else if (route.getState().equals(RouteState.IN_PROGRESS)){
            pauseRoute();
        }
    }

    private void pauseRoute() {
        popup.showProgressDialog();
        routeService.pauseRoute(this, route, jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "pauseRoute: " + response);
                    route.setState(RouteState.PAUSED);
                    Drawable icon = AppCompatResources.getDrawable(this, ICON_PLAY);
                    pauseBtn.setBackground(icon);
                },
                error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "pauseRoute: ", error);
                    VolleyErrorHandler.handleError(this, error);
                });
    }

    private void tryPauseRoute() {
        if (route.getState() == RouteState.IN_PROGRESS) {
            Log.d(TAG, "tryPauseRoute");
            routeService.pauseRoute(this, route, jwtToken,
                    response -> {
                        Log.d(TAG, "tryPauseRoute: " + response);
                    },
                    error -> {
                        Log.e(TAG, "tryPauseRoute: ", error);
                    }
            );
            route.setState(RouteState.PAUSED);
        }
    }

    private void resumeRoute() {
        popup.showProgressDialog();
        routeService.resumeRoute(this, route, currentPosition.getCurrentGeoPoint(), jwtToken,
                response -> {
                    popup.dismissProgressDialog();
                    Log.d(TAG, "resumeRoute: " + response);
                    route.setState(RouteState.IN_PROGRESS);
                    pauseBtn.setBackground(AppCompatResources.getDrawable(this, ICON_PAUSE));
                }, error -> {
                    popup.dismissProgressDialog();
                    Log.e(TAG, "resumeRoute: ", error);
                    VolleyErrorHandler.handleError(this, error);
                });
    }

    /**
     * Stop the launched route.
     */
    private boolean stop() {
        popup.showAlertDialog(getString(R.string.stop_route_popup_title), getString(R.string.stop_route_popup_text),
                new DialogButton(getString(R.string.stop_route_confirm_dialog_btn),(dialog, which) -> {
                    dialog.dismiss();
                    currentPosition.stopLocationUpdates();
                    Log.d(TAG, "State : " + RouteState.STOPPED);
                    route.setState(RouteState.STOPPED);

                    popup.showProgressDialog();
                    routeService.stopRoute(this, route, jwtToken,
                            response -> {
                                popup.dismissProgressDialog();
                                Log.d(TAG, "onResponse: " + response);
                                finish();
                            },
                            error -> {
                                popup.dismissProgressDialog();
                                Log.e(TAG, "onErrorResponse: ", error);
                                handleError(this, error);
                            });
                }),
                null,
                DialogButton.okDismiss(this));
        return true;
    }

    public JWTToken getJWTToken() {
        return jwtToken;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: Route state " + route.getState());
        if (route.getState() == RouteState.PAUSED) {
            resumeRoute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Route state" + route.getState());
        tryPauseRoute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Route state " + route.getState());
        tryPauseRoute();
    }
}
