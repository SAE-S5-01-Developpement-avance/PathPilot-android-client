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

    private MapView map = null;
    private Button selectButton = null;

    private GeoPoint pointSelected = null;
    private Marker selectedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Important! Initialiser la configuration OSMdroid
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Définir le layout
        setContentView(R.layout.view_map_selection);

        // Initialiser la carte
        map = findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);

        // Activer le zoom et les contrôles
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
//        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        // Contrôleur de carte
        IMapController mapController = map.getController();
        mapController.setZoom(10.0);

        // Centrer la carte sur Paris
        GeoPoint startPoint = new GeoPoint(48.8566, 2.3522);
        mapController.setCenter(startPoint);

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(this);
        map.getOverlays().add(mapEventsOverlay);

        selectButton = findViewById(R.id.select);
        selectButton.setOnClickListener(v -> sendSelectedPoint());
        selectButton.setEnabled(pointSelected != null);
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
        // Nécessaire pour OSMdroid
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Nécessaire pour OSMdroid
        map.onPause();
    }

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) {
        return false;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) {
        if (p != null) {
            double latitude = p.getLatitude();
            double longitude = p.getLongitude();
            Log.d("MapClick", "Coordonnées : Lat=" + latitude + ", Lon=" + longitude);
            setSelectedPoint(p);
        }
        return true;
    }

    private void setSelectedPoint(GeoPoint geoPoint) {
        if (selectedMarker != null) {
            map.getOverlays().remove(selectedMarker);
        }
        pointSelected = geoPoint;

        selectedMarker = new Marker(map);
        selectedMarker.setPosition(pointSelected);
        selectedMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(selectedMarker);
        map.invalidate();

        selectButton.setEnabled(pointSelected != null);
    }
}
