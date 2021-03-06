package com.daily.gccollector;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BinActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private LocationAddressResultReceiver addressResultReceiver;
    private TextView lblCurrentLocation,lblDeviceNo,lblVehicleNo;
    private Location currentLocation;
    private LocationCallback locationCallback;


    Button btnFullBin,btnEmptyBin,btnSubmitBin,btnCheckBin,btnSubmit;
    ImageView FullBinImage,EmptyBinImage;
    String FullBinImageStr,EmptyBinImageStr,longitude,latitude;
    com.google.android.material.textfield.TextInputLayout txtBinRFIDNo;
    public static final int RequestPermissionCode = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin);
        setTitle("BIN");

        sharedpreferences= getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
        String token = sharedpreferences.getString(AppConstraint.PRF_TOKEN, "");
        String DeviceNo = sharedpreferences.getString(AppConstraint.PRF_DEVICENO, "");
        String VehicleNo = sharedpreferences.getString(AppConstraint.PRF_VEHICLENO, "");
        if(token.equals(""))
        {
            Intent intent = new Intent(BinActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        btnFullBin = findViewById(R.id.btnFullBin);
        FullBinImage = findViewById(R.id.FullBinImage);
        btnEmptyBin = findViewById(R.id.btnEmptyBin);
        EmptyBinImage = findViewById(R.id.EmptyBinImage);

        lblDeviceNo = findViewById(R.id.lblDeviceNo);
        lblVehicleNo = findViewById(R.id.lblVehicleNo);
        txtBinRFIDNo = findViewById(R.id.txtBinRFIDNo);
        btnSubmitBin = findViewById(R.id.btnSubmitBin);
        btnCheckBin = findViewById(R.id.btnCheckBin);
        btnSubmit=findViewById(R.id.btnSubmit);

        lblDeviceNo.setText("DeviceNo: "+DeviceNo);
        lblVehicleNo.setText("VehicleNo: "+VehicleNo);

        EnableRuntimePermission();
        btnFullBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
            }
        });
        btnEmptyBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 8);
            }
        });
        btnCheckBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String BinNo =txtBinRFIDNo.getEditText().getText().toString().trim();
                if(BinNo.length()==0)
                {
                    Toast.makeText(BinActivity.this,"Please Enter/Scan RFID NO First!!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    BinNoIsValid(BinNo);
                }
            }
        });
        btnSubmitBin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String BinNo =txtBinRFIDNo.getEditText().getText().toString().trim();
                if(BinNo.length()==0)
                {
                    Toast.makeText(BinActivity.this,"Please Enter/Scan RFID NO First!!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //if(BinNoIsValid(BinNo))
                    {
                        SaveBin();
                    }
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BinActivity.this, VehicleActivitySubmit.class);
                startActivity(intent);
            }
        });

        addressResultReceiver = new LocationAddressResultReceiver(new Handler());
        lblCurrentLocation = findViewById(R.id.lblCurrentLocation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                longitude = String.valueOf(currentLocation.getLongitude());
                latitude = String.valueOf(currentLocation.getLatitude());

                //Toast.makeText(BinActivity.this,"longitude:"+longitude+" latitude:"+latitude,Toast.LENGTH_LONG).show();
                getAddress();
            }
        };
        startLocationUpdates();
    }

    private Boolean BinNoIsValid(String BinNo) {
        final Boolean[] IsValid = {false};
        RequestQueue queue = VolleyClient.getInstance(BinActivity.this).getRequestQueue();
        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, AppConstraint.GET_BIN_DTL+"?BinRFID="+BinNo,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try {
                                if(response.getInt("status")==1)
                                {
                                    Toast.makeText(BinActivity.this,"Bin is Exist!!",Toast.LENGTH_LONG).show();
                                    IsValid[0] =true;
                                    SharedPreferences sharedpreferences = getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(AppConstraint.PRF_BINNO, BinNo);
                                    editor.commit();
                                }
                                else
                                {
                                    Toast.makeText(BinActivity.this,response.getString("message"),Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                Toast.makeText(BinActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(BinActivity.this,"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
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
            Toast.makeText(BinActivity.this, "Error:"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return IsValid[0];
    }

    private void SaveBin() {
        RequestQueue queue = VolleyClient.getInstance(BinActivity.this).getRequestQueue();
        try {
            JSONObject obj = new  JSONObject();
            obj.put("id", 0);
            obj.put("deviceNo", sharedpreferences.getString(AppConstraint.PRF_DEVICENO, ""));
            obj.put("vehicleNo", sharedpreferences.getString(AppConstraint.PRF_VEHICLENO, ""));
            obj.put("userID", sharedpreferences.getString(AppConstraint.PRF_USER, ""));
            obj.put("binRFID", sharedpreferences.getString(AppConstraint.PRF_BINNO, ""));
            obj.put("emptyImage", EmptyBinImageStr);
            obj.put("fullImage", FullBinImageStr);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("location", lblCurrentLocation.getText());

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AppConstraint.POST_BIN,obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try {
                                if(response.getInt("status")==1)
                                {
                                    Toast.makeText(BinActivity.this,"Record Saved!!",Toast.LENGTH_LONG).show();

                                    SharedPreferences sharedpreferences = getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(AppConstraint.PRF_BINNO, "");
                                    editor.commit();

                                    Intent intent = new Intent(BinActivity.this, BinActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(BinActivity.this,response.getString("message"),Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                Toast.makeText(BinActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(BinActivity.this,"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
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
            Toast.makeText(BinActivity.this, "Error:"+ex.getMessage(), Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(BinActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(BinActivity.this,"Logout Successfully!!",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //Camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7 && resultCode == RESULT_OK)
        {
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            FullBinImage.setImageBitmap(bm);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
            byte[] imgbyte = baos.toByteArray();
            FullBinImageStr = Base64.encodeToString(imgbyte, Base64.DEFAULT);
        }
        else if (requestCode == 8 && resultCode == RESULT_OK)
        {
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            EmptyBinImage.setImageBitmap(bm);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
            byte[] imgbyte = baos.toByteArray();
            EmptyBinImageStr = Base64.encodeToString(imgbyte, Base64.DEFAULT);
        }
    }
    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(BinActivity.this,
                Manifest.permission.CAMERA)) {
            Toast.makeText(BinActivity.this,"CAMERA permission allows us to Access CAMERA app",     Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(BinActivity.this,new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);
        }
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
                    Toast.makeText(BinActivity.this, "Permission Canceled, Now your application cannot access CAMERA.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(BinActivity.this, "Can't find current address, ",
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
                Toast.makeText(BinActivity.this, "Address not found, ", Toast.LENGTH_SHORT).show();
            }
            String currentAdd = resultData.getString("address_result");
            showResults(currentAdd);
        }
    }
    private void showResults(String currentAdd) {
        lblCurrentLocation.setText(currentAdd);
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
