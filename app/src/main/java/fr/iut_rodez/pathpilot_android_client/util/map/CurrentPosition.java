package fr.iut_rodez.pathpilot_android_client.util.map;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import fr.iut_rodez.pathpilot_android_client.map.ActivityWithCurrentPosition;

public class CurrentPosition {

    public static final int REQUEST_POSITION_CODE = 1;
    private static final String TAG = CurrentPosition.class.getSimpleName();
    public static final int LOCATION_UPDATE_MIN_TIME_MS = 1000;
    public static final int LOCATION_UPDATE_MIN_DISTANCE_METERS = 5;
    
    private final ActivityWithCurrentPosition activity;
    private final MyLocationNewOverlay myLocationOverlay;
    private Runnable permissionGrantedCallback;
    private Runnable permissionDeniedCallback;
    private LocationManager locationManager;

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
        activity.getMapView().getOverlays().add(myLocationOverlay);
    }

    /**
     * Check if the location permission is granted
     *
     * @return true if the location permission is granted
     */
    public boolean isLocationPermissionGranted() {
        final String accessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION;
        final String accessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION;

        return checkSelfPermission(activity, accessCoarseLocation) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(activity, accessFineLocation) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request location permission with an optional callback
     * <h1>Usage</h1>
     * This method should be called in the activity where the permission is requested.
     * The activity must override the {@link Activity#onRequestPermissionsResult(int, String[], int[])} method.
     * In this method,
     * the activity must call {@link CurrentPosition#onRequestPermissionsResult(int, String[], int[])}.
     *
     * @param grantedCallback Runnable to execute when permission is granted
     * @param deniedCallback  Runnable to execute when permission is denied
     * @see CurrentPosition#onRequestPermissionsResult(int, String[], int[])
     */
    public void requestLocationPermission(Runnable grantedCallback, Runnable deniedCallback) {
        this.permissionGrantedCallback = grantedCallback;
        this.permissionDeniedCallback = deniedCallback;
        final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Log.d(TAG, "requestLocationPermission: Requesting GPS Location permission");
        activity.requestPermissions(permissions, REQUEST_POSITION_CODE);
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
        if (requestCode == REQUEST_POSITION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "GPS Location permission granted");
                locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);

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
     * Get the current position
     * <p>
     *     This method returns the current position from the location manager
     *     if the location permission is granted.
     *     <br>
     *     If the method {@link #positionFromManager()} returns null,
     *     the method returns the current position from the location overlay.
     * @return The current position
     * @see #positionFromManager()
     * @see MyLocationNewOverlay#getMyLocation()
     */
    public GeoPoint getCurrentGeoPoint() {
        GeoPoint myLocation = positionFromManager();
        if (myLocation == null) {
            myLocation = myLocationOverlay.getMyLocation();
        }
        Log.d(TAG, "getCurrentGeoPoint: " + myLocation);
        return myLocation;
    }

    /**
     * Get the current position from the location manager
     * <p>
     * This method should be called only if the location permission is granted.
     * <br>
     * If the location permission is not granted, or the location manager is null, this method returns null.
     * </p>
     *
     * @return The current position
     * @see LocationManager#getLastKnownLocation(String)
     */
    @SuppressLint("MissingPermission") // We check the permission with isLocationPermissionGranted
    private GeoPoint positionFromManager() {
        GeoPoint myLocation = null;
        if (isLocationPermissionGranted() && locationManager != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                myLocation = new GeoPoint(location);
            }
        }
        return myLocation;
    }

    /**
     * Follow the user location or not depending on the parameter
     *
     * @param follow true to follow the user location, false otherwise
     * @see MyLocationNewOverlay#enableFollowLocation()
     * @see MyLocationNewOverlay#disableFollowLocation()
     */
    public void followLocation(boolean follow) {
        if (follow) {
            myLocationOverlay.enableFollowLocation();
        } else {
            myLocationOverlay.disableFollowLocation();
        }
    }

    /**
     * Resume the location overlay
     * <p>
     * This method should be called in the activity's {@linkplain Activity#onResume()} method.
     * </p>
     */
    public void onResume() {
        myLocationOverlay.enableMyLocation();
    }

    /**
     * Pause the location overlay
     * <p>
     * This method should be called in the activity's {@link Activity#onPause()} method.
     * </p>
     */
    public void onPause() {
        myLocationOverlay.disableMyLocation();
    }

    /**
     * Start location updates
     * <p>
     * If the location permission is granted, the location updates are started.
     * <br>
     * Every {@value LOCATION_UPDATE_MIN_TIME_MS} milliseconds or every {@value LOCATION_UPDATE_MIN_DISTANCE_METERS} meters, the callback is called with the new location.
     * </p>
     *
     * @param callback The callback to call when the location changes
     * @see LocationCallback
     */
    @SuppressLint("MissingPermission") // We check the permission with isLocationPermissionGranted
    public void startLocationUpdates(LocationCallback callback) {
        Log.d(TAG, "startLocationUpdates: Starting location updates");
        LocationListener locationListener = location -> {
            Log.d(TAG, "startLocationUpdates: Location changed");
            Log.d(TAG, "startLocationUpdates: " + location);
            callback.onLocationChanged(location);
        };

        if (isLocationPermissionGranted()) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_UPDATE_MIN_TIME_MS,
                    LOCATION_UPDATE_MIN_DISTANCE_METERS,
                    locationListener
            );
        }
    }

    /**
     * Describe the callback to call when the location changes.
     */
    @FunctionalInterface
    public interface LocationCallback {
        void onLocationChanged(Location location);
    }
}
