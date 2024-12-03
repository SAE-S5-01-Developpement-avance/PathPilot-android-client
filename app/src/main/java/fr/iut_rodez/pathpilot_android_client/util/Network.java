package fr.iut_rodez.pathpilot_android_client.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle network requests.
 */
public class Network {

    private static Network instance;
    private final Map<Context, RequestQueue> requestQueues;

    /**
     * Check if the device is connected to the internet.
     *
     * @param context the context of the application
     * @return true if the device is connected to the internet, false otherwise
     */
    public static boolean isNetworkConnected(Context context) {
        return true; //STUB
    }

    public static Network getInstance() {
        if (instance == null) {
            instance = new Network();
        }
        return instance;
    }

    private Network() {
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
