package fr.iut_rodez.pathpilot_android_client.map;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.List;
import java.util.Locale;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.map.CurrentPosition.ActivityWithCurrentPosition;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

public class MapSelection extends ActivityWithCurrentPosition implements MapEventsReceiver {

    private static final String TAG = MapSelection.class.getSimpleName();
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final GeoPoint PARIS_POINT = new GeoPoint(48.8566, 2.3522);

    private Button selectButton;
    private EditText adresseInput;
    private MapView map = null;

    private GeoPoint pointSelected = null;
    private Marker selectedMarker = null;

    private Popup popup;
    private CurrentPosition currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popup = new Popup(this);

        // Important! Initialise the osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.view_map_selection);

        selectButton = findViewById(R.id.select);
        selectButton.setOnClickListener(v -> sendSelectedPoint());
        selectButton.setEnabled(pointSelected != null);
        adresseInput = findViewById(R.id.adresse_input);

        findViewById(R.id.search).setOnClickListener(v -> searchAddress());

        // Initialise the map
        map = findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        currentPosition = new CurrentPosition(this);

        // Enable zoom buttons and multi-touch zoom
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        // Set the map center and zoom level
        IMapController mapController = map.getController();
        mapController.setZoom(13.0);
        // Set the map center to the given point or the default point
        mapController.setCenter(getGivenSelectedPointOrDefault());
        currentPosition.requestLocationPermission(() -> {
            mapController.setCenter(getGivenSelectedPointOrDefault());
        }, null);

        GeoPoint givenSelectedPoint = getGivenSelectedPoint();
        if (givenSelectedPoint != null) {
            setSelectedPoint(givenSelectedPoint);
        }

        // Add a map event overlay to handle the long press event
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(mapEventsOverlay);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        currentPosition.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public MapView getMapView() {
        return map;
    }

    /**
     * Get the selected point from the intent or return the default point
     * <p>
     * The default point is the center of Paris
     * </p>
     *
     * @return the selected point or the default point
     */
    private GeoPoint getGivenSelectedPointOrDefault() {
        var point = getGivenSelectedPoint();
        if (point == null) {
            point = currentPosition.getCurrentGeoPoint();
        }
        return point != null ? point : PARIS_POINT;
    }

    private GeoPoint getGivenSelectedPoint() {
        Intent intent = getIntent();
        GeoPoint point = null;
        if (intent != null && intent.hasExtra(KEY_LATITUDE) && intent.hasExtra(KEY_LONGITUDE)) {
            double latitude = intent.getDoubleExtra(KEY_LATITUDE, Double.NaN);
            double longitude = intent.getDoubleExtra(KEY_LONGITUDE, Double.NaN);

            if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
                point = new GeoPoint(latitude, longitude);
            }
        }
        return point;
    }

    /**
     * Send to the activity who call this one the selected point
     * If no point is selected, do nothing
     */
    private void sendSelectedPoint() {
        if (pointSelected != null) {
            Log.d(TAG, "Selected point : " + pointSelected.getLatitude() + ", " + pointSelected.getLongitude());
            Intent intent = new Intent();

            intent.putExtra(KEY_LATITUDE, pointSelected.getLatitude());
            intent.putExtra(KEY_LONGITUDE, pointSelected.getLongitude());
            setResult(RESULT_OK, intent);

            finish();
        }
    }

    private void searchAddress() {
        String addressStr = adresseInput.getText().toString();
        if (!addressStr.isEmpty()) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocationName(addressStr, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    // An improvement could be to show a list of addresses and let the user choose
                    Address address = addresses.get(0);

                    // Set the selected point to the address
                    setSelectedPoint(new GeoPoint(address.getLatitude(), address.getLongitude()));

                    centerToSelected();
                } else {
                    popup.showToastLong(getString(R.string.adress_not_found)); // TODO: i18n
                }

            } catch (Exception e) {
                // TODO: i18n
                popup.showAlertDialog("Erreur", "Erreur lors de la recherche: " + e.getMessage());
            }
        }
    }

    private void centerToSelected() {
        map.getController().animateTo(pointSelected);
        map.getController().setZoom(17.0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Needed for OSMdroid
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Needed for OSMdroid
        map.onPause();
    }

    /**
     * Called when a single tap event is detected
     *
     * @param p the point where the tap occurred
     * @return true if the event is consumed, false otherwise
     */
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    /**
     * Called when a long press event is detected
     *
     * @param p the point where the long press occurred
     * @return true if the event is consumed, false otherwise
     */
    @Override
    public boolean longPressHelper(GeoPoint p) {
        if (p != null) {
            setSelectedPoint(p);
        }
        return true;
    }

    /**
     * Set the selected point on the map
     *
     * @param geoPoint the point to select
     */
    private void setSelectedPoint(GeoPoint geoPoint) {
        if (selectedMarker != null) {
            // Remove the previous selected marker if it exists
            map.getOverlays().remove(selectedMarker);
        }
        pointSelected = geoPoint;

        // Add a new marker at the selected point
        selectedMarker = new Marker(map);
        selectedMarker.setPosition(pointSelected);
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(selectedMarker);
        map.invalidate();

        // Enable the select button
        selectButton.setEnabled(pointSelected != null);
    }
}
