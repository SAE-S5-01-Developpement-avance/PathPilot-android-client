package fr.iut_rodez.pathpilot_android_client;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    private final int FIRST_NAME_MAX_SIZE = 30;
    private final int LAST_NAME_MAX_SIZE = 30;
    private final int PASSWORD_NAME_MIN_SIZE = 8;
    private final String REGEX_MAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private final String REGEX_LONGITUDE_LATITUDE = "^[0-9]{1,3}+.[0-9]{5}$";

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
        String first_name = firstName.getText().toString();
        if (first_name.length()>FIRST_NAME_MAX_SIZE){
            //TODO lancer la méthode de gestion d'erreur de saisie
        }
    }

    /**
     * Indicates the input error to the user
     * @param label
     * @param message
     */
    public void setErrorField(TextView label, String message){
        
    }
}