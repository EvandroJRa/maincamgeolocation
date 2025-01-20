package mai.maincamgeolocation.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtils {

    public static Location getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            try {
                return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        return null; // Retorna null se não for possível obter a localização
    }
}


