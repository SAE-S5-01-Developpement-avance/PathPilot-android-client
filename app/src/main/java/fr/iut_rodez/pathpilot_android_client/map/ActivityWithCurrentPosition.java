package fr.iut_rodez.pathpilot_android_client.map;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.views.MapView;

public abstract class ActivityWithCurrentPosition extends AppCompatActivity {
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    abstract public MapView getMapView();
}
