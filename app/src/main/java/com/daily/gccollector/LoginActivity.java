package com.daily.gccollector;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daily.gccollector.Utility.AppConstraint;
import com.daily.gccollector.Volley.VolleyClient;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {
//Umesh
    com.google.android.material.textfield.TextInputLayout txtUser,txtPassword;
    com.google.android.material.button.MaterialButton btnLogin;
    private SharedPreferences prefSFA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefSFA= getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
        String token = prefSFA.getString(AppConstraint.PRF_TOKEN, "");
        if(!token.equals(""))
        {
            Intent intent = new Intent(LoginActivity.this, DeviceActivity.class);
            startActivity(intent);
            finish();
        }

        txtUser=findViewById(R.id.txtUser);
        txtPassword=findViewById(R.id.txtPassword);
        btnLogin=findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String UserName =txtUser.getEditText().getText().toString().trim();
                String Password =txtPassword.getEditText().getText().toString().trim();

                if(UserName.length()==0 || Password.length()==0)
                {

                    Snackbar.make(view, "Invalid User Or Password!!", Snackbar.LENGTH_LONG)
                            .setAction("Login", null).show();
                }
                else
                {
                    login(UserName,Password);
                }
            }
        });
    }

    private void login(String UserName,String Password) {

        RequestQueue queue = VolleyClient.getInstance(LoginActivity.this).getRequestQueue();
        try {
            JSONObject obj = new JSONObject();
            obj.put("email", "");
            obj.put("username", UserName);
            obj.put("password", Password);
            obj.put("rememberMe", true);
            obj.put("id", "");

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, AppConstraint.POST_LOGIN,obj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try {
                                if(response.getInt("status")==1)
                                {
                                    Toast.makeText(LoginActivity.this,"Login Successfully!!",Toast.LENGTH_LONG).show();

                                    JSONObject data = response.getJSONObject("data");
                                    String token = data.getString("token");

                                    SharedPreferences sharedpreferences = getSharedPreferences(AppConstraint.PRF_LOGINAUTH, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString(AppConstraint.PRF_TOKEN, token);
                                    editor.putString(AppConstraint.PRF_USER, UserName);
                                    editor.commit();


                                    Intent intent = new Intent(LoginActivity.this, DeviceActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this,response.getString("message"),Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                                Toast.makeText(LoginActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(LoginActivity.this,"Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
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
            Toast.makeText(LoginActivity.this, "Error:"+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
