package com.example.googlemaps2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapAcitivity extends AppCompatActivity implements OnMapReadyCallback {



    private static final String TAG = "MapAcitivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String CORSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;

    // OnMapReadycallBack method
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapAcitivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), CORSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    //after getting the permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed.");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission Granted.");
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }


}
