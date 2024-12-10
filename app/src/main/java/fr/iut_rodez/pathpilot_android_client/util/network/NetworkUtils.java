package fr.iut_rodez.pathpilot_android_client.util.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle network requests.
 */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
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

    /**
     * Create a new JsonObjectRequest with the given parameters.
     * <p>
     * This request is authenticated.
     *
     * @param method          the HTTP method
     * @param url             the URL of the request
     * @param body            the body of the request
     * @param onResponse      a listener when the request is successful
     * @param onErrorResponse a listener when the request fails
     * @param jwtToken        the JWT token to authenticate the request
     * @return the JsonObjectRequest instance
     */
    public static JsonObjectRequest createAuthenticatedRequest(int method, String url, JSONObject body, String jwtToken, Listener<JSONObject> onResponse, ErrorListener onErrorResponse) {

        assert jwtToken != null : "JWT token cannot be null";
        assert !jwtToken.isBlank() : "JWT token cannot be empty";

        Log.d(TAG, "method:" + method + " url:" + url + " body:" + body);

        /*
         * The Volley library does not support DELETE requests with a body.
         * If the method is DELETE, the body will be null.
         * This is a limitation of the Volley library.
         *
         * So we log a warning message to inform the developer.
         */
        if (method == Request.Method.DELETE && body != null) {
            Log.e(TAG, "DELETE request will have an empty body");
        }

        return new JsonObjectRequest(method, url, body, onResponse, onErrorResponse) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + jwtToken);
                return headers;
            }
        };
    }
}