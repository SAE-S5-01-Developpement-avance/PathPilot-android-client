package fr.iut_rodez.pathpilot_android_client.home.itinerary;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.map.CurrentPosition;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

public class PlayerItinerary extends AppCompatActivity {

    private static final String TAG = PlayerItinerary.class.getSimpleName();

    private static final int ICON_PLAY = R.drawable.icon_start;
    private static final int ICON_PAUSE = R.drawable.icon_pause;

    private ImageButton detailClientBtn;
    private TextView clientName;
    private TextView clientAddress;
    private TextView clientDistance;
    private TextView counterVisitedClients;
    private MapView mapView;
    private ImageButton stopBtn;
    private ImageButton pauseBtn;
    private ImageButton clientVisitedBtn;
    private ImageButton listClientsBtn;

    private Itinerary itinerary;
    private boolean itineraryIsPause = false;

    private final Popup popup = new Popup(this);
    private final CurrentPosition currentPosition = new CurrentPosition(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Important! Initialise the osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.view_player_itinerary);

        detailClientBtn = findViewById(R.id.detail_client_btn);
        clientName = findViewById(R.id.client_name);
        clientAddress = findViewById(R.id.client_address);
        clientDistance = findViewById(R.id.client_distance);
        counterVisitedClients = findViewById(R.id.counter_visited_clients);
        mapView = findViewById(R.id.mapview);
        stopBtn = findViewById(R.id.stop_btn);
        pauseBtn = findViewById(R.id.pause_btn);
        clientVisitedBtn = findViewById(R.id.client_visited_btn);
        listClientsBtn = findViewById(R.id.clients_setting_btn);

        // Set onClickListener
        detailClientBtn.setOnClickListener(v -> Log.d(TAG, "onCreate: detailClientBtn"));
        findViewById(R.id.back_btn).setOnClickListener(v -> finish());
        stopBtn.setOnClickListener(v -> stop());
        pauseBtn.setOnClickListener(v -> pause());
        clientVisitedBtn.setOnClickListener(v -> clientVisited());
        listClientsBtn.setOnClickListener(v -> listClients());

        getItineraryFromIntent();

        initialiseMap();
    }

    private void initialiseMap() {
        Log.d(TAG, "initialiseMap: Initialising the map");
        mapView = findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        // Enable zoom buttons and multi-touch zoom
        Log.d(TAG, "initialiseMap: Enable zoom buttons and multi-touch zoom");
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        mapView.setMultiTouchControls(true);

        // Set the map center and zoom level
        Log.d(TAG, "initialiseMap: Set the map center and zoom level");
        IMapController mapController = mapView.getController();
        mapController.setZoom(10.0);
        // Set the map center to the given point or the default point
        Runnable setCenterWithCurrentPosition = () -> mapController.setCenter(getCurrentPositionOrDefault());
        currentPosition.requestLocationPermission(setCenterWithCurrentPosition, () -> {
            Popup.Button no = new Popup.Button("No (You can't use this feature)", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            Popup.Button yes = new Popup.Button("Yes give access", (dialog, which) -> {
                dialog.dismiss();
                currentPosition.requestLocationPermission(setCenterWithCurrentPosition, null);
            });
            popup.showAlertDialog("Warning", "You need to allow the location permission to use the map.", yes, null, no);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        currentPosition.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getItineraryFromIntent() {
        Intent intent = getIntent();
        itinerary = intent.getParcelableExtra(InfoItinerary.ITINERARY_KEY);

        if (itinerary == null) {
            Log.e(TAG, "onCreate: No itinerary found in the intent");
            popup.showAlertDialog("Error", "No itinerary found in the intent"); // TODO i18n
            finish();
        }
    }

    /**
     * @return The current position or the first client position if the current position is null.
     */
    private GeoPoint getCurrentPositionOrDefault() {
        var currentPoint = currentPosition.getCurrentGeoPoint(true);
        Log.d(TAG, "getCurrentPositionOrDefault: Current position: " + currentPoint);
        return currentPoint != null ? currentPoint : itinerary.getClients().get(0).getGeoPoint();
    }

    private void listClients() {
        Log.d(TAG, "listClients: ");
    }

    private void clientVisited() {
        Log.d(TAG, "clientVisited: ");
    }

    private void pause() {
        Log.d(TAG, "pause: ");
        // Toggle the icon
        Drawable icon = AppCompatResources.getDrawable(this, itineraryIsPause ? ICON_PLAY : ICON_PAUSE);
        pauseBtn.setBackground(icon);
        itineraryIsPause = !itineraryIsPause;
    }

    private void stop() {
        Log.d(TAG, "stop: ");
    }

}
