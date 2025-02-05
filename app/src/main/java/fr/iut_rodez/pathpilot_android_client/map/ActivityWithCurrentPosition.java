package fr.iut_rodez.pathpilot_android_client.map;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.views.MapView;

/**
 * Describe an activity with a map.
 */
public abstract class ActivityWithCurrentPosition extends AppCompatActivity {
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected MapView mapView;

    public MapView getMapView() {
        return mapView;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            // Needed for OSMdroid
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            // Needed for OSMdroid
            mapView.onPause();
        }
    }
}
