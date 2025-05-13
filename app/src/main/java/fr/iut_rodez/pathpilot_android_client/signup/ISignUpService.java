package fr.iut_rodez.pathpilot_android_client.signup;

import android.content.Context;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;

/**
 * Interface to the SignUpService class.
 */
public interface ISignUpService {

    String LOGIN_URL = BuildConfig.API_BASE_URL + "auth/signup";
    String TAG = ISignUpService.class.getSimpleName();

    /**
     * Sign up the user with the given information.
     * <p>
     * If the request is successful, a dialog is shown to the user with two buttons:
     * <ul>
     *     <li>One to dismiss the dialog</li>
     *     <li>One to go to the login page</li>
     * </ul>
     * <p>
     * If the request fails, a dialog is shown to the user with an error message.
     *
     * @param signUpInput the information of the user
     * @param context     the context of the SignUp activity
     */
    void signUp(SignUpInput signUpInput, Context context);
}
