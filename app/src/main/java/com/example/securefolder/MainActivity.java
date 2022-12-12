package com.example.securefolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnSettings_onClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void btnReadSettings_onClick(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sSetting = prefs.getString("password","xxx");

        Toast.makeText(this, sSetting, Toast.LENGTH_SHORT).show();
    }

    public void loginPage_onClick(View view) {
        Intent intentLogin = new Intent(this, loginPage.class);
        startActivity(intentLogin);
    }
}