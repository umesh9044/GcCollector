package com.daily.gccollector;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daily.gccollector.Services.GetAddressIntentService;
import com.daily.gccollector.Utility.AppConstraint;
import com.daily.gccollector.Volley.VolleyClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class VehicleActivitySubmit extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    TextInputLayout txtFuel,txtEndMtrRd,txtEndLat,txtEndLng,txtEndLocation;
    MaterialButton btnVehicleSubmit;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private Location currentLocation;
    private LocationCallback locationCallback;
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_submit);
        setTitle("Close Scanning");

        sharedpreferences= getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
        String VehicleNo = sharedpreferences.getString(AppConstraint.PRF_VEHICLENO, "");
        if(VehicleNo.equals(""))
        {
            Intent intent = new Intent(VehicleActivitySubmit.this, VehicleActivity.class);
            startActivity(intent);
            finish();
        }
        txtFuel=findViewById(R.id.txtFuel);
        txtEndMtrRd=findViewById(R.id.txtEndMtrRd);
        txtEndLat=findViewById(R.id.txtEndLat);
        txtEndLng=findViewById(R.id.txtEndLng);
        txtEndLocation=findViewById(R.id.txtEndLocation);

        btnVehicleSubmit=findViewById(R.id.btnVehicleSubmit);
        btnVehicleSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VehicleSubmit(VehicleNo);
            }
        });




        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                String longitude = String.valueOf(currentLocation.getLongitude());
                String latitude = String.valueOf(currentLocation.getLatitude());

                txtEndLng.getEditText().setText(longitude);
                txtEndLat.getEditText().setText(latitude);
                //Toast.makeText(BinActivity.this,"longitude:"+longitude+" latitude:"+latitude,Toast.LENGTH_LONG).show();
                getAddress();
            }
        };
        startLocationUpdates();
    }

    private void VehicleSubmit(String VehicleNo) {
        RequestQueue queue = VolleyClient.getInstance(VehicleActivitySubmit.this).getRequestQueue();
        try {

            JSONObject obj = new JSONObject();
            obj.put("id", 0);
            obj.put("deviceNo", sharedpreferences.getString(AppConstraint.PRF_DEVICENO, ""));
            obj.put("vehicleNo", VehicleNo);
            obj.put("userID",sharedpreferences.getString(AppConstraint.PRF_USER, ""));
            if(txtEndMtrRd.getEditText().getText().length()>0) {
                obj.put("endReading",Double.valueOf(txtEndMtrRd.getEditText().getText().toString()));
            }
            obj.put("endLatitude", txtEndLat.getEditText().getText());
            obj.put("endLongitude", txtEndLng.getEditText().getText());
            obj.put("endLocation", txtEndLocation.getEditText().getText());
            if(txtFuel.getEditText().getText().length()>0) {
                obj.put("fuelConsumption", Double.valueOf(txtFuel.getEditText().getText().toString()));
            }


            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AppConstraint.POST_VEHICLE_DTL,obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try {
                                if(response.getInt("status")==1)
                                {
                                    Toast.makeText(VehicleActivitySubmit.this,"Record Saved.. You Can Logout!!",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(VehicleActivitySubmit.this,response.getString("message"),Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                Toast.makeText(VehicleActivitySubmit.this,ex.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(VehicleActivitySubmit.this,"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsObjRequest);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Toast.makeText(VehicleActivitySubmit.this, "Error:"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {
            SharedPreferences sharedpreferences = getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(AppConstraint.PRF_TOKEN, "");
            editor.putString(AppConstraint.PRF_USER, "");
            editor.putString(AppConstraint.PRF_DEVICENO, "");
            editor.putString(AppConstraint.PRF_VEHICLENO, "");
            editor.commit();
            Intent intent = new Intent(VehicleActivitySubmit.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(VehicleActivitySubmit.this,"Logout Successfully!!",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] result) {
        super.onRequestPermissionsResult(requestCode, permissions, result);
        switch (requestCode) {
            case RequestPermissionCode:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(BinActivity.this, "Permission Granted, Now your application can access CAMERA.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(VehicleActivitySubmit.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
                }
                break;
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                }
                else {
                    Toast.makeText(this, "Location permission not granted, " + "restart the app if you want the feature", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    // Location
    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new
                            String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(VehicleActivitySubmit.this, "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        startService(intent);
    }
    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }
            if (resultCode == 1) {
                Toast.makeText(VehicleActivitySubmit.this, "Address not found, ", Toast.LENGTH_SHORT).show();
            }
            String currentAdd = resultData.getString("address_result");
            showResults(currentAdd);
        }
    }
    private void showResults(String currentAdd) {
        txtEndLocation.getEditText().setText(currentAdd);
    }
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }
    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

}
