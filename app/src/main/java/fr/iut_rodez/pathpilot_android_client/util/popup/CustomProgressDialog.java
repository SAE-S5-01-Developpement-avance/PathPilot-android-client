package fr.iut_rodez.pathpilot_android_client.util.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import fr.iut_rodez.pathpilot_android_client.R;

public class CustomProgressDialog extends AlertDialog {
    private static final String TAG = CustomProgressDialog.class.getSimpleName();
    private TextView message;
    private String messageToDisplay;

    /**
     * Creates a dialog with a progress spinner and a default message.
     * <p>
     * The default message is "Loading..."
     *
     * @param context
     */
    public CustomProgressDialog(@NonNull Context context) {
        super(context);
        messageToDisplay = context.getString(R.string.loading);
    }

    public void setMessage(String messageToDisplay) {
        this.messageToDisplay = messageToDisplay;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress_dialog);

        message = findViewById(R.id.message);
        message.setText(messageToDisplay);
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }
}
