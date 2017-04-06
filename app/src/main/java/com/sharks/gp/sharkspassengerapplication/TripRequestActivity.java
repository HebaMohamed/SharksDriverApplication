package com.sharks.gp.sharkspassengerapplication;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.mtp.MtpConstants;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;
import com.sharks.gp.sharkspassengerapplication.myclasses.MyURL;
import com.sharks.gp.sharkspassengerapplication.myclasses.Passenger;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TripRequestActivity extends AppCompatActivity {

    ImageView mapimg;
    TextView addresstxt, distancetxt, detailstxt, durationtxt, counttxt;
    Button acceptbtn;

    Location currentloc;
    Handler handler; int i, counted;

    Trip trip;

    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_request);
        setTitle("Trip Request");

        mapimg=(ImageView)findViewById(R.id.mapimg);
        addresstxt=(TextView)findViewById(R.id.addresstxt);
        distancetxt=(TextView)findViewById(R.id.distancetxt);
        detailstxt=(TextView)findViewById(R.id.detailstxt);
        durationtxt=(TextView)findViewById(R.id.durationtxt);
        counttxt=(TextView)findViewById(R.id.counttxt);
        acceptbtn=(Button)findViewById(R.id.acceptbtn);

        setuploading();

        try {
            String appstate = MyApplication.getAppState();
            if(!appstate.equals("request")) {//no trip
                startActivity(new Intent(TripRequestActivity.this, MainActivity.class));
            }
            else
            {
                trip = MyApplication.getTripRequest();

                getLocImg(trip.pickup);
                addresstxt.setText(MyApplication.getLocationAddress(trip.pickup));
                getDistanceDuration(trip.pickup);
                detailstxt.setText(trip.details);

                acceptbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // /accepttrip
                        //for test as a result we'll have a trip associated with passenger data
//                        Passenger p =  new Passenger(1,"","Heba","Female",21,24684,248854,"English","h@h.h");
//                        MyApplication.storeTripAcceptance(p);
//                        MyApplication.removeNotifications(1);
//                        //send notification to passenger
//
//                        startActivity(new Intent(TripRequestActivity.this, ArrivingActivity.class));
//                        finish();
                        handler.removeMessages(0);
                        accepttrip(trip.trip_ID,MyApplication.getLoggedDriverID());
                    }
                });

                int diff = getcountertime(trip.request_timestamp).intValue();
                i = diff;  counted = 1;
                //start counter
                handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        if(counted<=60 && counted>0)
                        {
                            i++;
                            counted = 60-i;
                            counttxt.setText(counted+"");

                            handler.postDelayed(this, 1000); // Call it 1 second later
                        }
                        else if (counted == 0 || counted < 0 ) {
                            String appstate = MyApplication.getAppState();
                            if (appstate.equals("request")) {//3shn bysht8l fl background 7ta lw fy activity tnya
//                                counttxt.setText("finished");
                                //remove request
                                MyApplication.beReady();
                                MyApplication.removeNotifications(1);
                                startActivity(new Intent(TripRequestActivity.this, MainMapActivity.class));
                                finish();
                                //send request to ignored tripsto increase count
                            }
                            handler.removeMessages(0);
                        }

                    }
                };
                handler.postDelayed(runnable, 0); // Call it immediatly

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getLocImg(Location loc) throws IOException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String locurl = "http://maps.googleapis.com/maps/api/staticmap?zoom=17&size=360x180&markers=size:mid|color:red|" + loc.getLatitude() + "," + loc.getLongitude() + "&sensor=false";
        URL url = new URL(locurl);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        mapimg.setImageBitmap(bmp);
    }

    void getDistanceDuration(Location loc){
        currentloc = MyApplication.getLastKnownLocation();
        String url = "http://maps.google.com/maps/api/directions/json?origin=" + currentloc.getLatitude() + "," + currentloc.getLongitude()
                                                          + "&destination=" + loc.getLatitude() + "," + loc.getLongitude() + "&sensor=false&units=metric";
        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");
                    JSONObject routes = routeArray.getJSONObject(0);

                    JSONArray newTempARr = routes.getJSONArray("legs");
                    JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

                    JSONObject distOb = newDisTimeOb.getJSONObject("distance");
                    JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

                    distancetxt.setText(distOb.getString("text"));
                    durationtxt.setText(timeOb.getString("text"));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                params.put("latitude", String.valueOf(lat));
//                params.put("longitude", String.valueOf(lng));
                return params;
            }
        };
        // Add the request to the queue
        Volley.newRequestQueue(MyApplication.getAppContext()).add(sr);
    }



    Long getcountertime(Long timestamp){
        Long now = System.currentTimeMillis()/1000;
        Long diff  = now - timestamp;
        return diff;
    }

    void sendTripAcceptance(String channel){

        JSONObject jso = new JSONObject();
        try {
            jso.put("type", "requestresponse");
            jso.put("tripid", trip.trip_ID);
            jso.put("driverid", MyApplication.getLoggedDriverID());
            MyApplication.sendNotificationToChannel(jso,channel);

        } catch (JSONException e) { e.printStackTrace(); }
    }


    void setuploading(){
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Loading. Please wait...");
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }

    void accepttrip(int tripid,int driverid) {
        progress.show();
//        JSONObject toobj = new JSONObject();
//        try {
//            toobj.put("tripid",tripid);
//            toobj.put("driverid",driverid);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        final String requestBody = toobj.toString();

        StringRequest sr = new StringRequest(Request.Method.GET, MyURL.accepttrip+tripid+"/"+driverid, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String msg = obj.getString("msg");
                    int success = Integer.parseInt(obj.getString("success"));

                    //progress.hide();
                    if (success == 1) {
                        JSONObject tripobj = obj.getJSONObject("tripobj");
                        JSONObject passenger = obj.getJSONObject("passenger");


                        Trip t = new Trip(tripobj.getInt("tripid"));
                        t.start_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(tripobj.getString("start"));
                        t.end_Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(tripobj.getString("end"));

//                        t.price = new Double(tripobj.getDouble("price"));
//                        t.comment = new String(tripobj.getString("comment"));
//                        t.rating = new Double(tripobj.getDouble("ratting"));
                        t.p = new Passenger(tripobj.getInt("passenger_id"));
                        t.d = new Driver(tripobj.getInt("driver_id"));

                        sendTripAcceptance("passenger"+t.p.id);

//                        Passenger p =  new Passenger(1,"","Heba","Female",21,24684,248854,"English","h@h.h");
                        Passenger p =  new Passenger(t.p.id);
                        p.fullName=passenger.getString("fullname");
                        p.phone=passenger.getInt("phone");


                        MyApplication.storeTripAcceptance(p);
                        MyApplication.removeNotifications(1);
                        //send notification to passenger

                        progress.hide();
                        startActivity(new Intent(TripRequestActivity.this, ArrivingActivity.class));
                        finish();

                    } else {
                        progress.hide();
                        Toast.makeText(TripRequestActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    progress.hide();
                    Toast.makeText(TripRequestActivity.this, "Something went wrong! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progress.hide();
                Toast.makeText(TripRequestActivity.this, "Something went wrong!" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
