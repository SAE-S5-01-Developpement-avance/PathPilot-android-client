package fr.iut_rodez.pathpilot_android_client;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    private final int FIRST_NAME_MAX_SIZE = 20;
    private final int LAST_NAME_MAX_SIZE = 30;
    private final int PASSWORD_MIN_SIZE = 8;
    private final String REGEX_MAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

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

        findViewById(R.id.sign_up_button).setOnClickListener(this);

    }

    /**
     * Checks that the parameters entered are valid.
     * Creates an account or notifies the user of input errors.
     * @param v
     */
    public void onClick(View v) {
        String errorMessage = "";
        String firstNameText = firstName.getText().toString();
        String lastNameText = lastName.getText().toString();
        String latitudeText = latitude.getText().toString();
        String longitudeText = longitude.getText().toString();
        String mailText = mail.getText().toString();
        String passwordText = password.getText().toString();
        float latitudeValue = 0;
        float longitudeValue = 0;
        // reset the style of the text field
        resetFieldStyle();

        //TODO use the good error messages from string file
        try {
            latitudeValue = Float.parseFloat(latitudeText);
        } catch (Exception e) {
            labelLatitude.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        try {
            longitudeValue = Float.parseFloat(longitudeText);
        } catch (Exception e) {
            labelLongitude.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }

        if (firstNameText.isBlank()) {
            labelFirstName.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (firstNameText.length() > FIRST_NAME_MAX_SIZE) {
            labelFirstName.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (lastNameText.isBlank()) {
            labelLastName.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (lastNameText.length() > LAST_NAME_MAX_SIZE) {
            labelLastName.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (latitudeText.isBlank()) {
            labelLatitude.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (latitudeValue>=90f || latitudeValue<=-90f) {
            labelLatitude.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (longitudeText.isBlank()) {
            labelLongitude.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (longitudeValue>=180 || longitudeValue<=-180) {
            labelLongitude.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (mailText.isBlank()) {
            labelMail.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (!mailText.matches(REGEX_MAIL)) {
            labelMail.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (passwordText.isBlank()) {
            labelPassword.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (passwordText.length() <= PASSWORD_MIN_SIZE) {
            labelPassword.setTextColor(getColor(R.color.red));
            errorMessage+="";
        }
        if (!errorMessage.isEmpty()){
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Reset the style of sign up interface.
     */
    public void resetFieldStyle(){
        labelFirstName.setTextColor(getColor(R.color.black));
        labelLastName.setTextColor(getColor(R.color.black));
        labelLatitude.setTextColor(getColor(R.color.black));
        labelLongitude.setTextColor(getColor(R.color.black));
        labelMail.setTextColor(getColor(R.color.black));
        labelPassword.setTextColor(getColor(R.color.black));
    }

    /**
     * Sends to the sign in interface when the sign in Textview is clicked.
     * @param v
     */
    public void onClickSignIn(View v) {
        //TODO go on the sign in interface
    }
}