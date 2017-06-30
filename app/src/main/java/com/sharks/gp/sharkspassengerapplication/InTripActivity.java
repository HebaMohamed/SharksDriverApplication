package com.sharks.gp.sharkspassengerapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.devspark.appmsg.AppMsg;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.LatLngInterpolator;
import com.sharks.gp.sharkspassengerapplication.myclasses.MyURL;
import com.sharks.gp.sharkspassengerapplication.myclasses.Passenger;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;


public class InTripActivity  extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Marker drivermarker;
    //testtt
    int driverid;// = 1;

    TextView addresstxt, disttxt, durationtxt;
    Button endbtn;
    CircleButton chatbtn, navbtn;
    ImageView alertcircle;
    Trip trip;
    Passenger passenger;

    int vid;

    static long lastpattrenscount = 0;
    int f = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_trip);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addresstxt = (TextView)findViewById(R.id.addresstxt);
        disttxt = (TextView)findViewById(R.id.disttxt);
        durationtxt = (TextView)findViewById(R.id.durationtxt);
        endbtn = (Button) findViewById(R.id.endbtn);
        chatbtn = (CircleButton)findViewById(R.id.chatbtn);
        navbtn = (CircleButton)findViewById(R.id.navbtn);
        alertcircle = (ImageView)findViewById(R.id.alertcircle);

        driverid=MyApplication.getLoggedDriverID();

        try {
            trip = MyApplication.getTripRequest();
            passenger = MyApplication.getTripPassenger();
            vid = MyApplication.getLoggedDriverVehicleID();

//            addresstxt.setText(MyApplication.getLocationAddress(trip.destination));


            navbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Location s = MyApplication.getLastKnownLocation();
                    Location d = trip.destination;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+s.getLatitude()+","+s.getLongitude()+"&daddr="+d.getLatitude()+","+d.getLongitude()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER );
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });
            endbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createAndShowAlertDialog();
                }
            });
            chatbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(InTripActivity.this, TalkActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        //listen to talk notification
        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_TRIPS).child(String.valueOf(trip.trip_ID)).child("talk").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(f==0)
                    f=1;//3shn yb2a mra w7da bs
                else
                    alertcircle.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ll = new LatLng(trip.destination.getLatitude(),trip.destination.getLongitude());
        mMap.addMarker(new MarkerOptions().position(ll).title("Destination"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));//first only
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

//        Location loc = MyApplication.getLastKnownLocation();
        LatLng mll = new LatLng(0,0);//(loc.getLatitude(),loc.getLongitude());//for test onlyyy //ay location we hyt8yr lma ysm3
        drivermarker =  mMap.addMarker(new MarkerOptions()
                .position(mll)
                .title("My Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark)));
        //hat l dist wl duration mo2ktn
//        getDistanceDuration(ll);

        //get expected route
        //testtt
//        ArrayList<LatLng> lll = new ArrayList<>();
//        lll.add(new LatLng(30.133177,31.019540));
//        lll.add(new LatLng(30.133177,31.2019540));
//        drawPolyLineOnMap(lll);
//        getDirections(loc.getLatitude(),loc.getLongitude());

        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_VEHICLES).child(String.valueOf(vid)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double lat = dataSnapshot.child("Latitude").getValue(Double.class);
                double lng = dataSnapshot.child("Longitude").getValue(Double.class);

                //add to pathway
                long ts = System.currentTimeMillis();
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("pathway").child(String.valueOf(ts)).child("lat").setValue(lat);
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("pathway").child(String.valueOf(ts)).child("lng").setValue(lng);


                animateMarkerToGB(drivermarker,new LatLng(lat, lng));
                getDirections(lat,lng);

                //for pattrens detection
                if(lastpattrenscount == 0)//init
                    lastpattrenscount = dataSnapshot.child("Patterns detected").getChildrenCount();
                else{
                    if(dataSnapshot.child("Patterns detected").getChildrenCount() > lastpattrenscount){//there is a new pattren !
                        AppMsg.makeText(InTripActivity.this, "New Pattren Detected!", AppMsg.STYLE_ALERT).show();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        //listen to my wehicle location
//        try {
//            pubnub.subscribe(AppConstants.CHANNEL_PartnersLocation, new Callback() {
//                        @Override
//                        public void connectCallback(String channel, Object message) {
//                        }
//
//                        @Override
//                        public void disconnectCallback(String channel, Object message) {
//                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
//                                    + " : " + message.getClass() + " : "
//                                    + message.toString());
//                        }
//
//                        public void reconnectCallback(String channel, Object message) {
//                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
//                                    + " : " + message.getClass() + " : "
//                                    + message.toString());
//                        }
//
//                        @Override
//                        public void successCallback(String channel, Object message) { //l msg bttst2bl hna
//                            System.out.println("SUBSCRIBE : " + channel + " : "
//                                    + message.getClass() + " : " + message.toString());
//
//                            try {
//                                JSONObject obj = (JSONObject) message;
//                                int id = obj.getInt("id");
//                                if (id == driverid) {//get location for my vehicle
//                                    final Double lat = obj.getDouble("lat");
//                                    final Double lng = obj.getDouble("lng");
//                                    final LatLng ll = new LatLng(lat, lng);
//
//                                    runOnUiThread(new Runnable() { // l runnable d 3shn err IllegalStateException 3shn d async
//                                        @Override
//                                        public void run() {
//                                            // Your code to run in UI thread here
////                                            getDirections(lat,lng);//3shn msh kl shwya y7t
//
//                                            drivermarker.setPosition(ll);
//                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
//                                            //update UI data
////                                            getDistanceDuration(ll);
//                                        }
//                                    });
//
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void errorCallback(String channel, PubnubError error) {
//                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
//                                    + " : " + error.toString());
//                        }
//                    }
//            );
//        } catch (PubnubException e) {
//            System.out.println(e.toString());
//        }



    }


    static void animateMarkerToGB(final Marker marker, final LatLng finalPosition) {
        final LatLng startPosition = marker.getPosition();

        ///
//        initGMaps(mypos,finalPosition,selectedtransportModeFlag);
        ///
        final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }


    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to end the trip?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
//                sendTripEnded("passenger"+passenger.id);
//                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("status").setValue("ended");
                startActivity(new Intent(InTripActivity.this, TripEndActivity.class));
                finish();
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

//    void sendTripEnded(String channel){
//
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "tripended");
//            MyApplication.sendNotificationToChannel(jso,channel);
//
//        } catch (JSONException e) { e.printStackTrace(); }
//    }

    void getDirections(double newLat, double newLng){

        String url = "https://maps.googleapis.com/maps/api/directions/json"+
                "?origin="+newLat+","+newLng+
                "&destination="+trip.destination.getLatitude()+","+trip.destination.getLongitude()+
                "&sensor=false&mode=driving&alternatives=true"+"&key="+AppConstants.MAP_API_KEY;

        StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject json = new JSONObject(response);
                    JSONArray routeArray = json.getJSONArray("routes");
                    JSONObject routes = routeArray.getJSONObject(0);//awl route

                    JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                    String encodedString = overviewPolylines.getString("points");//string kber by3ml route

                    List<LatLng> list = PolyUtil.decode(encodedString);//to convert that string to list of lat lng

                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))
                            .geodesic(true)
                    );

                    //get distance and duration
                    JSONObject legs = routes.getJSONArray("legs").getJSONObject(0);
                    String distance = legs.getJSONObject("distance").getString("text");
                    String duration = legs.getJSONObject("duration").getString("text");

                    String endAddress = legs.getString("end_address");
                    addresstxt.setText(endAddress);

                    disttxt.setText(distance);
                    durationtxt.setText(duration);



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

    @Override
    public void onResume(){
        super.onResume();
        alertcircle.setVisibility(View.INVISIBLE);
    }









}
