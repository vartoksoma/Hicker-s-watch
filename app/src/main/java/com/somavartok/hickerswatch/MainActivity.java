package com.somavartok.hickerswatch;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView latitudeTextView;
    TextView longitudeTextView;
    TextView accuracyTextView;
    TextView altitudeTextView;
    TextView addressTextView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public List<Double> getAccuracyAltitude() {
        double accuracy = 0;
        double altitude = 0;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            altitude = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAltitude();
            accuracy = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getAccuracy();
        }
        return Arrays.asList(accuracy, altitude);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitudeTextView = findViewById(R.id.LatitudeTextView);
        longitudeTextView = findViewById(R.id.LongitudeTextView);
        accuracyTextView = findViewById(R.id.AccuracyTextView);
        altitudeTextView = findViewById(R.id.AltitudeTextView);
        addressTextView = findViewById(R.id.AddressTextView);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listAddress =geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (listAddress != null && listAddress.size() > 0){
                        latitudeTextView.setText("Latitude: " + Double.toString(listAddress.get(0).getLatitude()));
                        longitudeTextView.setText("Longitude: " + Double.toString(listAddress.get(0).getLongitude()));
                        altitudeTextView.setText("Altitude: " + getAccuracyAltitude().get(0) + " m");
                        accuracyTextView.setText("Accuracy: " + getAccuracyAltitude().get(1) + " m");
                        addressTextView.setText("Address: " + listAddress.get(0).getAddressLine(0));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i("New location: ", location.toString());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }
    }
}
