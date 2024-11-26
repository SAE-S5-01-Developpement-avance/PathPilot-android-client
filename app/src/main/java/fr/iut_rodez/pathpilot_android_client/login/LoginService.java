package fr.iut_rodez.pathpilot_android_client.login;

public class LoginService {

    /**
     * Login the user with the given email and password.
     * @param email the email of the user
     * @param password the password of the user
     * @return true if the login is successful, false otherwise
     * @throws IllegalArgumentException if email or password is empty
     */
    public static boolean login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Email and password must not be empty");
        }

        //TODO request login to the API
        return true; //STUB
    }
}
