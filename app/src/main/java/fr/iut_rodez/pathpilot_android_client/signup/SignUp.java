package fr.iut_rodez.pathpilot_android_client.signup;

import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isEmailValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isFirstNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLastNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLatitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLongitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isPasswordValid;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.login.Login;
import fr.iut_rodez.pathpilot_android_client.map.MapSelection;
import fr.iut_rodez.pathpilot_android_client.signup.SignUpService.SignUpInput;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

/**
 * Handle the sign up Activity
 */
public class SignUp extends AppCompatActivity {
    private static final String TAG = SignUp.class.getSimpleName();
    private EditText firstName;
    private EditText lastName;
    private EditText mail;
    private EditText password;
    private EditText confirmPassord;
    private TextView address;
    private TextView labelAddress;
    private TextView labelFirstName;
    private TextView labelLastName;
    private TextView labelMail;
    private TextView labelPassword;
    private TextView labelConfirmPassword;
    private double latitude = Double.NaN;
    private double longitude = Double.NaN;

    private Popup popup;

    private ActivityResultLauncher<Intent> launcherMapSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_sign_up);

        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        address = findViewById(R.id.address);
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        confirmPassord = findViewById(R.id.confirm_password);

        labelFirstName = findViewById(R.id.label_first_name);
        labelLastName = findViewById(R.id.label_last_name);
        labelAddress = findViewById(R.id.label_address);
        labelMail = findViewById(R.id.label_mail);
        labelPassword = findViewById(R.id.label_password);
        labelConfirmPassword = findViewById(R.id.label_confirm_password);

        findViewById(R.id.sign_up_button).setOnClickListener(v -> createAccount());
        findViewById(R.id.link_sign_in).setOnClickListener(v -> gotoSignIn());
        findViewById(R.id.selection_map_button).setOnClickListener(v -> gotoSelectionMap());

        popup = new Popup(this);

        launcherMapSelection = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleReturnedMapSelection);
    }

    private void handleReturnedMapSelection(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                latitude = data.getDoubleExtra(MapSelection.KEY_LATITUDE, Double.NaN);
                longitude = data.getDoubleExtra(MapSelection.KEY_LONGITUDE, Double.NaN);

                String placeName = "";
                Geocoder geocoderAddress = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoderAddress.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        placeName = address.getAddressLine(0);
                    } else {
                        placeName += getString(R.string.client_address_not_found);
                    }
                } catch (IOException e) {
                    placeName = getString(R.string.client_address_not_found);
                }
                address.setText(placeName);
            }
        }
    }

    private void gotoSelectionMap() {
        Intent intent = new Intent(this, MapSelection.class);
        if (!Double.isNaN(latitude) && !Double.isNaN(longitude)) {
            intent.putExtra(MapSelection.KEY_LATITUDE, latitude);
            intent.putExtra(MapSelection.KEY_LONGITUDE, longitude);
        }
        launcherMapSelection.launch(intent);
    }

    /**
     * Checks that the parameters entered are valid.
     * Creates an account or notifies the user of input errors.
     */
    public void createAccount() {
        ArrayList<String> errorMessage = new ArrayList<>();
        // reset the style of the text field
        resetFieldStyle();

        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String mailText = mail.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPasswordText = confirmPassord.getText().toString();

        errorMessage.add(checkFirstName(firstNameText));
        errorMessage.add(checkLastName(lastNameText));
        errorMessage.add(checkAddress(latitude, longitude));
        errorMessage.add(checkMail(mailText));
        errorMessage.add(checkPassword(passwordText));
        errorMessage.add(checkConfirmPassword(passwordText, confirmPasswordText));

        errorMessage.removeIf(String::isEmpty);

        if (!errorMessage.isEmpty()) {
            popup.showAlertDialog(
                    getString(R.string.please_fix_the_following_errors),
                    // Concatenate all error messages into one string.
                    // Each message is separated by a newline character.
                    errorMessage.stream().reduce("", (acc, s) -> acc + "\n" + s)
            );
        } else {
            sendInformationToSignInUser(firstNameText, lastNameText, latitude, longitude, mailText, passwordText);
        }
    }

    /**
     * Check the first name field.
     *
     * @param firstNameText
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
     *
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
     *
     * @param latitude
     * @return errorMessage
     */
    public String checkAddress(double latitude, double longitude) {
        String errorMessage = "";

        if (!isLatitudeValid(latitude) || !isLongitudeValid(longitude)) {
            labelAddress.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.address_missing);
        }

        return errorMessage;
    }

    /**
     * Check the mail field.
     *
     * @param mailText
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
     *
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
     *
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
        labelAddress.setTextColor(getColor(R.color.black));
        labelMail.setTextColor(getColor(R.color.black));
        labelPassword.setTextColor(getColor(R.color.black));
    }

    /**
     * Send information to the API for sign in the user with the entered informations.
     */
    public void sendInformationToSignInUser(String firstNameText, String lastNameText,
                                            double latitudeValue, double longitudeValue,
                                            String mailText, String passwordText) {

        SignUpInput signUpInput = new SignUpInput(
                firstNameText,
                lastNameText,
                latitudeValue,
                longitudeValue,
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