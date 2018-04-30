package com.newwesterndev.tutoru;

import android.content.Context;
import android.location.LocationManager;

import com.newwesterndev.tutoru.utilities.LocationProxy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProxyTests {

    LocationProxy lp;
    LocationManager lm;
    Context context;

    @Before
    public void setUp(){
        context = Mockito.mock(Context.class);
        lm = Mockito.mock(LocationManager.class);
        lp = new LocationProxy(context, lm);
    }

    @Test
    public void testForLocation(){
        lp.getUsersLocation();
    }
    @Test
    public void cancelLocationRequests(){
        lp.cancelUsersLocationRequest();
    }
    @Test
    public void getLatitude(){
        lp.getUsersLocation().getLatitude();
    }
    @Test
    public void getLongitude(){
        lp.getUsersLocation().getLongitude();
    }

}
