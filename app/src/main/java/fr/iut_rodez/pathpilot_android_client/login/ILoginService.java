package fr.iut_rodez.pathpilot_android_client.login;

import android.content.Context;

import fr.iut_rodez.pathpilot_android_client.BuildConfig;

/**
 * Interface for LoginService class.
 */
public interface ILoginService {
    String TAG = ILoginService.class.getSimpleName();
    String LOGIN_URL = BuildConfig.API_BASE_URL + "auth/login";


    /**
     * Login the user with the given email and password.
     *
     * @param email      the email of the user
     * @param password   the password of the user
     * @param context    the context of the application
     * @throws IllegalArgumentException if email or password is empty
     */
    void login(String email, String password, Context context);
}
