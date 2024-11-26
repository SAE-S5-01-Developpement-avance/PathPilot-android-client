package fr.iut_rodez.pathpilot_android_client.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.util.Popup;
import fr.iut_rodez.pathpilot_android_client.util.ValidateForm;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();

    private Popup popup;

    private Button loginButton;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Get xml elements
        loginButton = findViewById(R.id.login_btn);
        emailInput = findViewById(R.id.mail);
        passwordInput = findViewById(R.id.password);

        // Set onClickListener
        loginButton.setOnClickListener(v -> login());

        // Initialize popup
        popup = new Popup(this);
    }

    public void login() {
        // Get email and password
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Check if email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {
            popup.showAlertDialog("Error", getResources().getString(R.string.error_email_password_field_empty));

        } else if (!ValidateForm.isEmailValid(email)) { // Check if email is valid
            popup.showAlertDialog("Error", getResources().getString(R.string.error_email_invalid));

        } else {

            // Send request to server
            boolean loginSuccess = LoginService.login(email, password);

            if (loginSuccess) {
                //TODO : Start launch Home activity
                popup.showToastLong("Login success"); //TODO remove the Toast
            } else {
                // Notify user
                //TODO Make a better msg
                popup.showToastLong("Login failed");
            }
        }
    }
}
