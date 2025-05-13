package fr.iut_rodez.pathpilot_android_client.util.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import fr.iut_rodez.pathpilot_android_client.R;

public class LocationNameProvider {

    /**
     * Get the name of the address at the given GeoPoint.
     * <p>
     * If the address is not found, the string resource {@code R.string.client_address_not_found } is returned.
     * </p>
     *
     * @param context  the context. Used to get the string resources and the Geocoder.
     * @param geoPoint the GeoPoint to get the address name from.
     * @return the name of the address at the given GeoPoint.
     */
    @NonNull
    public static String getAddressName(@NonNull Context context, @NonNull GeoPoint geoPoint) {
        String placeName = context.getString(R.string.client_address_not_found);
        List<Address> addresses = getAddresses(context, geoPoint);
        if (!addresses.isEmpty()) {
            placeName = addresses.get(0).getAddressLine(0);
        }
        return placeName;
    }

    @NonNull
    public static List<Address> getAddresses(@NonNull Context context, @NonNull GeoPoint geoPoint) {
        Geocoder geocoderAddress = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = Collections.emptyList();
        try {
            var addressesFound = geocoderAddress.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
            if (addressesFound != null && !addressesFound.isEmpty()) {
                addresses = addressesFound;
            }
        } catch (IOException e) {
            // Do nothing
        }
        return addresses;
    }

    private LocationNameProvider() {}
}
