package com.example.googlemaps2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isServiceOK()){
            init();
        }
    }

    private void init(){
        // Button for Map
        Button btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapAcitivity.class);
                startActivity(intent);
            }
        });

        // Button for AutoSearch
        Button btnAuto = (Button) findViewById(R.id.btnAuto);
        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AutoComplete.class);
                startActivity(intent);
            }
        });

        // Button for current location
        Button btnCurLocation = (Button) findViewById(R.id.btnCurrentLocation);
        btnCurLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, currentLocation.class);
                startActivity(intent);
            }
        });


        //Button for Calculate distance
        Button btnCalDistance = (Button) findViewById(R.id.btn_CalDistance);
        btnCalDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalculateDistance.class);
                startActivity(intent);
            }
        });

        //Button for displaying retrofit example
        Button btnRetrofit = (Button) findViewById(R.id.btn_Retrofit);
        btnRetrofit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SimpleRetrofit.class);
                startActivity(intent);
            }
        });

    }

    // check 'google service' version is up-to-date for cellphone
    public boolean isServiceOK() {
        Log.d(TAG, "isServiceOK: checking google service version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServiceOK: Goolge Play Service is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // an error occured but we can resolved it
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
        }else {
            Toast.makeText(this, "You can't make requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}