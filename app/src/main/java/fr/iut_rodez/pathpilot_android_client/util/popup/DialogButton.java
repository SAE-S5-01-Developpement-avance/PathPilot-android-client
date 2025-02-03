package fr.iut_rodez.pathpilot_android_client.util.popup;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import fr.iut_rodez.pathpilot_android_client.R;

/**
 * Describe a button with a text and an onClickListener.
 *
 * @param text            the text of the button
 * @param onClickListener the onClickListener of the button
 */
public record DialogButton(String text, DialogInterface.OnClickListener onClickListener) {

    /**
     * Create a button with the text "OK" that dismisses the dialog.
     *
     * @return the button
     */
    public static DialogButton okDismiss(Context context) {
        return new DialogButton(context.getString(R.string.ok), getDismissListener());
    }


    /**
     * Create a button with the text "OK" that dismisses the dialog and finish the activity.
     *
     * @param activity the activity to finish
     * @return the button
     */
    public static DialogButton okFinish(Activity activity) {
        return new DialogButton(activity.getString(R.string.ok), getFinishListener(activity));
    }

    /**
     * Return an onClickListener that dismisses the dialog.
     * @return the onClickListener
     */
    public static DialogInterface.OnClickListener getDismissListener() {
        return (dialog, which) -> dialog.dismiss();
    }

    /**
     * Return an onClickListener that dismisses the dialog and finish the activity.
     * @param activity the activity to finish
     * @return the onClickListener
     */
    public static DialogInterface.OnClickListener getFinishListener(Activity activity) {
        return (dialog, which) -> {
            dialog.dismiss();
            activity.finish();
        };
    }
}