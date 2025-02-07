package fr.iut_rodez.pathpilot_android_client.home.clients;

import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isDescriptionValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isFirstNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLastNameValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLatitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isLongitudeValid;
import static fr.iut_rodez.pathpilot_android_client.util.ValidateForm.isPhoneNumberValid;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.iut_rodez.pathpilot_android_client.R;
import fr.iut_rodez.pathpilot_android_client.ServiceFactory;
import fr.iut_rodez.pathpilot_android_client.login.JWTToken;
import fr.iut_rodez.pathpilot_android_client.map.MapSelection;
import fr.iut_rodez.pathpilot_android_client.util.popup.Popup;

/**
 * Handle the sign up Activity
 */
public class AddClient extends AppCompatActivity {

    private static final String TAG = fr.iut_rodez.pathpilot_android_client.signup.SignUp.class.getSimpleName();
    public static final String ADDED_CLIENT_KEY = "clientAdded";

    private ActivityResultLauncher<Intent> launcherMapSelection;

    private EditText companyName;
    private TextView address;
    private EditText description;
    private RadioGroup clientType;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;

    private TextView labelCompanyName;
    private TextView labelAddress;
    private TextView labelDescription;
    private TextView labelFirstName;
    private TextView labelLastName;
    private TextView labelPhoneNumber;

    private double latitude = Double.NaN;
    private double longitude = Double.NaN;

    private Popup popup;
    private JWTToken jwtToken;
    private final IClientService clientService = ServiceFactory.getClientService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_create_client);

        companyName = findViewById(R.id.company_name);
        address = findViewById(R.id.address);
        description = findViewById(R.id.description);
        clientType = findViewById(R.id.groupradio);
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        phoneNumber = findViewById(R.id.phone_number);

        labelCompanyName = findViewById(R.id.label_company_name);
        labelAddress = findViewById(R.id.label_address);
        labelDescription = findViewById(R.id.label_description);
        labelFirstName = findViewById(R.id.label_first_name);
        labelLastName = findViewById(R.id.label_last_name);
        labelPhoneNumber = findViewById(R.id.label_phone_number);

        findViewById(R.id.create_client_button).setOnClickListener(v -> createAccount());
        findViewById(R.id.backButton).setOnClickListener(v -> gotoClient());
        findViewById(R.id.selection_map_button).setOnClickListener(v -> gotoMapSelection());

        popup = new Popup(this);

        Intent intent = getIntent();
        jwtToken = intent.getParcelableExtra(FragmentClients.TOKEN_KEY);

        launcherMapSelection = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleReturnedMapSelection);
    }

    private void gotoMapSelection() {
        Intent intent = new Intent(this, MapSelection.class);

        if (!Double.isNaN(latitude) && Double.isNaN(longitude)) {
            intent.putExtra(MapSelection.KEY_LATITUDE, latitude);
            intent.putExtra(MapSelection.KEY_LONGITUDE, longitude);
        }

        launcherMapSelection.launch(intent);
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

    /**
     * Checks that the parameters entered are valid.
     * Creates an account or notifies the user of input errors.
     */
    public void createAccount() {
        ArrayList<String> errorMessage = new ArrayList<>();
        // reset the style of the text field
        resetFieldStyle();

        String companyNameText = companyName.getText().toString();
        String descriptionText = description.getText().toString().trim().isEmpty() ? "" : description.getText().toString();
        Boolean isClient = getClientGategory();
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String phoneNumberText = phoneNumber.getText().toString();

        errorMessage.add(checkCompanyName(companyNameText));
        errorMessage.add(checkAddress(latitude, longitude));

        // If the optional description field is not empty, check it
        if (!descriptionText.isEmpty()) {
            descriptionText = description.getText().toString();
            errorMessage.add(checkDescription(descriptionText));
        }

        // If the optional first name field is not empty, check it
        if (!firstNameText.isEmpty()) {
            firstNameText = firstName.getText().toString();
            errorMessage.add(checkFirstName(firstNameText));
        }

        // If the optional last name field is not empty, check it
        if (!lastNameText.isEmpty()) {
            lastNameText = lastName.getText().toString();
            errorMessage.add(checkLastName(lastNameText));
        }

        // If the optional phone number field is not empty, check it
        if (!phoneNumberText.isEmpty()) {
            phoneNumberText = phoneNumber.getText().toString();
            errorMessage.add(checkPhoneNumber(phoneNumberText));
        }

        errorMessage.removeIf(String::isEmpty);

        if (!errorMessage.isEmpty()) {
            popup.showAlertDialog(
                    getString(R.string.please_fix_the_following_errors),
                    // Concatenate all error messages into one string.
                    // Each message is separated by a newline character.
                    errorMessage.stream().reduce("", (acc, s) -> acc + "\n" + s)
            );
        } else {
            sendInformationToCreateClient(companyNameText, latitude, longitude, descriptionText, isClient, firstNameText, lastNameText, phoneNumberText);
        }
    }

    /**
     * Get the client category.
     * <p>
     *     If the client radio button is checked, return true.
     *     If the prospect radio button is checked, return false.
     *     If neither radio button is checked, return null.
     * </p>
     * @return the client category or null if no radio button is checked
     */
    private Boolean getClientGategory() {
        return clientType.getCheckedRadioButtonId() == -1 ? null : clientType.getCheckedRadioButtonId() == R.id.radio_client;
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
    public String checkAddress(double latitudeValue, double longitudeValue) {
        String errorMessage = "";

        if (!isLatitudeValid(latitudeValue) || !isLongitudeValid(longitudeValue)) {
            labelAddress.setTextColor(getColor(R.color.red));
            errorMessage = getString(R.string.address_missing);
        }

        return errorMessage;
    }

    /**
     * Reset the style of add client interface.
     */
    public void resetFieldStyle() {
        labelCompanyName.setTextColor(getColor(R.color.black));
        labelAddress.setTextColor(getColor(R.color.black));
        labelDescription.setTextColor(getColor(R.color.black));
        labelFirstName.setTextColor(getColor(R.color.black));
        labelLastName.setTextColor(getColor(R.color.black));
        labelPhoneNumber.setTextColor(getColor(R.color.black));
    }

    /**
     * Send information to the API for sign in the user with the entered informations.
     */
    public void sendInformationToCreateClient(String companyNameText, double latitudeValue, double longitudeValue, String descriptionText, Boolean isClient, String firstNameText, String lastNameText, String phoneNumberText) {
        clientService.addClient(this, new Client(companyNameText, latitudeValue, longitudeValue, descriptionText, isClient, firstNameText, lastNameText, phoneNumberText));
    }

    /**
     * Sends to the sign in interface when the sign in Textview is clicked.
     */
    public void gotoClient() {
        Log.d(TAG, "Switch to Home activity");

        Intent returnIntention = new Intent();
        returnIntention.putExtra(ADDED_CLIENT_KEY, false);
        setResult(RESULT_OK, returnIntention);

        finish();
    }

    public JWTToken getJWTToken() {
        return jwtToken;
    }
}