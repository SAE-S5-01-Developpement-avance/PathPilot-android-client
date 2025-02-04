package fr.iut_rodez.pathpilot_android_client.util.map;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.home.clients.Client;
import fr.iut_rodez.pathpilot_android_client.map.ActivityWithCurrentPosition;

/**
 * Helper class to add markers on a map.
 */
public class MapMarker {

    private ActivityWithCurrentPosition activity;
    private MapView mapView;

    /**
     * Create a new MapMarker object
     * <p>
     * The activity must have a MapView. If it doesn't, an IllegalArgumentException is thrown.
     * </p>
     *
     * @param activity The activity
     * @throws IllegalArgumentException if the activity doesn't have a MapView or the MapView isn't
     * @see ActivityWithCurrentPosition
     */
    public MapMarker(ActivityWithCurrentPosition activity) {
        this.activity = activity;
        MapView activityMapView = activity.getMapView();
        if (activityMapView == null) {
            throw new IllegalArgumentException("The activity must have a MapView");
        }
        this.mapView = activityMapView;
    }

    public void addMarker(Client client, MarkerType markerType) {
        addMarker(client.getCompanyName(), client.getAddressDisplayName(), client.getGeoPoint(), markerType);
    }

    /**
     * Add a marker on the map.
     * <p>
     * The title and the description are displayed when the marker is clicked.
     * <br>
     * The markerType is used to set the icon of the marker. {@link MarkerType}
     * </p>
     *
     * @param title       the title of the marker
     * @param description the description of the marker
     * @param position    the position of the marker
     * @param markerType  the type of the marker
     * @see MarkerType
     */
    public void addMarker(String title, String description, GeoPoint position, MarkerType markerType) {
        Marker marker = new Marker(mapView);
        marker.setTitle(title);
        marker.setSnippet(description);
        marker.setPosition(position);
        marker.setIcon(markerType.getDrawable(activity));
        mapView.getOverlays().add(marker);
    }

    public void removeAllMarkers() {
        mapView.getOverlays().clear();
    }

    /**
     * Define the custom map marker icon.
     */
    public enum MarkerType {
        CLIENT_IGNORED(R.drawable.marker_client_ignored),
        CLIENT_VISITED(R.drawable.marker_client_visited),
        NEXT_CLIENT(R.drawable.marker_next_client),
        EXPECTED_CLIENT(R.drawable.marker_expected_client),
        ;

        private final int drawableId;

        MarkerType(int drawableId) {
            this.drawableId = drawableId;
        }

        /**
         * Get the drawable of the marker type.
         *
         * @param context The context to get the drawable
         * @return The drawable of the marker type
         */
        public Drawable getDrawable(Context context) {
            return AppCompatResources.getDrawable(context, drawableId);
        }
    }
}
