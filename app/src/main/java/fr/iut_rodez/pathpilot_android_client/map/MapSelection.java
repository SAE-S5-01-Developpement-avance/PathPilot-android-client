package fr.iut_rodez.pathpilot_android_client.map;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

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

import fr.iut_rodez.pathpilot_android_client.R;

public class MapSelection extends AppCompatActivity implements MapEventsReceiver {

    private static final String TAG = MapSelection.class.getSimpleName();
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final GeoPoint PARIS_POINT = new GeoPoint(48.8566, 2.3522);

    private MapView map = null;
    private Button selectButton = null;

    private GeoPoint pointSelected = null;
    private Marker selectedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Important! Initialise the osmdroid configuration
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.view_map_selection);

        selectButton = findViewById(R.id.select);
        selectButton.setOnClickListener(v -> sendSelectedPoint());
        selectButton.setEnabled(pointSelected != null);

        // Initialise the map
        map = findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Enable zoom buttons and multi-touch zoom
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        // Set the map center and zoom level
        IMapController mapController = map.getController();
        mapController.setZoom(10.0);
        // Set the map center to Paris
        mapController.setCenter(PARIS_POINT);

        // Add a map event overlay to handle the long press event
        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(mapEventsOverlay);
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
     * @param p the point where the tap occurred
     * @return true if the event is consumed, false otherwise
     */
    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    /**
     * Called when a long press event is detected
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
