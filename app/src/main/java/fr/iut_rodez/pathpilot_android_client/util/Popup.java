package fr.iut_rodez.pathpilot_android_client.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import fr.iut_rodez.pathpilot_android_client.R;

/**
 * Instanceable class to display popups.
 */
public class Popup {

    private final Context context;

    private ProgressDialog progressDialog;

    public Popup(Context context) {
        this.context = context;
    }

    public void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void showProgressDialog() {
        showProgressDialog("", "");
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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

    /**
     * Show an alert dialog with the given title and message.
     * The dialog will have an OK button that dismisses the dialog.
     *
     * @param title   the title of the dialog
     * @param message the message of the dialog
     */
    public void showAlertDialog(String title, String message) {
        Button okButton = new Button("OK", (dialog, which) -> dialog.dismiss());
        showAlertDialog(title, message, okButton, null, null);
    }


    /**
     * Show an alert dialog with the given title, message and onClickOKListener.
     * If an onClickOKListener is provided, the dialog will have an OK button that dismisses the dialog.
     *
     * @param title          the title of the dialog
     * @param message        the message of the dialog
     * @param positiveButton the button to display
     *                       with the text and the onClickListener
     * @param neutralButton  the button to display
     *                       with the text and the onClickListener
     * @param negativeButton the button to display
     *                       with the text and the onClickListener
     */
    public void showAlertDialog(String title, String message, Button positiveButton, Button neutralButton, Button negativeButton) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message);

        if (positiveButton != null) {
            dialog.setPositiveButton(positiveButton.text(), positiveButton.onClickListener());
        }
        if (neutralButton != null) {
            dialog.setNeutralButton(neutralButton.text(), neutralButton.onClickListener());
        }
        if (negativeButton != null) {
            dialog.setNegativeButton(negativeButton.text(), negativeButton.onClickListener());
        }

        // If no button is provided, add an OK button that dismisses the dialog
        if (positiveButton == null && neutralButton == null && negativeButton == null) {
            dialog.setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss());
        }

        dialog.show();
    }

    /**
     * Describe a button with a text and an onClickListener.
     *
     * @param text            the text of the button
     * @param onClickListener the onClickListener of the button
     */
    public record Button(String text, DialogInterface.OnClickListener onClickListener) {
    }

    public static void showAlertDialog(Context context, String title, String message) {
        new Popup(context).showAlertDialog(title, message);
    }

    public static void showAlertDialog(Context context, String title, String message, Button positiveButton, Button neutralButton, Button negativeButton) {
        new Popup(context).showAlertDialog(title, message, positiveButton, neutralButton, negativeButton);
    }

    public void showAlertDialog(String title, String message, Button positiveButton) {
        showAlertDialog(title, message, positiveButton, null, null);
    }

    public void showErrorDialog(String message) {
        showAlertDialog(context.getString(R.string.error), message);
    }
}
