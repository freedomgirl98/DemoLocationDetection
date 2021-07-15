package sg.edu.rp.c346.id19036308.demolocationdetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button btnLastLocation, btnReceiveLocationUpdates, btnRemoveLocationUpdates;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLastLocation = findViewById(R.id.btnGetLastLocation);
        btnReceiveLocationUpdates = findViewById(R.id.btnGetLocationUpdate);
        btnRemoveLocationUpdates = findViewById(R.id.btnRemoveLocationUpdate);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);



        btnLastLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()){
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                String msg = "Lat : " + location.getLatitude() + " Lng : " + location.getLongitude();
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                String msg = "No Last Known Location Found";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnReceiveLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()) {

                    mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    mLocationRequest.setInterval(10000);
                    mLocationRequest.setFastestInterval(5000);
                    mLocationRequest.setSmallestDisplacement(100);

                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult){
                            if (locationResult != null){
                                Location data = locationResult.getLastLocation();
                                double lat = data.getLatitude();
                                double lng = data.getLongitude();

                                Toast.makeText(MainActivity.this, "New Location Detected\n" + "Lat: " + lat + "lng: " + lng, Toast.LENGTH_SHORT).show();
                            }
                        }

                    };
                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                }

            }

        });

        btnRemoveLocationUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove updates
                client.removeLocationUpdates(mLocationCallback);
            }
        });



    }

    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION
        );
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION
        );
        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}