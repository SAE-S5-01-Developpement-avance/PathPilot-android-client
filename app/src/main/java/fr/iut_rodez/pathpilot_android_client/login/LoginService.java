package fr.iut_rodez.pathpilot_android_client.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;

public class LoginService {

    private static final String LOGIN_URL = BuildConfig.API_BASE_URL + "auth/login";
    private static RequestQueue requestQueue;

    private static final String TAG = LoginService.class.getSimpleName();

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

        requestQueue = Volley.newRequestQueue(context);
        JSONObject loginInputJson = loginInput.toJson();

        Log.d(TAG, "login: " + loginInputJson);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, loginInputJson,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    try {
                        String token = response.getString("token");
                        saveAuthToken(token, context);
                        //TODO Goto main Page
                    } catch (JSONException e) {
                        Log.e(TAG, "Error while parsing JSON response", e);
                    }
                    progressDialog.dismiss();
                },
                error -> {
                    Log.e(TAG, "Error while logging in", error);
                    progressDialog.dismiss();
                    // Handle error
                }) {
        };

        requestQueue.add(request);
    }

    private static void saveAuthToken(String token, Context context) {
        // Utilisez SharedPreferences avec chiffrement ou un gestionnaire de tokens sécurisé
        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
        Log.d(TAG, "saveAuthToken: Token saved");
        Log.d(TAG, "saveAuthToken: Token: " + token);
    }

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

    public static class LoginResponse {
        private final String token;
        /**
         *
         */
        private final long expiresIn;

        public LoginResponse(String token, int expiresIn) {
            this.token = token;
            this.expiresIn = expiresIn;
        }

        public String getToken() {
            return token;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public Date getExpirationDate() {
            return new Date(System.currentTimeMillis() + expiresIn);
        }
    }
}
