package com.newwesterndev.tutoru;

import com.google.firebase.auth.FirebaseAuth;
import com.newwesterndev.tutoru.utilities.FirebaseManager;
import com.newwesterndev.tutoru.utilities.Utility;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UnitTests {

    Utility mUtility;
    FirebaseAuth mAuth;
    FirebaseManager fbManager;

    @Before
    public void setUp() throws Exception {

        // Mock your objects here, Lee
        // you can create them in the test folder and create instances of them with specific state
        // then test the functions.  I put some below to get your started and lemme know if you
        // have questions.
        mUtility = new Utility();
    }

    @Test
    public void testForCorrectEmailType() {
        String testEmail = "phil@tutoru.com";
        boolean result = mUtility.isValidEmail(testEmail);
        assertTrue(result);
    }

    @Test
    public void testForCorrectPasswordFormat() {
        String testPassword = "Password1";
        boolean result = mUtility.isValidPassword(testPassword);
        assertTrue(result);
    }
    @Test
    public void testForEmptyEmail() {
        String email = "";
        boolean result = mUtility.isValidEmail(email);
        assertFalse(result);
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void getCurrentUserId() {
        String id = fbManager.getUserUniqueId();
        assertNull(null);
    }

    @Test
    public void saveCurrentUserId() {
        assertTrue(true);
    }

    @Test
    public void saveUsersChatMessage() {
    }

    @Test
    public void getUsersChatMessage() {
    }

    @Test
    public void logUserIntoFirebase() {

    }

}
