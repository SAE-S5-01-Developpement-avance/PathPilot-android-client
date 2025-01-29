package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.map.CurrentPosition;
import fr.iut_rodez.pathpilot_android_client.map.CurrentPosition.ActivityWithCurrentPosition;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

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

    private Itinerary itinerary;
    private Client nextClient; // TODO read this data from a Route
    private boolean itineraryIsPause = false; // TODO read this data from a Route

    private final Popup popup = new Popup(this);
    private CurrentPosition currentPosition;
    private IMapController mapController;

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

        initialiseMap();
    }

    private void initialiseMap() {
        Log.d(TAG, "initialiseMap: Initialising the map");
        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        // Enable zoom buttons and multi-touch zoom
        Log.d(TAG, "initialiseMap: Enable zoom buttons and multi-touch zoom");
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);

        // Set the map center and zoom level
        Log.d(TAG, "initialiseMap: Set the map center and zoom level");
        mapController = mapView.getController();
        mapController.setZoom(15.0);

        currentPosition = new CurrentPosition(this);
        requestPermissionAndCenter();
    }

    private void requestPermissionAndCenter() {
        // Request location permission
        currentPosition.requestLocationPermission(
                // If the permission is granted, set the center with the current position
                () -> {
                    setCenter();
                    setInformationWithIntent();
                },
                // If the permission is denied,
                // show a popup to ask the user to allow the location permission
                () -> {
                    Popup.Button no = new Popup.Button(getString(R.string.no_you_can_t_use_this_feature), (dialog, which) -> {
                        dialog.dismiss();
                        Log.d(TAG, "initialiseMap: Said no, so finishing the activity");
                        finish();
                    });
                    Popup.Button yes = new Popup.Button(getString(R.string.yes_give_access), (dialog, which) -> {
                        dialog.dismiss();
                        Log.d(TAG, "initialiseMap: Said yes, so finishing the activity, so the user can allow the permission");
                        finish();
                    });
                    popup.showAlertDialog(getString(R.string.warning), getString(R.string.need_to_allow_location_permission), yes, null, no);
                }
        );
    }

    private void setCenter() {
        currentPosition.runOnFirstFix(() -> runOnUiThread(() -> {
            GeoPoint currentPoint = currentPosition.getCurrentGeoPoint();
            mapController.setCenter(currentPoint);
            mapController.animateTo(currentPoint);
        }));
    }

    /**
     * Retrieve the itinerary from the intent and set the information, like :
     * <ul>
     *     <li>The next client</li>
     *     <li>The distance to the next client</li>
     *     <li>The address to the next client</li>
     * </ul>
     */
    private void setInformationWithIntent() {
        Intent intent = getIntent();
        itinerary = intent.getParcelableExtra(InfoItinerary.ITINERARY_KEY);

        if (itinerary == null) {
            Log.e(TAG, "onCreate: No itinerary found in the intent");
            popup.showAlertDialog("Error", "No itinerary found in the intent"); // TODO i18n
            finish();
        }

        nextClient = itinerary.getClients().get(0);
        clientName.setText(nextClient.getCompanyName());
        clientAddress.setText(nextClient.getAddressDisplayName());
        clientDistance.setText(getString(R.string.distance_in_km, distanceToClient(nextClient)));
        counterVisitedClients.setText(getString(R.string.counter_visited_clients, 0, itinerary.getClients().size()));
    }

    private double distanceToClient(Client nextClient) {
        // TODO calculate with the roads and not in a straight line
        return currentPosition.getCurrentGeoPoint().distanceToAsDouble(nextClient.getGeoPoint()) / 1000;
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
        Drawable icon = AppCompatResources.getDrawable(this, itineraryIsPause ? ICON_PLAY : ICON_PAUSE);
        pauseBtn.setBackground(icon);
        itineraryIsPause = !itineraryIsPause;
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
