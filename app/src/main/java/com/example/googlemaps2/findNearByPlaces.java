package com.example.googlemaps2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class findNearByPlaces extends AppCompatActivity {

    // Initialise vars
    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat = 0, currentLon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_near_by_places);

        //Assign vars
        spType = findViewById(R.id.sp_type);
        btFind = findViewById(R.id.bt_find);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map_find );

        //Initialize array of place type
        String[] placeTypeList = {"atm", "bank", "hospital", "movie_theater", "restaurant"};
        //Initialize array of place name
        String[] placeNameList = {"ATM", "Bank", "Hospital", "Movie Theater", "Restaurant"};

        //Set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(findNearByPlaces.this,
                android.R.layout.simple_spinner_item, placeNameList));

        // Initialise fused location provider client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //Check permission
        if (ActivityCompat.checkSelfPermission(findNearByPlaces.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted -- call method
            getCurrentLocation();
        } else {
            //When permission denied -- request permission
            ActivityCompat.requestPermissions(findNearByPlaces.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 40);
        }

        btFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get selected position of spinner
                int i = spType.getSelectedItemPosition();
                //initialise url
                String url = "http://maps.googleapis.com/maps/api/place/nearbysearch/json" + //Base Url
                        "?location" + currentLat + "," + currentLon + // query -- Location latitude and longitude
                        "&radius = 5000" + //Nearby radius
                        "&types=" + placeTypeList[i] + //Place type
                        "&sensor = true" + //Sensor
                        "&key" + getResources().getString(R.string.google_maps_API_key);

                //Execute place task method to download json data
                new PlaceTask().execute(url);
            }
        });

    }

    private void getCurrentLocation() {
        // Initialize task location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map = googleMap;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat,currentLon), 10));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 40) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //When permission granted -- call method
                getCurrentLocation();
            }
        }
    }

    //Execute place task method to download json data
    private class PlaceTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                //Initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Execute parse task
            new ParserTask().execute(s);
        }
    }

    private String downloadUrl(String string) throws IOException {
        //initialise url
        URL url = new URL(string);
        //Initialise connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();
        // Initialize input stream
        InputStream stream = connection.getInputStream();
        //Initialise buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //Initialise string builder
        StringBuilder builder = new StringBuilder();
        //Initialise string variable
        String line = "";
        //Use while loop
        while ((line =reader.readLine()) !=null){
            //Append line
            builder.append(line);
        }
        //Get append data
        String data = builder.toString();
        //CLose reader
        reader.close();
        //Return data
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>>{

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //Create json parser class
            JasonParser jsonParser = new JasonParser();
            //Initialise hash map list
            List<HashMap<String, String>> mapList = null;
            JSONObject object = null;

            try {
                //Initialize json object
                object = new JSONObject(strings[0]);
                //Parse json object
                mapList = jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Return map list
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            super.onPostExecute(hashMaps);
            //Clear map
            map.clear();
            // Use for loop
            for (int i=0; i<hashMaps.size(); i++){
                //Initialize hash map
                HashMap<String, String> hashMapList = hashMaps.get(i);
                //Get latitude
                double lat = Double.parseDouble(hashMapList.get("lat"));
                //Get longitude
                double lng = Double.parseDouble(hashMapList.get("lng"));
                //Get name
                String name = hashMapList.get("name");
                //Concat latitude and longitude
                LatLng latLng = new LatLng(lat, lng);
                // Initialize marker options
                MarkerOptions options = new MarkerOptions();
                //Set position
                options.position(latLng);
                //Set title
                options.title(name);
                //Add marker on map
                map.addMarker(options);
            }
        }
    }
}













