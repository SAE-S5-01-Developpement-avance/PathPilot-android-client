package fr.iut_rodez.pathpilot_android_client.login;

import static fr.iut_rodez.pathpilot_android_client.util.NetworkUtils.getRequestQueue;
import static fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler.handleError;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.home.Home;

/**
 * Service to handle login requests.
 * @author François de Saint Palais
 */
public class LoginService {

    public static final String CLE_TOKEN = "token";
    private static final String LOGIN_URL = BuildConfig.API_BASE_URL + "auth/login";
    private static final String TAG = LoginService.class.getSimpleName();

    private static RequestQueue requestQueue;


    /**
     * Login the user with the given email and password.
     *
     * @param loginInput the email and password of the user
     * @param context    the context of the application
     * @throws IllegalArgumentException if email or password is empty
     */
    public static void login(LoginInput loginInput, Context context) {
        Log.d(TAG, "API URL: " + LOGIN_URL);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        requestQueue = getRequestQueue(context);
        JSONObject loginInputJson = loginInput.toJson();

        Log.d(TAG, "login: " + loginInputJson);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, loginInputJson,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        String token = response.getString("token");
                        int expiresIn = response.getInt("expiresIn");
                        saveAuthToken(token, context); // TODO See if we really need it

                        JWTToken JWTToken = new JWTToken(token, expiresIn);

                        Intent intent = new Intent(context, Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(CLE_TOKEN, JWTToken);

                        context.startActivity(intent);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error while parsing JSON response", e);
                    }
                    progressDialog.dismiss();
                },
                error -> {
                    Log.e(TAG, "Error while logging in", error);
                    progressDialog.dismiss();

                    handleError(context, error);
                }) {
        };

        requestQueue.add(request);
    }

    /**
     * Save the authentication token in the SharedPreferences.
     *
     * @param token   the authentication token
     * @param context the context of the application
     */
    private static void saveAuthToken(String token, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
        Log.d(TAG, "saveAuthToken: Token saved");
        Log.d(TAG, "saveAuthToken: Token: " + token);
    }

    /**
     * Schema for the login input.
     */
    public static class LoginInput {
        private final String email;
        private final String password;

        public LoginInput(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("email", email);
                json.put("password", password);
            } catch (JSONException e) {
                // This should never happen, has the keys are Strings
                Log.e(TAG, "Error while converting LoginInput to JSON", e);
            }
            return json;
        }
    }

    private LoginService() {
        // Private constructor to prevent instantiation
    }
}
