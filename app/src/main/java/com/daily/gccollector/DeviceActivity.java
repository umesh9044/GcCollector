package com.daily.gccollector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daily.gccollector.Utility.AppConstraint;
import com.google.android.material.snackbar.Snackbar;

public class DeviceActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    com.google.android.material.textfield.TextInputLayout txtDeviceNo;
    com.google.android.material.button.MaterialButton btnDeviceSubmit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        setTitle("DEVICE");

        sharedpreferences= getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
        String DeviceNo = sharedpreferences.getString(AppConstraint.PRF_DEVICENO, "");
        if(!DeviceNo.equals(""))
        {
            Intent intent = new Intent(DeviceActivity.this, VehicleActivity.class);
            startActivity(intent);
            finish();
        }
        txtDeviceNo=findViewById(R.id.txtDeviceNo);
        btnDeviceSubmit=findViewById(R.id.btnDeviceSubmit);

        btnDeviceSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String DeviceNo =txtDeviceNo.getEditText().getText().toString().trim();
                if(DeviceNo.length()==0)
                {
                    Snackbar.make(view, "Please Enter Device No First!!", Snackbar.LENGTH_LONG)
                            .setAction("Device No", null).show();
                }
                else
                {
                    SharedPreferences sharedpreferences = getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(AppConstraint.PRF_DEVICENO, DeviceNo);
                    editor.commit();

                    Intent intent = new Intent(DeviceActivity.this, VehicleActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
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
            Intent intent = new Intent(DeviceActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(DeviceActivity.this,"Logout Successfully!!",Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
