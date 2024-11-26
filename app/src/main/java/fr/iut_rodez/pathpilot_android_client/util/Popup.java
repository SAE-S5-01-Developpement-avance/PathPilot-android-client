package fr.iut_rodez.pathpilot_android_client.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

public class Popup {

    private final Context context;

    public Popup(Context context) {
        this.context = context;
    }

    private void showToast(String message, int length) {
        new Toast(context);
        Toast.makeText(context, message, length).show();
    }

    public void showToastLong(String message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    public void showToastShort(String message) {
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
