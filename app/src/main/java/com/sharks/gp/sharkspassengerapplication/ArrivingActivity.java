package com.sharks.gp.sharkspassengerapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.LatLngInterpolator;
import com.sharks.gp.sharkspassengerapplication.myclasses.Passenger;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;
import com.sharks.gp.sharkspassengerapplication.myservices.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import at.markushi.ui.CircleButton;


public class ArrivingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    TextView passengernametxt, addresstxt;
    CircleButton callbtn, navbtn;
    Button startbtn;

    Trip trip;
    Passenger passenger;

    Marker drivermarker;

//    int driverid = 1;
    static int driverid;

    int vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arriving);

        driverid=MyApplication.getLoggedDriverID();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        passengernametxt = (TextView) findViewById(R.id.passengernametxt);
        addresstxt = (TextView) findViewById(R.id.addresstxt);
        callbtn = (CircleButton) findViewById(R.id.callbtn);
        navbtn = (CircleButton) findViewById(R.id.navbtn);
        startbtn = (Button) findViewById(R.id.startbtn);

        init_destination_dialog();


        try {
            trip = MyApplication.getTripRequest();
            passenger = MyApplication.getTripPassenger();
            vid = MyApplication.getLoggedDriverVehicleID();

            passengernametxt.setText(passenger.fullName);
            addresstxt.setText(MyApplication.getLocationAddress(trip.pickup));

            callbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + passenger.phone));
                    if (ActivityCompat.checkSelfPermission(ArrivingActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                }
            });
            navbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Location s = MyApplication.getLastKnownLocation();
                    Location d = trip.pickup;
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr="+s.getLatitude()+","+s.getLongitude()+"&daddr="+d.getLatitude()+","+d.getLongitude()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER );
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });
            startbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   createAndShowAlertDialog();
                }
            });





        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng pickup = new LatLng(trip.pickup.getLatitude(), trip.pickup.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(pickup).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions()
                .position(pickup)
                .title("Pickup Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.passmarker)));

        Location loc = MyApplication.getLastKnownLocation();
        LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());//for test onlyyy //ay location we hyt8yr lma ysm3
        drivermarker =  mMap.addMarker(new MarkerOptions()
                                            .position(ll)
                                            .title("My Location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(pickup));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_VEHICLES).child(String.valueOf(vid)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double lat = dataSnapshot.child("lat").getValue(Double.class);
                double lng = dataSnapshot.child("lng").getValue(Double.class);

                animateMarkerToGB(drivermarker,new LatLng(lat, lng));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });
////
//        //listen to my vehicles moves
//        try {
//            pubnub.subscribe(AppConstants.CHANNEL_PartnersLocation, new Callback() {
//                        @Override
//                        public void connectCallback(String channel, Object message) {
////                            pubnub.publish("my_channel", "Hello from the PubNub Java SDK", new Callback() {});
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
//                                    Double lat = obj.getDouble("lat");
//                                    Double lng = obj.getDouble("lng");
//                                    final LatLng ll = new LatLng(lat, lng);
//
//                                    runOnUiThread(new Runnable() { // l runnable d 3shn err IllegalStateException 3shn d async
//                                        @Override
//                                        public void run() {
//                                            // Your code to run in GUI thread here
//                                            drivermarker.setPosition(ll);
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
//



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
        builder.setTitle("Are you sure to start trip right now ?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                destdialog.show();
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

    Dialog destdialog;
    Button starttripbtn;
    PlaceAutocompleteFragment placesfragment;
    void init_destination_dialog() {
        destdialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        destdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        destdialog.setCancelable(false);
        destdialog.setContentView(R.layout.specifylocationlayout);
        starttripbtn = (Button) destdialog.findViewById(R.id.starttripbtn);

        placesfragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //placesfragment.setHint("Find spots near..");

        placesfragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
//                LatLng ll = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
//                LatLng ll = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                Location destination = new Location("");
                destination.setLatitude(place.getLatLng().latitude);
                destination.setLongitude(place.getLatLng().longitude);
//              destination.setLatitude(30.133177);
//              destination.setLongitude(31.019540);
                MyApplication.startTrip(destination,place.getAddress().toString());//testttt send to /accepttrip      //get destination
//              startService(new Intent(MyApplication.getAppContext(), LocationService.class));//only start when start trip //start service

                //sendTripStarted("passenger"+passenger.id,place.getLatLng().latitude,place.getLatLng().longitude);
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("status").setValue("started");
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("destlat").setValue(destination.getLatitude());
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("destlng").setValue(destination.getLongitude());
                MyApplication.myFirebaseRef.child("trips").child(String.valueOf(trip.trip_ID)).child("start").setValue(System.currentTimeMillis());



                startActivity(new Intent(ArrivingActivity.this, InTripActivity.class));
                finish();
            }
            @Override
            public void onError(Status status) { // Handle the error
                Toast.makeText(ArrivingActivity.this, "Something went wrong! please try again ," + status, Toast.LENGTH_SHORT).show();
            }
        });

    }

//    void sendTripStarted(String channel, double dlat, double dlng){
//
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put("type", "tripstarted");
//            jso.put("destlat", dlat);
//            jso.put("destlng", dlng);
//            MyApplication.sendNotificationToChannel(jso,channel);
//
//        } catch (JSONException e) { e.printStackTrace(); }
//    }

}
