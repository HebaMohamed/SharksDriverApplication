package com.sharks.gp.sharkspassengerapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;
import com.sharks.gp.sharkspassengerapplication.myclasses.MyURL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText driveridtxt, driverpasstxt;
    Button loginbtn;
    ProgressDialog progress;

    Driver d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        setuploading();

        driveridtxt=(EditText)findViewById(R.id.driveridtxt);
        driverpasstxt=(EditText)findViewById(R.id.driverpasstxt);
        loginbtn=(Button)findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check()) {
                    //example login
//                    try {
//                        d = new Driver(1, "harry", "h@h.h", "", "");//test
//                        MyApplication.storeLogin(d);
//                        startActivity(new Intent(LoginActivity.this, MainMapActivity.class));
//                        finish();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    int id = Integer.parseInt(driveridtxt.getText().toString());
                    String pass = driverpasstxt.getText().toString();
                    d = new Driver(id);
                    d.password=pass;
                    sendlogin();
                }
            }
        });

    }

    boolean check() {
        String id = driveridtxt.getText().toString();
        String pass = driverpasstxt.getText().toString();
        if (id.equals("") || pass.equals(""))
            return false;
        else
            return true;
    }


    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }

    void sendlogin(){
        JSONObject toobj = new JSONObject();
        try {
            toobj.put("driver_id",d.id);
            toobj.put("password",d.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = toobj.toString();

        progress.show();
        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.login , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    progress.hide();
                    if(success==1){
//                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

                        JSONObject driver = obj.getJSONObject("driver");
                        String fullname = driver.getString("fullname");
                        d.name=fullname;
                        Toast.makeText(LoginActivity.this, "Welcome "+fullname, Toast.LENGTH_LONG).show();

                        MyApplication.storeLogin(d);
                        startActivity(new Intent(LoginActivity.this, MainMapActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(LoginActivity.this, "Something went wrong! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(LoginActivity.this, "Something went wrong!"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("driver_id", String.valueOf(d.id));
//                params.put("password",d.password);
//                return params;
//            }
            @Override
            public String getBodyContentType() {
                return String.format("application/x-www-form-urlencoded; charset=utf-8");
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            requestBody, "utf-8");
                    return null;
                }
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Content-Type","application/json");
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy( 100000, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        sr.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the queue
        Volley.newRequestQueue(this).add(sr);

    }

}
