package com.sharks.gp.sharkspassengerapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sharks.gp.sharkspassengerapplication.myclasses.MyURL;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TripEndActivity extends AppCompatActivity {


    public static double tripcost;
    public static double distance;
    public static double distancecost;
    public static double durationcost;


    TextView costtxt, stxt, dtxt, distancetxt, distancecosttxt, durationtxt, durationcosttxt;
    Button donebtn;

    ProgressDialog progress;

    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);
        setTitle("End Trip");
        setuploading();
        costtxt=(TextView) findViewById(R.id.costtxt);
        stxt=(TextView) findViewById(R.id.stxt);
        dtxt=(TextView) findViewById(R.id.dtxt);
        distancetxt=(TextView) findViewById(R.id.distancetxt);//
        distancecosttxt=(TextView) findViewById(R.id.distancecosttxt);//
        durationtxt=(TextView) findViewById(R.id.durationtxt);
        durationcosttxt=(TextView) findViewById(R.id.durationcosttxt);
        donebtn=(Button)findViewById(R.id.donebtn);

        try {
            trip=MyApplication.getTripRequest();
            stxt.setText(MyApplication.getLocationAddress(trip.pickup));
            dtxt.setText(MyApplication.getLocationAddress(trip.destination));
        } catch (Exception e) {
            e.printStackTrace();
        }


        calculatecost();

        MyApplication.endRequest();

        /// /donetrip and get data into here //fill data
        donebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.beReady();
                startActivity(new Intent(TripEndActivity.this, MainMapActivity.class));
                finish();
            }
        });
    }


    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }



    void calculatecost() {
        progress.show();
        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.donetrip + "/" + trip.trip_ID, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    if (success == 1) {
                        distance = obj.getDouble("distance");
                        distancecost = obj.getDouble("distancecost");


                        distancetxt.setText(Double.toString(distance));
                        distancecosttxt.setText(Double.toString(distancecost));
                        durationcost=0;//testtt;
                        tripcost=durationcost+distancecost;
                        costtxt.setText(tripcost+"$");
                        progress.hide();

                    } else {
                        progress.hide();
                        Toast.makeText(TripEndActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                     progress.hide();
                    Toast.makeText(TripEndActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(TripEndActivity.this, "Something went wrong!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return String.format("application/x-www-form-urlencoded; charset=utf-8");
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                params.put("Content-Type","application/json");
                return params;
            }
        };

        sr.setRetryPolicy(new DefaultRetryPolicy(100000, 10, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Add the request to the queue
        Volley.newRequestQueue(this).add(sr);
    }

}
