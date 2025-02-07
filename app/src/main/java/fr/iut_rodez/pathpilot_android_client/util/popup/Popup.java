package fr.iut_rodez.pathpilot_android_client.util.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import org.jetbrains.annotations.Nls;

import fr.iut_rodez.pathpilot_android_client.R;

/**
 * Instanceable class to display popups.
 */
public class Popup {

    private static final String TAG = Popup.class.getSimpleName();
    private final Context context;
    private CustomProgressDialog customProgressDialog;

    public Popup(Context context) {
        this.context = context;
    }

    public void showProgressDialog(@Nls String message) {
        dismissProgressDialog(); // Dismiss any previous dialog

        customProgressDialog = new CustomProgressDialog(context);
        customProgressDialog.setCancelable(false);
        if (message != null && !message.isBlank()) {
            customProgressDialog.setMessage(message);
        }
        customProgressDialog.show();
    }

    public void showProgressDialog() {
        showProgressDialog(null);
    }

    public void dismissProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog.dismiss();
        }
    }

    private void showToast(@Nls CharSequence message, int length) {
        new Toast(context);
        Toast.makeText(context, message, length).show();
    }

    public void showToastLong(@Nls CharSequence message) {
        showToast(message, Toast.LENGTH_LONG);
    }

    public void showToastShort(@Nls CharSequence message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * Show an alert dialog with the given title and message.
     * The dialog will have an OK button that dismisses the dialog.
     *
     * @param title   the title of the dialog
     * @param message the message of the dialog
     */
    public void showAlertDialog(@Nls String title, @Nls String message) {
        showAlertDialog(title, message, DialogButton.okDismiss(context), null, null);
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
    public void showAlertDialog(@Nls String title, @Nls String message, DialogButton positiveDialogButton, DialogButton neutralDialogButton, DialogButton negativeDialogButton) {
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
            DialogButton okButton = DialogButton.okDismiss(context);
            dialog.setPositiveButton(okButton.text(), okButton.onClickListener());
        }

        dialog.show();
    }

    public void showErrorDialog(@Nls String message) {
        showAlertDialog(context.getString(R.string.error), message);
    }

    public void showAlertDialogOK(@Nls String title, @Nls String message, DialogButton positiveDialogButton) {
        showAlertDialog(title, message, positiveDialogButton, null, null);
    }
}
