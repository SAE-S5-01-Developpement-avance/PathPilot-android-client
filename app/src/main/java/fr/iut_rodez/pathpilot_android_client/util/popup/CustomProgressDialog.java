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
    private final String messageToDisplay;

    /**
     * Creates a dialog with a progress spinner and a message.
     * <p>
     * The supplied {@code context} is used to get the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     * @param message the message to display
     */
    public CustomProgressDialog(@NonNull Context context, String message) {
        super(context);
        Log.d(TAG, "CustomProgressDialog: " + message);
        messageToDisplay = message;
    }

    /**
     * Creates a dialog with a progress spinner and a default message.
     * <p>
     * The default message is "Loading..."
     *
     * @param context
     */
    public CustomProgressDialog(@NonNull Context context) {
        this(context, context.getString(R.string.loading));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress_dialog);

        message = findViewById(R.id.message);
        message.setText(messageToDisplay);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setMessage(int message) {
        this.message.setText(message);
    }

    public void show() {
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }
}
