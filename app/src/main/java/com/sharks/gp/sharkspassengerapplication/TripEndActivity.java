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
import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;
import com.sharks.gp.sharkspassengerapplication.myclasses.MyURL;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TripEndActivity extends AppCompatActivity {


    public static double tripcost;
    public static double distance;
    public static double distancecost;
    public static double durationcost;
    double lastwallet;


//    TextView costtxt, stxt, dtxt, distancetxt, distancecosttxt, durationtxt, durationcosttxt;
    TextView kmtxt,costtxt,stxt,dtxt;
    Button donebtn;

    TextView p1txt,p2txt,p3txt,p4txt,p5txt,p6txt,p7txt,p8txt,p9txt,p10txt,p11txt,p12txt;
    TextView np1txt,np2txt,np3txt,np4txt,np5txt,np6txt,np7txt,np8txt,np9txt,np10txt,np11txt,np12txt;

    ProgressDialog progress;
    BarChart mBarChart;

    Trip trip;
    int vehicleid;
    Driver d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);
        setTitle("End Trip");
        setuploading();
        costtxt=(TextView) findViewById(R.id.costtxt);
        stxt=(TextView) findViewById(R.id.stxt);
        dtxt=(TextView) findViewById(R.id.dtxt);

        mBarChart = (BarChart) findViewById(R.id.barchart);
//        distancetxt=(TextView) findViewById(R.id.distancetxt);//
//        distancecosttxt=(TextView) findViewById(R.id.distancecosttxt);//
//        durationtxt=(TextView) findViewById(R.id.durationtxt);
//        durationcosttxt=(TextView) findViewById(R.id.durationcosttxt);
        donebtn=(Button)findViewById(R.id.donebtn);

        kmtxt=(TextView) findViewById(R.id.kmtxt);


        p1txt=(TextView) findViewById(R.id.p1txt);
        p2txt=(TextView) findViewById(R.id.p2txt);
        p3txt=(TextView) findViewById(R.id.p3txt);
        p4txt=(TextView) findViewById(R.id.p4txt);
        p5txt=(TextView) findViewById(R.id.p5txt);
        p6txt=(TextView) findViewById(R.id.p6txt);
        p7txt=(TextView) findViewById(R.id.p7txt);
        p8txt=(TextView) findViewById(R.id.p8txt);
        p9txt=(TextView) findViewById(R.id.p9txt);
        p10txt=(TextView) findViewById(R.id.p10txt);
        p11txt=(TextView) findViewById(R.id.p11txt);
        p12txt=(TextView) findViewById(R.id.p12txt);

        np1txt=(TextView) findViewById(R.id.np1txt);
        np2txt=(TextView) findViewById(R.id.np2txt);
        np3txt=(TextView) findViewById(R.id.np3txt);
        np4txt=(TextView) findViewById(R.id.np4txt);
        np5txt=(TextView) findViewById(R.id.np5txt);
        np6txt=(TextView) findViewById(R.id.np6txt);
        np7txt=(TextView) findViewById(R.id.np7txt);
        np8txt=(TextView) findViewById(R.id.np8txt);
        np9txt=(TextView) findViewById(R.id.np9txt);
        np10txt=(TextView) findViewById(R.id.np10txt);
        np11txt=(TextView) findViewById(R.id.np11txt);
        np12txt=(TextView) findViewById(R.id.np12txt);


        try {
            trip=MyApplication.getTripRequest();
            vehicleid=MyApplication.getLoggedDriverVehicleID();
            d=MyApplication.getLoggedDriver();
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
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("status").setValue("ended");
                MyApplication.myFirebaseRef.child("driver").child(String.valueOf(d.id)).child("wallet").setValue(lastwallet);

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
        StringRequest sr = new StringRequest(Request.Method.GET, MyURL.donetrip + trip.trip_ID + "/" + vehicleid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    if (success == 1) {
                        distance = obj.getDouble("distance");
                        distancecost = obj.getDouble("distancecost");
                        lastwallet = obj.getDouble("lastwallet");

                        JSONObject pattrenobj = obj.getJSONObject("pattrenobj");
                        p1txt.setText(pattrenobj.getString("p1"));
                        p2txt.setText(pattrenobj.getString("p2"));
                        p3txt.setText(pattrenobj.getString("p3"));
                        p4txt.setText(pattrenobj.getString("p4"));
                        p5txt.setText(pattrenobj.getString("p5"));
                        p6txt.setText(pattrenobj.getString("p6"));
                        p7txt.setText(pattrenobj.getString("p7"));
                        p8txt.setText(pattrenobj.getString("p8"));
                        p9txt.setText(pattrenobj.getString("p9"));
                        p10txt.setText(pattrenobj.getString("p10"));
                        p11txt.setText(pattrenobj.getString("p11"));
                        p12txt.setText(pattrenobj.getString("p12"));

                        JSONObject pattrennames = obj.getJSONObject("pattrennames");
                        np1txt.setText("(1) "+pattrennames.getString("p1"));
                        np2txt.setText("(2) "+pattrennames.getString("p2"));
                        np3txt.setText("(3) "+pattrennames.getString("p3"));
                        np4txt.setText("(4) "+pattrennames.getString("p4"));
                        np5txt.setText("(5) "+pattrennames.getString("p5"));
                        np6txt.setText("(6) "+pattrennames.getString("p6"));
                        np7txt.setText("(7) "+pattrennames.getString("p7"));
                        np8txt.setText("(8) "+pattrennames.getString("p8"));
                        np9txt.setText("(9) "+pattrennames.getString("p9"));
                        np10txt.setText("(10)"+pattrennames.getString("p10"));
                        np11txt.setText("(11)"+pattrennames.getString("p11"));
                        np12txt.setText("(12)"+pattrennames.getString("p12"));


                        mBarChart.addBar(new BarModel("1",  (float)pattrenobj.getInt("p1") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("2",  (float)pattrenobj.getInt("p2") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("3",  (float)pattrenobj.getInt("p3") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("4",  (float)pattrenobj.getInt("p4") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("5",  (float)pattrenobj.getInt("p5") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("6",  (float)pattrenobj.getInt("p6") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("7",  (float)pattrenobj.getInt("p7") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("8",  (float)pattrenobj.getInt("p8") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("9",  (float)pattrenobj.getInt("p9") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("10",  (float)pattrenobj.getInt("p10") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("11",  (float)pattrenobj.getInt("p11") ,  0xFF343456));
                        mBarChart.addBar(new BarModel("12",  (float)pattrenobj.getInt("p12") ,  0xFF343456));
                        mBarChart.startAnimation();
//                        distancetxt.setText(Double.toString(distance)+"Km");
//                        distancecosttxt.setText(Double.toString(distancecost)+"$");
                        kmtxt.setText(Double.toString(distance)+"Km");
                        costtxt.setText(distancecost+"$");

                        lastwallet+=distancecost;

//                        durationcost=0;//testtt;
//                        tripcost=durationcost+distancecost;
//                        costtxt.setText(tripcost+"$");
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
