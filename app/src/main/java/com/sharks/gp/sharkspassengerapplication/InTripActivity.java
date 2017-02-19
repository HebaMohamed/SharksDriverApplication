package com.sharks.gp.sharkspassengerapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;

import static com.sharks.gp.sharkspassengerapplication.MyApplication.pubnub;

public class InTripActivity  extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Marker drivermarker;
    //testtt
    int driverid = 1;

    TextView addresstxt, disttxt, durationtxt;
    Button endbtn;
    CircleButton chatbtn, navbtn;
    Trip trip;

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


        try {
            trip = MyApplication.getTripRequest();
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ll = new LatLng(trip.destination.getLatitude(),trip.destination.getLongitude());
        mMap.addMarker(new MarkerOptions().position(ll).title("Destination"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));//first only
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        Location loc = MyApplication.getLastKnownLocation();
        LatLng mll = new LatLng(loc.getLatitude(),loc.getLongitude());//for test onlyyy //ay location we hyt8yr lma ysm3
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
        getDirections(loc.getLatitude(),loc.getLongitude());

        //listen to my wehicle location
        try {
            pubnub.subscribe(AppConstants.CHANNEL_PartnersLocation, new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) { //l msg bttst2bl hna
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());

                            try {
                                JSONObject obj = (JSONObject) message;
                                int id = obj.getInt("id");
                                if (id == driverid) {//get location for my vehicle
                                    final Double lat = obj.getDouble("lat");
                                    final Double lng = obj.getDouble("lng");
                                    final LatLng ll = new LatLng(lat, lng);

                                    runOnUiThread(new Runnable() { // l runnable d 3shn err IllegalStateException 3shn d async
                                        @Override
                                        public void run() {
                                            // Your code to run in UI thread here
//                                            getDirections(lat,lng);//3shn msh kl shwya y7t

                                            drivermarker.setPosition(ll);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
                                            //update UI data
//                                            getDistanceDuration(ll);
                                        }
                                    });

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }



    }



    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to end the trip?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
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







}
