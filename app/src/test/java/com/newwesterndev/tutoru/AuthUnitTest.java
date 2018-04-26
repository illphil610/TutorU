package com.newwesterndev.tutoru;

import com.google.firebase.auth.FirebaseAuth;
import com.newwesterndev.tutoru.utilities.Utility;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AuthUnitTest {

    Utility mUtility;
    FirebaseAuth mAuth;

    @Before
    public void setUp() throws Exception {

        mUtility = new Utility();
        //mAuth = FirebaseAuth.getInstance();
        //firebaseUtility = new FirebaseUtility(context);
    }

    @Test
    public void testForEmptyEmail() {
        assertTrue(true);
    }

    @Test
    public void testForCorrectEmail() {
        assertTrue(true);
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
}
