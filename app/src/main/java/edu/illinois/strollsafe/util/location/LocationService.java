package edu.illinois.strollsafe.util.location;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.Locale;

import edu.illinois.strollsafe.GlobalConfig;
import edu.illinois.strollsafe.MainActivity;
import edu.illinois.strollsafe.util.GeneralSingletons;

/**
 * @author MichaelGoldstein
 */
public class LocationService {

    public static Location getLocationFast(Context context) {
        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locManager.getLastKnownLocation(locManager.getBestProvider(GeneralSingletons.LOOSE_CRITERIA, false));
    }


    public static String getZipFromLocation(Context context, Location location) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        if (location == null)
            return null;

        return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getPostalCode();
    }

    public static boolean isLocationSupported(String zip) {
        for (String s : GlobalConfig.SUPPORTED_ZIP_CODES) {
            if (zip.equals(s))
                return true;
        }

        return false;
    }

    public static void testLocationSupported(final MainActivity context) throws IOException {
        String zip = getZipFromLocation(context, getLocationFast(context));
        if (zip == null || !isLocationSupported(zip)) {
            LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            locManager.requestSingleUpdate(locManager.getBestProvider(GeneralSingletons.LOOSE_CRITERIA, false), new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try {
                        if (!isLocationSupported(getZipFromLocation(context, location))) {
                            context.showUnsupportedLocationDialog();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);
        }
    }
}
