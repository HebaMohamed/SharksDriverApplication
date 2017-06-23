package com.sharks.gp.sharkspassengerapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.sharks.gp.sharkspassengerapplication.myclasses.Vehicle;

import org.json.JSONArray;
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

//                    int id = Integer.parseInt(driveridtxt.getText().toString());
//                    String pass = driverpasstxt.getText().toString();
//                    d = new Driver(id);
//                    d.password=pass;
//                    sendlogin();
                    createAndShowAlertDialog();
                }
            }
        });

    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Make sure that you LOGGED IN before your vehicle powered ON !");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                int iid = Integer.parseInt(driveridtxt.getText().toString());
                String pass = driverpasstxt.getText().toString();
                d = new Driver(iid);
                d.password=pass;
                sendlogin();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
                        String img = driver.getString("img");

                        //get ristricted route
                        JSONArray ristrictions = driver.getJSONArray("route");
                        for (int i = 0; i < ristrictions.length(); i++) {
                            d.restrictedLats.add(ristrictions.getJSONObject(i).getDouble("lat"));
                            d.restrictedLngs.add(ristrictions.getJSONObject(i).getDouble("lng"));
                        }

//                        int vehicle_id = Integer.valueOf(driver.getString("vehicle_id"));
                        d.name = fullname;
                        d.image = img;

                        JSONObject vehicle = obj.getJSONObject("vehicle");
                        int vehicle_id = Integer.valueOf(vehicle.getString("vehicle_id"));
                        String color = vehicle.getString("color");
                        String plate_number = vehicle.getString("plate_number");
                        String model = vehicle.getString("model");

                        d.vehicle=new Vehicle(vehicle_id);
                        d.vehicle.Color = color;
                        d.vehicle.Plate_number = plate_number;
                        d.vehicle.Model = model;

                        Toast.makeText(LoginActivity.this, "Welcome "+fullname, Toast.LENGTH_LONG).show();

                        MyApplication.storeLogin(d);

                        MyApplication.myFirebaseRef.child("driver").child(String.valueOf(d.id)).child("logged").setValue("true");

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
