package mai.maincamgeolocation.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    // Método para verificar se a permissão foi concedida
    public static boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    // Método para solicitar permissões
    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    // Método para verificar se o resultado da permissão foi concedido
    public static boolean isPermissionGranted(int[] grantResults) {
        for (int result : grantResults) {  // Aqui o tipo deve ser int
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
