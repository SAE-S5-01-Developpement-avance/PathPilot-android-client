package fr.iut_rodez.pathpilot_android_client.util;

/**
 * Utility class to validate form fields.
 */
public class ValidateForm {

    private static final String REGEX_MAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Check if the email is valid.
     * @param email the email to check
     * @return true if the email is valid, false otherwise
     */
    public static boolean isEmailValid(String email) {
        return email.matches(REGEX_MAIL);
    }
}
