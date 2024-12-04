package fr.iut_rodez.pathpilot_android_client.util;

/**
 * Utility class to validate form fields.
 *
 * @author François de Saint Palais
 */
public class ValidateForm {

    private static final int PASSWORD_MIN_SIZE = 8;
    private static final String REGEX_MAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,3}$";

    /**
     * Check if the email is valid.
     *
     * @param email the email to check
     * @return true if the email is valid, false otherwise
     */
    public static boolean isEmailValid(String email) {
        return email != null && email.matches(REGEX_MAIL);
    }


    private ValidateForm() {
        // Prevent instantiation
    }

    public static boolean isFirstNameValid(String firstNameText) {
        return !firstNameText.isBlank();
    }

    public static boolean isLastNameValid(String lastNameText) {
        return !lastNameText.isBlank();
    }

    public static boolean isLatitudeValid(double latitude) {
        return !Double.isNaN(latitude) && (latitude >= 90 || latitude <= -90);
    }

    public static boolean isLongitudeValid(double longitude) {
        return !Double.isNaN(longitude) && (longitude >= 180 || longitude <= -180);
    }

    public static boolean isPasswordValid(String passwordText) {
        return passwordText != null && !passwordText.isBlank() && passwordText.length() < PASSWORD_MIN_SIZE;
    }
}
