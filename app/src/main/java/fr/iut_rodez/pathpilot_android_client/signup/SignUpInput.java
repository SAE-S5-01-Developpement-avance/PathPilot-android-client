package fr.iut_rodez.pathpilot_android_client.signup;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Information of the user to sign up.
 */
public record SignUpInput(String firstName, String lastName, double latitude, double longitude,
                          String mail, String password) {

    private static final String TAG = SignUpInput.class.getSimpleName();

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
