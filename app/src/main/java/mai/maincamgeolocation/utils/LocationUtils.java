package mai.maincamgeolocation.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtils {

    public static Location getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
}

