package fr.iut_rodez.pathpilot_android_client.util;

/**
 * Utility class to validate form fields.
 *
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
        return firstNameText != null && !firstNameText.isBlank();
    }

    public static boolean isLastNameValid(String lastNameText) {
        return lastNameText != null && !lastNameText.isBlank();
    }

    public static boolean isLatitudeValid(double latitude) {
        return !Double.isNaN(latitude) && -90 <= latitude && latitude <= 90;
    }

    public static boolean isLongitudeValid(double longitude) {
        return !Double.isNaN(longitude) && -180 <= longitude && longitude <= 180;
    }

    public static boolean isPasswordValid(String passwordText) {
        return passwordText != null && !passwordText.isBlank() && passwordText.length() >= PASSWORD_MIN_SIZE;
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber != null && !phoneNumber.isBlank() && phoneNumber.length() == 10 && phoneNumber.matches("[0-9]+");
    }

    public static boolean isDescriptionValid(String descriptionText) {
        return descriptionText != null && !descriptionText.isBlank();
    }
}
