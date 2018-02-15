package com.mapotempo.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.mapotempo.lib.MapotempoApplication;

/**
 * BaseMapotempoAppCompatActivity
 * This class ensure security for lukewarm and warm start.
 * If manager wasn't set in application context, application automatically switch into login activity.
 */
public abstract class BaseMapotempoAppCompatActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (((MapotempoApplication) getApplicationContext()).getManager() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
