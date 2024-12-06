package fr.iut_rodez.pathpilot_android_client.signup;

import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isEmailValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isFirstNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLastNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLatitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLongitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isPasswordValid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.login.Login;
import fr.iut_rodez.pathpilot_android_client.signup.SignUpService.SignUpInput;
import fr.iut_rodez.pathpilot_android_client.util.Popup;
import fr.iut_rodez.pathpilot_android_client.util.ValidateForm;

/**
 * Handle the sign up Activity
 */
public class SignUp extends AppCompatActivity {
    private static final String TAG = SignUp.class.getSimpleName();
    private EditText firstName;
    private EditText lastName;
    private EditText latitude;
    private EditText longitude;
    private EditText mail;
    private EditText password;
    private EditText confirmPassord;
    private TextView labelFirstName;
    private TextView labelLastName;
    private TextView labelLatitude;
    private TextView labelLongitude;
    private TextView labelMail;
    private TextView labelPassword;
    private TextView labelConfirmPassword;

    private Popup popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_sign_up);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        confirmPassord = findViewById(R.id.confirm_password);

        labelFirstName = findViewById(R.id.label_first_name);
        labelLastName = findViewById(R.id.label_last_name);
        labelLatitude = findViewById(R.id.label_position_lat);
        labelLongitude = findViewById(R.id.label_position_long);
        labelMail = findViewById(R.id.label_mail);
        labelPassword = findViewById(R.id.label_password);
        labelConfirmPassword = findViewById(R.id.label_confirm_password);

        findViewById(R.id.sign_up_button).setOnClickListener(v -> createAccount());
        findViewById(R.id.link_sign_in).setOnClickListener(v -> gotoSignIn());

        popup = new Popup(this);
    }

    /**
     * Checks that the parameters entered are valid.
     * Creates an account or notifies the user of input errors.
     */
    public void createAccount() {
        StringBuilder errorMessage = new StringBuilder();
        // reset the style of the text field
        resetFieldStyle();

        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String latitudeText = latitude.getText().toString();
        String longitudeText = longitude.getText().toString();
        String mailText = mail.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassord.getText().toString();

        errorMessage.append(checkFirstName(firstNameText));
        errorMessage.append(checkLastName(lastNameText));
        errorMessage.append(checkLatitude(latitudeText));
        errorMessage.append(checkLongitude(longitudeText));
        errorMessage.append(checkMail(mailText));
        errorMessage.append(checkPassword(passwordText));
        errorMessage.append(checkConfirmPassword(passwordText, confirmPasswordText));

        if (errorMessage.length() != 0) {
            popup.showToastLong(errorMessage.toString());
        } else {
            double latitudeValue = Double.parseDouble(latitudeText);
            double longitudeValue = Double.parseDouble(longitudeText);
            sendInformationToSignInUser(firstNameText, lastNameText, latitudeValue, longitudeValue, mailText, passwordText);
        }
    }

    /**
     * Check the first name field.
     * @param  firstNameText
     * @return errorMessage
     */
    public String checkFirstName(String firstNameText) {
        String errorMessage = "";

        if (!isFirstNameValid(firstNameText)) {
            labelFirstName.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.first_name_blank);
        }
        return errorMessage;
    }

    /**
     * Check the last name field.
     * @param lastNameText
     * @return errorMessage
     */
    public String checkLastName(String lastNameText) {
        String errorMessage = "";

        if (!isLastNameValid(lastNameText)) {
            labelLastName.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.last_name_blank);
        }
        return errorMessage;
    }

    /**
     * Check the latitude field.
     * @param latitudeText
     * @return errorMessage
     */
    public String checkLatitude(String latitudeText) {
        double latitudeValue = Double.NaN;
        String errorMessage = "";

        try {
            latitudeValue = Double.parseDouble(latitudeText);
        } catch (Exception e) {
            labelLatitude.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.latitude_not_float);
        }

        if (!isLatitudeValid(latitudeValue)) {
            labelLatitude.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.latitude_not_included);
        }

        return errorMessage;
    }

    /**
     * Check the longitude field.
     * @param longitudeText
     * @return errorMessage
     */
    public String checkLongitude(String longitudeText) {
        double longitudeValue = Double.NaN;
        String errorMessage = "";

        try {
            longitudeValue = Double.parseDouble(longitudeText);
        } catch (Exception e) {
            labelLongitude.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.longitude_not_float);
        }

        if (!isLongitudeValid(longitudeValue)) {
            labelLongitude.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.longitude_not_included);
        }

        return errorMessage;
    }

    /**
     * Check the mail field.
     * @param  mailText
     * @return errorMessage
     */
    public String checkMail(String mailText) {
        String errorMessage = "";

        if (!isEmailValid(mailText)) {
            labelMail.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.error_email_invalid);
        }

        return errorMessage;
    }

    /**
     * Check the password field.
     * @param passwordText
     * @return errorMessage
     */
    public String checkPassword(String passwordText) {
        String errorMessage = "";

        if (passwordText.isBlank()) {
            labelPassword.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.password_blank);
        } else if (!isPasswordValid(passwordText)) {
            labelPassword.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.password_min_size_error);
        }

        return errorMessage;
    }

    /**
     * Check that the password is confirmed.
     * @param password
     * @param confirmPasswordText
     * @return errorMessage
     */
    public String checkConfirmPassword(String password, String confirmPasswordText) {
        String errorMessage = "";

        if (confirmPasswordText.isBlank()) {
            labelConfirmPassword.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.confirm_password_blank);
        } else if (!confirmPasswordText.equals(password)) {
            labelConfirmPassword.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.confirm_password_not_same);
        }

        return errorMessage;
    }


    /**
     * Reset the style of sign up interface.
     */
    public void resetFieldStyle() {
        labelFirstName.setTextColor(getColor(R.color.black));
        labelLastName.setTextColor(getColor(R.color.black));
        labelLatitude.setTextColor(getColor(R.color.black));
        labelLongitude.setTextColor(getColor(R.color.black));
        labelMail.setTextColor(getColor(R.color.black));
        labelPassword.setTextColor(getColor(R.color.black));
    }

    /**
     * Send information to the API for sign in the user with the entered informations.
     */
    public void sendInformationToSignInUser(String firstNameText, String lastNameText,
                                            double latitudeText, double longitudeText,
                                            String mailText, String passwordText) {

        SignUpInput signUpInput = new SignUpInput(
                firstNameText,
                lastNameText,
                latitudeText,
                longitudeText,
                mailText,
                passwordText
        );

        SignUpService.signUp(signUpInput, this);
    }

    /**
     * Sends to the sign in interface when the sign in Textview is clicked.
     */
    public void gotoSignIn() {
        Log.d(TAG, "Switch to Login activity");
        Intent intent = new Intent(this, Login.class);

        // The user can't go back to the login activity by pressing the back button
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
    }
}