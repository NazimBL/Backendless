package com.example.dell.listviewapp;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.push.BackendlessPushService;
import com.backendless.servercode.IBackendlessService;

/**
 * Created by DELL on 18/09/2016.
 */



public class MyPushService extends BackendlessPushService {

    @Override
    public void onRegistered( Context context, String registrationId )
    {
        Toast.makeText( context, "device registered" + registrationId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnregistered( Context context, Boolean unregistered )
    {
        Toast.makeText( context, "device unregistered", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMessage( Context context, Intent intent )
    {
        String message = intent.getStringExtra( "message" );
        Toast.makeText( context, "Push message received. Message: " + message, Toast.LENGTH_LONG ).show();
        return true;
    }

    @Override
    public void onError( Context context, String message )
    {
        Toast.makeText( context, message, Toast.LENGTH_SHORT).show();
    }

}
