package fr.iut_rodez.pathpilot_android_client.home.clients;

import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isDescriptionValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isFirstNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLastNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLatitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLongitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isPhoneNumberValid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.util.Popup;

/**
 * Handle the sign up Activity
 */
public class AddClient extends AppCompatActivity {

    private static final String TAG = fr.iut_rodez.pathpilot_android_client.signup.SignUp.class.getSimpleName();
    public static final String CLE_CLIENT_ADDED = "clientAdded";


    private EditText companyName;
    private EditText latitude;
    private EditText longitude;
    private EditText description;
    private RadioGroup clientType;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;

    private TextView labelCompanyName;
    private TextView labelLatitude;
    private TextView labelLongitude;
    private TextView labelDescription;
    private TextView labelFirstName;
    private TextView labelLastName;
    private TextView labelPhoneNumber;

    private Popup popup;
    private JWTToken jwtToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_create_client);

        companyName = findViewById(R.id.company_name);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        description = findViewById(R.id.description);
        clientType = findViewById(R.id.groupradio);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);

        labelCompanyName = findViewById(R.id.label_company_name);
        labelLatitude = findViewById(R.id.label_latitude);
        labelLongitude = findViewById(R.id.label_longitude);
        labelDescription = findViewById(R.id.label_description);
        labelFirstName = findViewById(R.id.label_first_name);
        labelLastName = findViewById(R.id.label_last_name);
        labelPhoneNumber = findViewById(R.id.label_phone_number);

        findViewById(R.id.create_client_button).setOnClickListener(v -> createAccount());
        findViewById(R.id.backButton).setOnClickListener(v -> gotoClient());

        popup = new Popup(this);

        Intent intent = getIntent();
        jwtToken = intent.getParcelableExtra(FragmentClients.CLE_TOKEN);
    }

    /**
     * Checks that the parameters entered are valid.
     * Creates an account or notifies the user of input errors.
     */
    public void createAccount() {
        StringBuilder errorMessage = new StringBuilder();
        // reset the style of the text field
        resetFieldStyle();

        String companyNameText = companyName.getText().toString();
        String latitudeText = latitude.getText().toString();
        String longitudeText = longitude.getText().toString();
        String descriptionText = description.getText().toString().trim().isEmpty() ? "" : description.getText().toString();
        boolean isClient = clientType.getCheckedRadioButtonId() == R.id.radio_client;
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String phoneNumberText = phoneNumber.getText().toString();

        errorMessage.append(checkCompanyName(companyNameText));
        errorMessage.append(checkLatitude(latitudeText));
        errorMessage.append(checkLongitude(longitudeText));

        // If the optional description field is not empty, check it
        if (!descriptionText.isEmpty()) {
            descriptionText = description.getText().toString();
            errorMessage.append(checkDescription(descriptionText));
        }

        // If the optional first name field is not empty, check it
        if (!firstNameText.isEmpty()) {
            firstNameText = firstName.getText().toString();
            errorMessage.append(checkFirstName(firstNameText));
        }

        // If the optional last name field is not empty, check it
        if (!lastNameText.isEmpty()) {
            lastNameText = lastName.getText().toString();
            errorMessage.append(checkLastName(lastNameText));
        }

        // If the optional phone number field is not empty, check it
        if (!phoneNumberText.isEmpty()) {
            phoneNumberText = phoneNumber.getText().toString();
            errorMessage.append(checkPhoneNumber(phoneNumberText));
        }


        if (errorMessage.length() != 0) {
            popup.showToastLong(errorMessage.toString());
        } else {
            double latitudeValue = Double.parseDouble(latitudeText);
            double longitudeValue = Double.parseDouble(longitudeText);
            sendInformationToCreateClient(companyNameText, latitudeValue, longitudeValue, descriptionText, isClient, firstNameText, lastNameText, phoneNumberText);
        }
    }

    /**
     * Check the company name field.
     *
     * @return errorMessage
     */
    public String checkCompanyName(String companyNameText) {
        String errorMessage = "";

        if (companyNameText.isEmpty()) {
            labelCompanyName.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.company_name_blank);
        }
        return errorMessage;
    }

    /**
     * Check the first name field.
     *
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
     * Check the description field.
     *
     * @return errorMessage
     */
    public String checkDescription(String descriptionText) {
        String errorMessage = "";

        if (!isDescriptionValid(descriptionText)) {
            labelDescription.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.description_blank);
        }
        return errorMessage;
    }

    /**
     * Check the phone number field.
     *
     * @return errorMessage
     */
    public String checkPhoneNumber(String phoneNumberText) {
        String errorMessage = "";

        if (!isPhoneNumberValid(phoneNumberText)) {
            labelPhoneNumber.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.phone_number_blank);
        }
        return errorMessage;
    }

    /**
     * Check the latitude field.
     *
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
     *
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
     * Reset the style of sign up interface.
     */
    public void resetFieldStyle() {
        labelCompanyName.setTextColor(getColor(R.color.black));
        labelLatitude.setTextColor(getColor(R.color.black));
        labelLongitude.setTextColor(getColor(R.color.black));
        labelDescription.setTextColor(getColor(R.color.black));
        labelFirstName.setTextColor(getColor(R.color.black));
        labelLastName.setTextColor(getColor(R.color.black));
        labelPhoneNumber.setTextColor(getColor(R.color.black));
    }

    /**
     * Send information to the API for sign in the user with the entered informations.
     */
    public void sendInformationToCreateClient(String companyNameText, double latitudeValue, double longitudeValue, String descriptionText, boolean isClient, String firstNameText, String lastNameText, String phoneNumberText) {
        ClientService.addClient(this, new Client(companyNameText, latitudeValue, longitudeValue, descriptionText, isClient, firstNameText, lastNameText, phoneNumberText));
    }

    /**
     * Sends to the sign in interface when the sign in Textview is clicked.
     */
    public void gotoClient() {
        Log.d(TAG, "Switch to Home activity");

        Intent returnIntention = new Intent();
        returnIntention.putExtra(CLE_CLIENT_ADDED, false);
        setResult(RESULT_OK, returnIntention);

        finish();
    }

    public JWTToken getJWTToken() {
        return jwtToken;
    }
}