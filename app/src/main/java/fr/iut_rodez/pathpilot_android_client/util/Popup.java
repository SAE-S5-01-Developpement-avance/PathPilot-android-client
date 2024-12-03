package fr.iut_rodez.pathpilot_android_client.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Instanceable class to display popups.
 * @author François de Saint Palais
 */
public class Popup {

    private final Context context;

    public Popup(Context context) {
        this.context = context;
    }

    private void showToast(CharSequence message, int length) {
        new Toast(context);
        Toast.makeText(context, message, length).show();
    }

    public void showToastLong(CharSequence message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    public void showToastShort(CharSequence message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
