package fr.iut_rodez.pathpilot_android_client.util.popup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
        DialogButton okDialogButton = new DialogButton("OK", (dialog, which) -> dialog.dismiss());
        showAlertDialog(title, message, okDialogButton, null, null);
    }


    /**
     * Show an alert dialog with the given title, message and onClickOKListener.
     * If an onClickOKListener is provided, the dialog will have an OK button that dismisses the dialog.
     *
     * @param title          the title of the dialog
     * @param message        the message of the dialog
     * @param positiveDialogButton the button to display
     *                       with the text and the onClickListener
     * @param neutralDialogButton  the button to display
     *                       with the text and the onClickListener
     * @param negativeDialogButton the button to display
     *                       with the text and the onClickListener
     */
    public void showAlertDialog(String title, String message, DialogButton positiveDialogButton, DialogButton neutralDialogButton, DialogButton negativeDialogButton) {
        AlertDialog.Builder dialog =
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message);

        if (positiveDialogButton != null) {
            dialog.setPositiveButton(positiveDialogButton.text(), positiveDialogButton.onClickListener());
        }
        if (neutralDialogButton != null) {
            dialog.setNeutralButton(neutralDialogButton.text(), neutralDialogButton.onClickListener());
        }
        if (negativeDialogButton != null) {
            dialog.setNegativeButton(negativeDialogButton.text(), negativeDialogButton.onClickListener());
        }

        // If no button is provided, add an OK button that dismisses the dialog
        if (positiveDialogButton == null && neutralDialogButton == null && negativeDialogButton == null) {
            DialogButton okButton = DialogButton.OKdismiss();
            dialog.setPositiveButton(okButton.text(), okButton.onClickListener());
        }

        dialog.show();
    }

    public void showErrorDialog(String message) {
        showAlertDialog(context.getString(R.string.error), message);
    }

    public void showAlertDialogOK(String title, String message, DialogButton positiveDialogButton) {
        showAlertDialog(title, message, positiveDialogButton, null, null);
    }
}
