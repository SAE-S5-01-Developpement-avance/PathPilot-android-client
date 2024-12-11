package fr.iut_rodez.pathpilot_android_client.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.nio.charset.StandardCharsets;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils;

/**
 * Utility class to handle Volley errors and provide user-friendly error messages
 */
public class VolleyErrorHandler {
    private static final String TAG = "VolleyErrorHandler";

    /**
     * Handles Volley errors and provides user-friendly error messages
     *
     * @param context Application context
     * @param error   Volley error to handle
     * @return User-friendly error message
     */
    public static String handleError(Context context, VolleyError error) {
        String errorMessage = context.getString(R.string.unknown_error_occurred);

        // Check network connectivity first
        if (!NetworkUtils.isNetworkConnected(context)) {
            return context.getString(R.string.no_internet_connection);
        }

        // Detailed error handling
        if (error instanceof NetworkError) {
            errorMessage = context.getString(R.string.network_error_unable_to_connect_to_the_server);
        } else if (error instanceof ServerError) {
            errorMessage = handleServerError(context, error);
        } else if (error instanceof AuthFailureError) {
            errorMessage = context.getString(R.string.authentication_failed_give_valid_credentials);
        } else if (error instanceof ParseError) {
            errorMessage = context.getString(R.string.unable_to_process_server_response);
        } else if (error instanceof TimeoutError) {
            errorMessage = context.getString(R.string.connection_timed_out);
        }

        // Log detailed error information
        logVolleyError(error);

        // Show toast notification
        Popup popup = new Popup(context);
        popup.showAlertDialog(context.getString(R.string.error), errorMessage);

        return errorMessage;
    }

    /**
     * Handles server-specific error messages
     *
     * @param error Volley error
     * @return Specific server error message
     */
    private static String handleServerError(Context context, VolleyError error) {
        if (error.networkResponse != null) {
            switch (error.networkResponse.statusCode) {
                case 400:
                    return context.getString(R.string.bad_request);
                case 401:
                    return context.getString(R.string.unauthorized);
                case 403:
                    return context.getString(R.string.forbidden_dont_have_permision_to_access);
                case 404:
                    return context.getString(R.string.requested_resource_not_found);
                case 500:
                    return context.getString(R.string.internal_server_error);
                case 503:
                    return context.getString(R.string.service_unavailable);
                default:
                    return context.getString(R.string.unknow_server_error, error.networkResponse.statusCode);
            }
        }
        return "Unknown server error occurred.";
    }

    /**
     * Logs detailed Volley error information
     *
     * @param error Volley error to log
     */
    private static void logVolleyError(VolleyError error) {
        Log.e(TAG, "Volley Error Details:");

        // Log error type
        Log.e(TAG, "Error Type: " + error.getClass().getSimpleName());

        // Log network response details if available
        if (error.networkResponse != null) {
            Log.e(TAG, "Status Code: " + error.networkResponse.statusCode);

            // Try to parse and log response body
            try {
                String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                Log.e(TAG, "Response Body: " + responseBody);
            } catch (Exception e) {
                Log.e(TAG, "Could not parse error response body");
            }
        }

        // Log stack trace
        if (error.getCause() != null) {
            Log.e(TAG, "Error Cause: " + error.getCause().getMessage());
        }
    }

    private VolleyErrorHandler() {
        // Prevent instantiation
    }
}