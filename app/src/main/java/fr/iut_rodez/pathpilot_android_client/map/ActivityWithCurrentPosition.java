package fr.iut_rodez.pathpilot_android_client.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.util.map.CurrentPosition;

/**
 * Describe an activity with a map.
 * <p>
 *     When a class extends this class, it must as a MapView with the id {@link #MAPVIEW_ID} in its layout.
 *     <br>
 *     Also the class need to implement the {@link Activity#onCreate(Bundle)} method and call the super method after the {@link Activity#setContentView(View)} method.
 *     Like that:
 *     <pre>
 * {@code
 * @Override
 * protected void onCreate(Bundle savedInstanceState) {
 *     setContentView(R.layout.view_map_selection);
 *     super.onCreate(savedInstanceState);
 *     // Your code
 * }
 * }
 *    </pre>
 * </p>
 */
public class ActivityWithCurrentPosition extends AppCompatActivity {

    public static final int MAPVIEW_ID = R.id.mapview;

    protected MapView mapView;
    protected CurrentPosition currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Important! Initialise the osmdroid configuration
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView = findViewById(MAPVIEW_ID);
        currentPosition = new CurrentPosition(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            // Needed for OSMdroid
            mapView.onResume();
        }
        if (currentPosition != null) {
            currentPosition.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            // Needed for OSMdroid
            mapView.onPause();
        }
        if (currentPosition != null) {
            currentPosition.onPause();
        }
    }

    public MapView getMapView() {
        return mapView;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        currentPosition.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
