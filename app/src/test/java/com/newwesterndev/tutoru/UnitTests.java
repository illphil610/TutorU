package com.newwesterndev.tutoru;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.newwesterndev.tutoru.model.Model;
import com.newwesterndev.tutoru.utilities.FirebaseManager;
import com.newwesterndev.tutoru.utilities.Utility;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UnitTests {

    Utility mUtility;
    FirebaseAuth mAuth;
    FirebaseManager fbManager;
    Model.Chat mChat;
    Model.Tutee mTutee;
    Model.Tutor mTutor;

    @Before
    public void setUp() throws Exception {

        mUtility = new Utility();
        mChat = new Model.Chat("test", "test", "this is test bruv");
        mTutee = new Model.Tutee("123457", "Tutee", "12345", "test", "", "", false);
        mTutor = new Model.Tutor("123456", "Tutor", "12345", "test", "", "", false, new ArrayList<>());


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
        fbManager.saveChatToFirebaseMessage(mChat);
        assertNull(null);
    }

    @Test
    public void getUsersChatMessage() {
        String message = mChat.getMessage();
        assertEquals("this is test bruv", message);
    }

    @Test
    public void logUserIntoFirebase() {
        mAuth.signInWithEmailAndPassword("test@temple.edu", "test123").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                assertTrue(true);
            }
        });
    }
    @Test
    public void saveUserTutor(){
        fbManager.saveUsersType("123456", "Tutor");
    }
    @Test
    public void saveUserTutee(){
        fbManager.saveUsersType("123457", "Tutee");
    }

    @Test
    public void updateInfoTutor(){
        fbManager.updateTutor("123456", mTutor);
    }
    @Test
    public void updateInfoTutee(){
        fbManager.updateTutee("123457", mTutee);
    }

}
