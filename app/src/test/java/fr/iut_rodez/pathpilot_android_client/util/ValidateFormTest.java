package fr.iut_rodez.pathpilot_android_client.util;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test class for the ValidateForm class.
 */
public class ValidateFormTest {

    @Test
    public void testIsEmailValid() {
        //Valid email
        assertTrue(ValidateForm.isEmailValid("test.test@test.com"));

        // Invalid email
        assertFalse(ValidateForm.isEmailValid("test.test"));
        assertFalse(ValidateForm.isEmailValid(""));
        assertFalse(ValidateForm.isEmailValid(null));
    }

    @Test
    public void testIsFirstNameValid() {
        //Valid first name
        assertTrue(ValidateForm.isFirstNameValid("John"));

        // Invalid first name
        assertFalse(ValidateForm.isFirstNameValid(""));
        assertFalse(ValidateForm.isFirstNameValid(null));
    }

    @Test
    public void testIsLastNameValid() {
        //Valid last name
        assertTrue(ValidateForm.isLastNameValid("Doe"));

        // Invalid last name
        assertFalse(ValidateForm.isLastNameValid(""));
        assertFalse(ValidateForm.isLastNameValid(null));
    }

    @Test
    public void testIsLatitudeValid() {
        //Valid latitude
        assertTrue(ValidateForm.isLatitudeValid(45.0));

        // Invalid latitude
        assertFalse(ValidateForm.isLatitudeValid(Double.NaN));
        assertFalse(ValidateForm.isLatitudeValid(91.0));
        assertFalse(ValidateForm.isLatitudeValid(-91.0));
    }

    @Test
    public void testIsLongitudeValid() {
        //Valid longitude
        assertTrue(ValidateForm.isLongitudeValid(45.0));

        // Invalid longitude
        assertFalse(ValidateForm.isLongitudeValid(Double.NaN));
        assertFalse(ValidateForm.isLongitudeValid(181.0));
        assertFalse(ValidateForm.isLongitudeValid(-181.0));
    }

    @Test
    public void testIsPasswordValid() {
        //Valid password
        assertTrue(ValidateForm.isPasswordValid("12345678"));

        // Invalid password
        assertFalse(ValidateForm.isPasswordValid("1234567"));
        assertFalse(ValidateForm.isPasswordValid(""));
        assertFalse(ValidateForm.isPasswordValid(null));
    }
}