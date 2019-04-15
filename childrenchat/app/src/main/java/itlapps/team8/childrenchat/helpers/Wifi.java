package itlapps.team8.childrenchat.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Wifi {

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo  = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null  && networkInfo.isConnected()) {
            try {
                //Generamos un ping a los servidores de Google
                Process process  = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
                int val = process.waitFor();
                boolean reachable = (val == 0);

                return reachable;
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return false;
        }

        return false;
    }

}
