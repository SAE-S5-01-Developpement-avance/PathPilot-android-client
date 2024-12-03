package fr.iut_rodez.pathpilot_android_client.util;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for the ValidateForm class.
 * @author François de Saint Palais
 */
public class ValidateFormTest {

    @Test
    public void testIsEmailValidWithValidEmail() {
        // Given an valid email
        String email = "test.test@test.com";
        // When we check if the email is valid
        boolean result = ValidateForm.isEmailValid(email);
        // Then the result should be true
        assertTrue(result);
    }

    @Test
    public void testIsEmailValidWithInvalidEmail() {
        // Given an invalid email
        String email = "test.test";
        // When we check if the email is valid
        boolean result = ValidateForm.isEmailValid(email);
        // Then the result should be false
        assertFalse(result);
    }

    @Test
    public void testIsEmailValidWithEmptyEmail() {
        // Given an empty email
        String email = "";
        // When we check if the email is valid
        boolean result = ValidateForm.isEmailValid(email);
        // Then the result should be false
        assertFalse(result);
    }

    @Test
    public void testIsEmailValidWithNullEmail() {
        // Given a null email
        String email = null;
        // When we check if the email is valid
        boolean result = ValidateForm.isEmailValid(email);
        // Then the result should be false
        assertFalse(result);
    }
}