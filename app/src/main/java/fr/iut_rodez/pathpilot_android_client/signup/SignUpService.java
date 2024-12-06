package fr.iut_rodez.pathpilot_android_client.signup;

import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.login.Login;
import fr.iut_rodez.pathpilot_android_client.util.Popup;
import fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler;

public class SignUpService {

    private static final String LOGIN_URL = BuildConfig.API_BASE_URL + "auth/signup";

    private static final String TAG = SignUpService.class.getSimpleName();
    public static final String CLE_MAIL = "mail";

    /**
     * Sign up the user with the given information.
     * <p>
     * If the request is successful, a dialog is shown to the user with two buttons:
     * <ul>
     *     <li>One to dismiss the dialog</li>
     *     <li>One to go to the login page</li>
     * </ul>
     *
     * If the request fails, a dialog is shown to the user with an error message.
     *
     * @param signUpInput the information of the user
     * @param context the context of the SignUp activity
     */
    public static void signUp(SignUpInput signUpInput, Context context) {
        Log.d(TAG, "API URL: " + LOGIN_URL);

        Popup popup = new Popup(context);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        RequestQueue requestQueue = getRequestQueue(context);
        JSONObject signUpInputJson = signUpInput.toJson();

        Log.d(TAG, "signUp: " + signUpInputJson);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, signUpInputJson,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    progressDialog.dismiss();

                    // Button to go to login page
                    Popup.Button btnLogin = new Popup.Button("Login", (dialog, which) -> {
                        dialog.dismiss();

                        Intent intent = new Intent(context, Login.class);
                        intent.putExtra(CLE_MAIL, signUpInput.mail);

                        context.startActivity(intent);
                    });
                    // Button to dismiss the dialog
                    Popup.Button btnOk = new Popup.Button("OK", (dialog, which) -> dialog.dismiss());

                    // Show success dialog with the two buttons
                    popup.showAlertDialog("Success", "Account created successfully", btnLogin, btnOk, null);
                },
                error -> {
                    Log.e(TAG, "Error while sending request", error);
                    progressDialog.dismiss();
                    VolleyErrorHandler.handleError(context, error);
                }
        );

        requestQueue.add(request);
    }

    /**
     * Information of the user to sign up.
     */
    public static class SignUpInput {
        private final String firstName;
        private final String lastName;
        private final double latitude;
        private final double longitude;
        private final String mail;
        private final String password;

        public SignUpInput(String firstName, String lastName, double latitude, double longitude, String mail, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.mail = mail;
            this.password = password;
        }

        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            try {
                json.put("firstName", firstName);
                json.put("lastName", lastName);
                json.put("latitude", latitude);
                json.put("longitude", longitude);
                json.put("email", mail);
                json.put("password", password);
            } catch (JSONException e) {
                Log.e(TAG, "Error while converting SignUpInput to JSON", e);
            }
            return json;
        }
    }
}
