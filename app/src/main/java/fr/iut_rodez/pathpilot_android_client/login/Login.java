package fr.iut_rodez.pathpilot_android_client.login;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.util.ValidateForm;

public class Login extends AppCompatActivity {

    private static final String TAG = Login.class.getSimpleName();

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
    }

    public void login() {
        // Get email and password
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        // Check if email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {
            // Notify user
            //TODO : Add notification
            return;
        }

        // Check if email is valid
        if (ValidateForm.isEmailValid(email)) {
            // Notify user
            //TODO : Add notification
            return;
        }

        // Send request to server
        Log.d(TAG, "login: " + email + " " + password);
        //TODO add request to server
    }
}
