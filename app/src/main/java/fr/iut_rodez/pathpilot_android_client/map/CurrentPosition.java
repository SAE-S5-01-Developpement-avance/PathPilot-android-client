package fr.iut_rodez.pathpilot_android_client.map;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

public class CurrentPosition {

    private static final String TAG = CurrentPosition.class.getSimpleName();
    public static final int REQUEST_POSITION_CODE = 1;
    private final Activity activity;
    private Location currentLocation;
    private Runnable permissionGrantedCallback;
    private Runnable permissionDeniedCallback;

    public CurrentPosition(Activity activity) {
        this.activity = activity;
    }

    private boolean isLocationPermissionGranted() {
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
     * @param requestCode The code of the request
     * @param permissions The permissions requested
     * @param grantResults The result of the request
     * @see Activity#onRequestPermissionsResult(int, String[], int[])
     * @see CurrentPosition#requestLocationPermission(Runnable, Runnable)
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode == REQUEST_POSITION_CODE) {
            Log.d(TAG, "onRequestPermissionsResult: " + grantResults);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: " + grantResults[0]);
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

    @SuppressLint("MissingPermission") // Permission is checked in isLocationPermissionGranted
    private void updatePosition() {
        if (isLocationPermissionGranted()) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Activity.LOCATION_SERVICE);
            if (locationManager != null) {
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        } else {
            Log.e(TAG, "GPS Permission isn't granted");
        }
    }

    /**
     * Get the current position
     * <p>
     *     If the parameter is true, the position will be updated
     * </p>
     * @param updatePosition If true, update the position
     * @return The current position
     */
    public Location getCurrentPosition(boolean updatePosition) {
        updatePosition();
        return currentLocation;
    }

    /**
     * Get the current position
     * <p>
     *     If the parameter is true, the position will be updated
     * </p>
     * @param updatePosition If true, update the position
     * @return The current position
     */
    public GeoPoint getCurrentGeoPoint(boolean updatePosition) {
        if (updatePosition) {
            updatePosition();
        }
        if (currentLocation != null) {
            return new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        return null;
    }

}
