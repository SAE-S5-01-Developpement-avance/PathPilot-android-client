package fr.iut_rodez.pathpilot_android_client.login;

import static fr.iut_rodez.pathpilot_android_client.signup.SignUpService.CLE_MAIL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.signup.SignUp;
import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.util.NetworkUtils;
import fr.iut_rodez.pathpilot_android_client.util.Popup;
import fr.iut_rodez.pathpilot_android_client.util.ValidateForm;

/**
 * Handle the login Activity
 * @author François de Saint Palais
 */
public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();

    private Popup popup;

    private Button loginButton;
    private EditText emailInput;
    private EditText passwordInput;
    private TextView linkSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Get xml elements
        loginButton = findViewById(R.id.login_btn);
        emailInput = findViewById(R.id.mail);
        passwordInput = findViewById(R.id.password);
        linkSignUp = findViewById(R.id.link_sign_up);

        // Set onClickListener
        loginButton.setOnClickListener(v -> login());
        linkSignUp.setOnClickListener(v -> gotoSignUp());

        // Initialize popup
        popup = new Popup(this);

        // If the Activity was started by the SignUp Activity, get the email from the intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CLE_MAIL)) {
            emailInput.setText(intent.getStringExtra(CLE_MAIL));
        }
    }

    /**
     * Switch to SignUp activity
     */
    private void gotoSignUp() {
        Log.d(TAG, "Switch to SignUp activity");
        Intent intent = new Intent(this, SignUp.class);

        // The user can't go back to the login activity by pressing the back button
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }

    /**
     * Login the user
     */
    public void login() {
        // Get email and password
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Check if email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {

            popup.showAlertDialog(getString(R.string.error), getResources().getString(R.string.error_email_password_field_empty));
        } else if (!ValidateForm.isEmailValid(email)) { // Check if email is valid

            popup.showAlertDialog(getString(R.string.error), getResources().getString(R.string.error_email_invalid));
        } else if (!NetworkUtils.isNetworkConnected(this)) { // Check if the device is connected to the internet

            popup.showAlertDialog(getString(R.string.error), getResources().getString(R.string.error_no_internet));
        } else {

            // Send request to server
            LoginService.login(new LoginService.LoginInput(email, password), this);
        }
    }
}
