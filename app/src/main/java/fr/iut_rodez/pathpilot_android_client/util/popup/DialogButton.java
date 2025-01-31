package fr.iut_rodez.pathpilot_android_client.util.popup;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * Describe a button with a text and an onClickListener.
 *
 * @param text            the text of the button
 * @param onClickListener the onClickListener of the button
 */
public record DialogButton(String text, DialogInterface.OnClickListener onClickListener) {

    /**
     * Create a button with the text "OK" that dismisses the dialog.
     * @return the button
     */
    public static DialogButton OKdismiss() {
        return new DialogButton("OK", (dialog, which) -> dialog.dismiss());
    }


    /**
     * Create a button with the text "OK" that dismisses the dialog and finish the activity.
     * @param activity the activity to finish
     * @return the button
     */
    public static DialogButton OKFinish(Activity activity) {
        return new DialogButton("OK", (dialog, which) -> {
            dialog.dismiss();
            activity.finish();
        });
    }
}