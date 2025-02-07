package fr.iut_rodez.pathpilot_android_client.signup;

import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.login.Login;
import fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class SignUpService implements ISignUpService {
    public static final String EMAIL_KEY = "mail";

    @Override
    public void signUp(SignUpInput signUpInput, Context context)  {
        Log.d(TAG, "API URL: " + LOGIN_URL);

        Popup popup = new Popup(context);
        popup.showProgressDialog("Creating account...");

        RequestQueue requestQueue = getRequestQueue(context);
        JSONObject signUpInputJson = signUpInput.toJson();

        Log.d(TAG, "signUp: " + signUpInputJson);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, signUpInputJson,
                response -> {
                    Log.d(TAG, "onResponse: " + response);
                    popup.dismissProgressDialog();

                    // Button to go to login page
                    DialogButton btnLogin = new DialogButton(context.getString(R.string.sign_in), (dialog, which) -> {
                        dialog.dismiss();

                        Intent intent = new Intent(context, Login.class);
                        intent.putExtra(SignUpService.EMAIL_KEY, signUpInput.mail());

                        context.startActivity(intent);
                    });
                    // Show success dialog with the two buttons
                    popup.showAlertDialog(
                            context.getString(R.string.success),
                            context.getString(R.string.account_created_successfully),
                            btnLogin,
                            DialogButton.okDismiss(context),
                            null
                    );
                },
                error -> {
                    Log.e(TAG, "Error while sending request", error);
                    popup.dismissProgressDialog();
                    VolleyErrorHandler.handleError(context, error);
                }
        );

        requestQueue.add(request);
    }
}
