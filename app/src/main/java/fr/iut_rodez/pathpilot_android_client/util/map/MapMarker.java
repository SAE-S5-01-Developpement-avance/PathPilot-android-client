package fr.iut_rodez.pathpilot_android_client.util.map;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
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
     * Add a marker on the map
     * <p>
     * The marker is added with the given position, title and description
     * <br>
     * The title and description are displayed when the user click on the marker
     * <br>
     * The marker icon is the given drawable. If the drawable is null, the default marker icon is used.
     * </p>
     *
     * @param title       the title of the marker
     * @param description the description of the marker
     * @param position    the position of the marker
     * @param drawable    the drawable of the marker. If null, the default marker icon is used.
     */
    public void addMarker(@NonNull String title, @NonNull String description, @NonNull GeoPoint position, Drawable drawable) {
        Marker marker = new Marker(mapView);
        marker.setTitle(title);
        marker.setSnippet(description);
        marker.setPosition(position);
        if (drawable != null) {
            marker.setIcon(drawable);
        }
        mapView.getOverlays().add(marker);
    }

    /**
     * Add a marker on the map
     * <p>
     * The marker is added with the given position, title and description
     * <br>
     * The title and description are displayed when the user click on the marker
     * </p>
     *
     * @param title       the title of the marker
     * @param description the description of the marker
     * @param position    the position of the marker
     * @param markerType  the type of the marker
     * @see MarkerType
     */
    public void addMarker(@NonNull String title,@NonNull String description,@NonNull GeoPoint position,@NonNull MarkerType markerType) {
        Marker marker = new Marker(mapView);
        marker.setTitle(title);
        marker.setSnippet(description);
        marker.setPosition(position);
        marker.setIcon(markerType.getDrawable(activity));
        marker.setAnchor(markerType.anchor.horizontal, markerType.anchor.vertical);
        mapView.getOverlays().add(marker);
    }

    /**
     * Add a marker on the map with the given icon id
     *
     * @see #addMarker(String, String, GeoPoint, Drawable)
     */
    public void addMarker(@NonNull String title,@NonNull String description,@NonNull GeoPoint position,@DrawableRes int iconId) {
        addMarker(title, description, position, AppCompatResources.getDrawable(activity, iconId));
    }

    public void removeAllMarkers() {
        mapView.getOverlays().clear();
    }

    /**
     * Define the custom map marker icon.
     */
    public enum MarkerType {
        CLIENT_IGNORED(R.drawable.marker_client_ignored, MarkerAnchor.TOP_CENTER),
        CLIENT_VISITED(R.drawable.marker_client_visited, MarkerAnchor.TOP_CENTER),
        NEXT_CLIENT(R.drawable.marker_next_client, MarkerAnchor.BOTTOM_CENTER),
        EXPECTED_CLIENT(R.drawable.marker_expected_client, MarkerAnchor.BOTTOM_CENTER),
        ;

        private final int drawableId;
        private final MarkerAnchor anchor;

        MarkerType(int drawableId, MarkerAnchor anchor) {
            this.drawableId = drawableId;
            this.anchor = anchor;
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

        private record MarkerAnchor(float horizontal, float vertical) {
            public static final MarkerAnchor CENTER = new MarkerAnchor(0.5f, 0.5f);
            public static final MarkerAnchor BOTTOM_CENTER = new MarkerAnchor(0.5f, 1f);
            public static final MarkerAnchor TOP_CENTER = new MarkerAnchor(0.5f, 0f);
        }
    }
}
