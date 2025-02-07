package fr.iut_rodez.pathpilot_android_client.signup;

import static fr.iut_rodez.pathpilot_android_client.util.network.NetworkUtils.getRequestQueue;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;
import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.login.Login;
import fr.iut_rodez.pathpilot_android_client.util.VolleyErrorHandler;
import fr.iut_rodez.pathpilot_android_client.util.popup.DialogButton;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

public class SignUpService implements ISignUpService {
    public static final String CLE_MAIL = "mail";

    @Override
    public void signUp(SignUpInput signUpInput, Context context)  {
        Log.d(SignUpService.TAG, "API URL: " + SignUpService.LOGIN_URL);

        Popup popup = new Popup(context);

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();

        RequestQueue requestQueue = getRequestQueue(context);
        JSONObject signUpInputJson = signUpInput.toJson();

        Log.d(SignUpService.TAG, "signUp: " + signUpInputJson);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SignUpService.LOGIN_URL, signUpInputJson,
                response -> {
                    Log.d(SignUpService.TAG, "onResponse: " + response);
                    progressDialog.dismiss();

                    // Button to go to login page
                    DialogButton btnLogin = new DialogButton(context.getString(R.string.sign_in), (dialog, which) -> {
                        dialog.dismiss();

                        Intent intent = new Intent(context, Login.class);
                        intent.putExtra(SignUpService.CLE_MAIL, signUpInput.mail());

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
                    Log.e(SignUpService.TAG, "Error while sending request", error);
                    progressDialog.dismiss();
                    VolleyErrorHandler.handleError(context, error);
                }
        );

        requestQueue.add(request);
    }
}
