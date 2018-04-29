package com.newwesterndev.tutoru;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.newwesterndev.tutoru.model.Model;
import com.newwesterndev.tutoru.utilities.FirebaseManager;
import com.newwesterndev.tutoru.utilities.Utility;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class AuthUnitTest {

    Utility mUtility;
    FirebaseManager manager;
    FirebaseAuth mAuth;



    @Before
    @PrepareForTest(FirebaseAuth.class)
    public void setUp() throws Exception {

        mAuth = Mockito.mock(FirebaseAuth.class);
        PowerMockito.mockStatic(FirebaseAuth.class);
        when(FirebaseAuth.getInstance()).thenReturn(mAuth);

    }

    @Test
    @PrepareForTest(FirebaseAuth.class)
    public void testForLogin() {
        mAuth.signInWithEmailAndPassword("tuf41165@temple.edu", "lee052996");
    }


}
