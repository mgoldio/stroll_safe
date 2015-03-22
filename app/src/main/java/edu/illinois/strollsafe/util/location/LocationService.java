package edu.illinois.strollsafe.util.location;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.util.Locale;

import edu.illinois.strollsafe.GlobalConfig;
import edu.illinois.strollsafe.util.GeneralSingletons;

/**
 * @author MichaelGoldstein
 */
public class LocationService {

    public static String getZipCode(Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locManager.getLastKnownLocation(locManager.getBestProvider(GeneralSingletons.LOOSE_CRITERIA, false));
        System.out.println(location);
        if (location == null)
            return null;

        return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getPostalCode();
    }

    public static boolean isCurrentLocationSupported(Context context) throws IOException {
        String zip = getZipCode(context);
        if (zip == null)
            return false;

        for (String s : GlobalConfig.SUPPORTED_ZIP_CODES) {
            if (zip.equals(s))
                return true;
        }
        return false;
    }
}
