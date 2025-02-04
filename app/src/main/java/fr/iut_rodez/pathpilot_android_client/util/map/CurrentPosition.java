package fr.iut_rodez.pathpilot_android_client.util.map;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class CurrentPosition {

    public static final int REQUEST_POSITION_CODE = 1;
    private static final String TAG = CurrentPosition.class.getSimpleName();
    private final ActivityWithCurrentPosition activity;
    private Runnable permissionGrantedCallback;
    private Runnable permissionDeniedCallback;
    private final MyLocationNewOverlay myLocationOverlay;
    private boolean centerOnLocation;

    /**
     * Create a new CurrentPosition object
     *
     * @param activity The activity
     * @see ActivityWithCurrentPosition
     */
    public CurrentPosition(ActivityWithCurrentPosition activity) {
        this.activity = activity;

        this.myLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(activity),
                activity.getMapView()
        );
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
    }

    @NonNull
    private static GeoPoint getGeoPoint(Location location) {
        return new GeoPoint(location.getLatitude(), location.getLongitude());
    }

    public boolean isLocationPermissionGranted() {
        final String accessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        final String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;

        return checkSelfPermission(activity, accessCoarseLocation) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(activity, accessFineLocation) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request location permission with an optional callback
     *
     * @param grantedCallback Runnable to execute when permission is granted
     * @param deniedCallback  Runnable to execute when permission is denied
     */
    public void requestLocationPermission(Runnable grantedCallback, Runnable deniedCallback) {
        this.permissionGrantedCallback = grantedCallback;
        this.permissionDeniedCallback = deniedCallback;
        final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Log.d(TAG, "requestLocationPermission: Requesting GPS Location permission");
        activity.requestPermissions(permissions, REQUEST_POSITION_CODE);
    }

    public void requestLocationPermission() {
        requestLocationPermission(null, null);
    }

    /**
     * Handle the result of the permission request
     * <p>
     * <h1>This method need to be called by the Activity who use this method</h1>
     * <pre>
     * {@code
     * @Override
     * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
     *     super.onRequestPermissionsResult(requestCode, permissions, grantResults);
     *     currentPosition.onRequestPermissionsResult(requestCode, permissions, grantResults);
     * }
     * }
     * </pre>
     * </p>
     *
     * @param requestCode  The code of the request
     * @param permissions  The permissions requested
     * @param grantResults The result of the request
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     * @see CurrentPosition#requestLocationPermission(Runnable, Runnable)
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "" + requestCode);
        if (requestCode == REQUEST_POSITION_CODE) {
            Log.d(TAG, "" + grantResults);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "" + grantResults[0]);
                Log.d(TAG, "GPS Location permission granted");

                // Execute callback if provided
                if (permissionGrantedCallback != null) {
                    permissionGrantedCallback.run();
                    // Reset callback to avoid multiple executions
                    permissionGrantedCallback = null;
                }
            } else {
                // Permission denied
                Log.d(TAG, "Location permission denied");

                // Execute callback if provided
                if (permissionDeniedCallback != null) {
                    permissionDeniedCallback.run();
                    // Reset callback to avoid multiple executions
                    permissionDeniedCallback = null;
                }
            }
        }
    }

    /**
     * @return The current position
     */
    public GeoPoint getCurrentGeoPoint() {
        GeoPoint myLocation = myLocationOverlay.getMyLocation();
        Log.d(TAG, "getCurrentGeoPoint: " + myLocation);
        if (myLocation == null) {
            myLocation = positionFromManager();
        }
        return myLocation;
    }

    @SuppressLint("MissingPermission") // We check the permission with isLocationPermissionGranted
    private GeoPoint positionFromManager() {
        GeoPoint myLocation = null;
        if (isLocationPermissionGranted()) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    myLocation = getGeoPoint(location);
                }
            }
        }
        return myLocation;
    }

    /**
     * As soon as we can get the user location, execute the runnable.
     * <p>
     * If the user location is already available, the runnable will be executed immediately.<br>
     * Otherwise, the runnable will be executed as soon as the user location is available.
     * </p
     *
     * @param runnable The runnable
     * @see MyLocationNewOverlay#runOnFirstFix(Runnable)
     */
    public void runOnFirstFix(Runnable runnable) {
        Runnable locationFixRunnable = () -> {
            runnable.run();
            if (centerOnLocation) {
                activity.runOnUiThread(() -> {
                    GeoPoint currentPoint = getCurrentGeoPoint();
                    activity.getMapView().getController().setCenter(currentPoint);
                    activity.getMapView().getController().animateTo(currentPoint);
                    activity.getMapView().getOverlays().add(myLocationOverlay);
                });
            }
        };
        myLocationOverlay.runOnFirstFix(locationFixRunnable);
    }

    /**
     * Disable the center on location
     * <p>
     * The map will not center automatically on the user location
     * </p>
     */
    public void disableCenterOnLocation() {
        centerOnLocation = false;
        myLocationOverlay.disableFollowLocation();
    }

    public void enableCenterOnLocation() {
        centerOnLocation = true;
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
    }

    public abstract static class ActivityWithCurrentPosition extends AppCompatActivity {
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        abstract public MapView getMapView();
    }
}
