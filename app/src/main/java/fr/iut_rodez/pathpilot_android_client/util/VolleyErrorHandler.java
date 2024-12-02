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
        String errorMessage = "Unknown error occurred";

        // Check network connectivity first
        if (!Network.isNetworkConnected(context)) {
            return "No internet connection. Please check your network settings.";
        }

        // Detailed error handling
        errorMessage = switch (error) {
            case NetworkError networkError -> "Network error. Unable to connect to the server.";
            case ServerError serverError -> handleServerError(error);
            case AuthFailureError authFailureError -> "Authentication failed. Please give valid credentials.";
            case ParseError parseError -> "Unable to process server response.";
            case TimeoutError timeoutError -> "Connection timed out. Please try again.";
            default -> "Unknown error occurred";
        };

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
    private static String handleServerError(VolleyError error) {
        if (error.networkResponse != null) {
            return switch (error.networkResponse.statusCode) {
                case 400 -> "Bad Request. Please check your input.";
                case 401 -> "Unauthorized. Please log in again.";
                case 403 -> "Forbidden. You don't have permission to access this resource.";
                case 404 -> "Requested resource not found.";
                case 500 -> "Internal server error. Please try again later.";
                case 503 -> "Service unavailable. Server is temporarily overloaded.";
                default -> "Server error. Status code: " + error.networkResponse.statusCode;
            };
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