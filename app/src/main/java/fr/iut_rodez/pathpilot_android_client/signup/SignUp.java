package fr.iut_rodez.pathpilot_android_client.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.login.Login;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

public class SignUp extends AppCompatActivity {

    private static final String TAG = SignUp.class.getSimpleName();
    private static final int PASSWORD_MIN_SIZE = 8;
    private static final String REGEX_MAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private EditText firstName;
    private EditText lastName;
    private EditText latitude;
    private EditText longitude;
    private EditText mail;
    private EditText password;
    private TextView labelFirstName;
    private TextView labelLastName;
    private TextView labelLatitude;
    private TextView labelLongitude;
    private TextView labelMail;
    private TextView labelPassword;

    private Popup popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.sign_up);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);

        labelFirstName = findViewById(R.id.label_first_name);
        labelLastName = findViewById(R.id.label_last_name);
        labelLatitude = findViewById(R.id.label_position_lat);
        labelLongitude = findViewById(R.id.label_position_long);
        labelMail = findViewById(R.id.label_mail);
        labelPassword = findViewById(R.id.label_password);

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

        errorMessage.append(checkFirstName(firstNameText));
        errorMessage.append(checkLastName(lastNameText));
        errorMessage.append(checkLatitude(latitudeText));
        errorMessage.append(checkLongitude(longitudeText));
        errorMessage.append(checkMail(mailText));
        errorMessage.append(checkPassword(passwordText));

        if (errorMessage.length() != 0) {
            popup.showToastLong(errorMessage.toString());
        } else {
            sendInformationToSignInUser(firstNameText, lastNameText, latitudeText, longitudeText, mailText, passwordText);
        }
    }

    /**
     * Check the first name field.
     *
     * @return errorMessage
     */
    public String checkFirstName(String firstNameText) {

        String errorMessage = "";
        if (firstNameText.isBlank()) {
            labelFirstName.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.first_name_blank);
        }
        return errorMessage;
    }

    /**
     * Check the last name field.
     *
     * @return errorMessage
     */
    public String checkLastName(String lastNameText) {
        String errorMessage = "";
        if (lastNameText.isBlank()) {
            labelLastName.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.last_name_blank);
        }
        return errorMessage;
    }

    /**
     * Check the latitude field.
     *
     * @return errorMessage
     */
    public String checkLatitude(String latitudeText) {
        double latitudeValue = 0;
        String errorMessage = "";
        try {
            latitudeValue = Double.parseDouble(latitudeText);
            if (latitudeText.isBlank()) {
                labelLatitude.setTextColor(getColor(R.color.red));
                errorMessage += getString(R.string.latitude_blank);
            } else if (latitudeValue >= 90 || latitudeValue <= -90) {
                labelLatitude.setTextColor(getColor(R.color.red));
                errorMessage += getString(R.string.latitude_not_included);
            }
        } catch (Exception e) {
            labelLatitude.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.latitude_not_float);
        }
        return errorMessage;
    }

    /**
     * Check the longitude field.
     *
     * @return errorMessage
     */
    public String checkLongitude(String longitudeText) {
        double longitudeValue = 0;
        String errorMessage = "";
        try {
            longitudeValue = Double.parseDouble(longitudeText);
            if (longitudeText.isBlank()) {
                labelLongitude.setTextColor(getColor(R.color.red));
                errorMessage += getString(R.string.longitude_blank);
            } else if (longitudeValue >= 180 || longitudeValue <= -180) {
                labelLongitude.setTextColor(getColor(R.color.red));
                errorMessage += getString(R.string.longitude_not_included);
            }
        } catch (Exception e) {
            labelLongitude.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.longitude_not_float);
        }
        return errorMessage;
    }

    /**
     * Check the mail field.
     *
     * @return errorMessage
     */
    public String checkMail(String mailText) {
        String errorMessage = "";
        if (mailText.isBlank()) {
            labelMail.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.mail_blank);
        } else if (!mailText.matches(REGEX_MAIL)) {
            labelMail.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.mail_regex_invalided);
        }
        return errorMessage;
    }

    /**
     * Check the password field.
     *
     * @return errorMessage
     */
    public String checkPassword(String passwordText) {
        String errorMessage = "";
        if (passwordText.isBlank()) {
            labelPassword.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.password_blank);
        } else if (passwordText.length() < PASSWORD_MIN_SIZE) {
            labelPassword.setTextColor(getColor(R.color.red));
            errorMessage += getString(R.string.password_min_size_error);
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
                                            String latitudeText, String longitudeText,
                                            String mailText, String passwordText) {

        //TODO connect the API and send informations
        Log.i("FIRSTNAME", firstNameText);
        Log.i("LASTNAME", lastNameText);
        Log.i("LATITUDE", Double.parseDouble(latitudeText) + "");
        Log.i("LONGITUDE", Double.parseDouble(longitudeText) + "");
        Log.i("MAIL", mailText);
        Log.i("PASSWORD", passwordText);

        //TODO if API return an error, send an error message to the user returned by the API
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