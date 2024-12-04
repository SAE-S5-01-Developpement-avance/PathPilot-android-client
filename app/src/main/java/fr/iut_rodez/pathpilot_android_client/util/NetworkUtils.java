package fr.iut_rodez.pathpilot_android_client.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle network requests.
 */
public class NetworkUtils {

    private static NetworkUtils instance;
    private final Map<Context, RequestQueue> requestQueues;

    /**
     * Check if the device is connected to the internet.
     *
     * @param context the context of the application
     * @return true if the device is connected to the internet, false otherwise
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (cap == null) return false;
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        } else {
            android.net.Network[] networks = cm.getAllNetworks();
            for (android.net.Network n : networks) {
                NetworkInfo nInfo = cm.getNetworkInfo(n);
                if (nInfo != null && nInfo.isConnected()) return true;
            }
        }

        return false;
    }

    public static NetworkUtils getInstance() {
        if (instance == null) {
            instance = new NetworkUtils();
        }
        return instance;
    }

    private NetworkUtils() {
        requestQueues = new HashMap<>();
    }

    /**
     * Get the RequestQueue for the given context.
     *
     * @param context the context of the application
     * @return the RequestQueue instance
     */
    private RequestQueue getRequestQueueInstance(Context context) {
        if (!requestQueues.containsKey(context)) {
            requestQueues.put(context, Volley.newRequestQueue(context));
        }
        return requestQueues.get(context);
    }

    /**
     * Get the RequestQueue for the given context.
     *
     * @param context the context of the application
     * @return the RequestQueue instance
     */
    public static RequestQueue getRequestQueue(Context context) {
        return getInstance().getRequestQueueInstance(context);
    }

}
