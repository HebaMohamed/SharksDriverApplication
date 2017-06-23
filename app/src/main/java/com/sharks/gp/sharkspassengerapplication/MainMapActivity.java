package com.sharks.gp.sharkspassengerapplication;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;
import com.sharks.gp.sharkspassengerapplication.myclasses.LatLngInterpolator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    private static GoogleMap mMap;
    private Marker drivermarker;

    //testtt
    int driverid;// = 1;
    int vehicleid;// = 1;

    Location loc;

    Driver d;

    ArrayList<Marker> markers = new ArrayList<>();

    TextView colortxt, modeltxt, numtxt;
    ImageView onoffimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Sharks");

        colortxt = (TextView) findViewById(R.id.colortxt);
        modeltxt = (TextView) findViewById(R.id.modeltxt);
        numtxt = (TextView) findViewById(R.id.numtxt);
        onoffimg = (ImageView) findViewById(R.id.onoffimg);

        driverid=MyApplication.getLoggedDriverID();
        vehicleid=MyApplication.getLoggedDriverVehicleID();
        d=MyApplication.getLoggedDriver();

        colortxt.setText(d.vehicle.Color);
        modeltxt.setText(d.vehicle.Model);
        numtxt.setText(d.vehicle.Plate_number);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //add image and text
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.dfullnametxt);
        CircleImageView dimg = (CircleImageView)hView.findViewById(R.id.dimg);
        nav_user.setText(d.name);
        if(d.image != "") {
            byte[] decodedString = Base64.decode(d.image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            dimg.setImageBitmap(decodedByte);
        }else{
            dimg.setImageDrawable(getResources().getDrawable(R.drawable.usericn2));
        }

        navigationView.setNavigationItemSelectedListener(this);


        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_TRIPS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        int did = postSnapshot.child("did").getValue(int.class);
                        String status = postSnapshot.child("status").getValue(String.class);
                        if (did == driverid && status.equals("requested")) {//trip request
                            int tid = Integer.parseInt(postSnapshot.getKey());
                            double ilat = postSnapshot.child("ilat").getValue(Double.class);
                            double ilng = postSnapshot.child("ilng").getValue(Double.class);
                            String details = postSnapshot.child("details").getValue(String.class);
                            Long timestamp = postSnapshot.child("timestamp").getValue(Long.class);
                            int pid = postSnapshot.child("pid").getValue(Integer.class);


                            MyApplication.storeRequest(ilat, ilng, details, tid, timestamp, pid);
                            startActivity(new Intent(MainMapActivity.this, TripRequestActivity.class));
                            finish();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //Toast.makeText(MainMapActivity.this, "There is new request but the problem : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
//                String status = dataSnapshot.child("status").getValue(String.class);
//
//                if(status.equals("started")) {
//
//                    double destlat = dataSnapshot.child("destlat").getValue(Double.class);
//                    double destlng = dataSnapshot.child("destlng").getValue(Double.class);
//
//                    //save request for activity use
//                    MyApplication.setInTripState(destlat, destlng);
//                    startActivity(new Intent(ArrivingActivity.this, InTripActivity.class));
//                    finish();
//                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

        /////recive trip request
//        IntentFilter filter = new IntentFilter(AppConstants.BROADCAST_TRIP_REQUEST_ACTION);
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                startActivity(new Intent(MainMapActivity.this, TripRequestActivity.class));
//                finish();
//            }
//        };
//        registerReceiver(receiver, filter);


        ////////listen to female requests
        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_DRIVER).child(String.valueOf(driverid)).child("warninghelp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    long femalesafteyid = dataSnapshot.child("femalesafteyid").getValue(Long.class);
                    if(femalesafteyid!=0) {
                        MyApplication.addFemaleHelp(femalesafteyid);

                        startActivity(new Intent(MainMapActivity.this, HelpFemaleActivity.class));
                        finish();
                    }
                }catch (NullPointerException ne){
                    ne.printStackTrace();
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_map, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            startActivity(new Intent(MainMapActivity.this, LastTripsActivity.class));
        } else if (id == R.id.nav_mgr) {
            startActivity(new Intent(MainMapActivity.this, ManagerInstructionActivity.class));
        } else if (id == R.id.nav_out) {
            createAndShowAlertDialog();
        } else if (id == R.id.nav_report) {
            startActivity(new Intent(MainMapActivity.this, ReportActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    LatLng ll; int id;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        buildGoogleApiClient();
        //show my location
//        Location loc = MyApplication.getLastKnownLocation();
        drivermarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0,0))
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallorangeshark)));


        addRestrictedRouted();


        //listen & get initial value
        MyApplication.myFirebaseRef.child(AppConstants.FIRE_VEHICLES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    try {
                        int vid = Integer.parseInt(postSnapshot.getKey());
                        double lat = postSnapshot.child("Latitude").getValue(Double.class);
                        double lng = postSnapshot.child("Longitude").getValue(Double.class);



                        ll = new LatLng(lat, lng);

                        if (vid == vehicleid) {
                            //show vehicle on off
                            int status = postSnapshot.child("status").getValue(int.class);
                            if(status==1)
                                onoffimg.setImageDrawable(getResources().getDrawable(R.drawable.poweron));
                            else
                                onoffimg.setImageDrawable(getResources().getDrawable(R.drawable.poweroff));

                            animateMarkerToGB(drivermarker, ll);
                        } else {
                            int f = 0;
                            for (int i = 0; i < markers.size(); i++) {
                                if (markers.get(i).getSnippet().toString().equals(String.valueOf(vid))) {
                                    //markers.get(i).setPosition(ll);
                                    // animation part
                                    animateMarkerToGB(markers.get(i), ll);
                                    f = 1;
                                }
                            }
                            if (f == 0) {
                                markers.add(mMap.addMarker(new MarkerOptions()
                                        .position(ll)
                                        .title("Shark Location")
                                        .snippet(String.valueOf(vid))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallblueshark))));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();//3shn momkn fl test yb2a fy node msh feha lat msln
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }

        });

//        //listen to all vehicles moves
//        try {
//            MyApplication.pubnub.subscribe(AppConstants.CHANNEL_PartnersLocation, new Callback() {
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
//                                id = obj.getInt("did");
//                                Double lat = obj.getDouble("lat");
//                                Double lng = obj.getDouble("lng");
//                                ll = new LatLng(lat, lng);
//
//                                runOnUiThread(new Runnable() { // l runnable d 3shn err IllegalStateException 3shn d async
//                                    @Override
//                                    public void run() {
//                                        // Your code to run in GUI thread here
//                                        if (id == driverid) {//get location for my vehicle
////                                            drivermarker.setPosition(ll);
//                                            //animation
//                                            animateMarkerToGB(drivermarker,ll);
////                                            //move to this location
////                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
////                                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//                                            mMap.animateCamera(CameraUpdateFactory.newLatLng(ll));
//
//                                        }
//                                        else {
//                                            mMap.addMarker(new MarkerOptions()
//                                                    .position(ll)
//                                                    .title("Another Driver Location")
//                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallorangeshark)));
//                                        }
//                                    }
//                                });
//
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


    void addRestrictedRouted(){
        //show restricted route
        List<LatLng> list = new ArrayList<LatLng>();
        for (int i = 0; i < d.restrictedLats.size(); i++) {
            list.add(new LatLng(d.restrictedLats.get(i),d.restrictedLngs.get(i)));
        }
        if(list.size()!=0) {
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(12)
                    .color(Color.parseColor("#e74c3c"))
                    .geodesic(true)
            );
            mMap.addMarker(new MarkerOptions()
                    .position(list.get(0))
                    .title("Restricted route start")
                    .snippet(""));
            mMap.addMarker(new MarkerOptions()
                    .position(list.get(list.size()-1))
                    .title("Restricted route end")
                    .snippet(""));
        }
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure to sign out ?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                MyApplication.myFirebaseRef.child("driver").child(String.valueOf(MyApplication.getLoggedDriverID())).child("logged").setValue("false");
                MyApplication.storelogout();
                startActivity(new Intent(MainMapActivity.this, MainActivity.class));
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


    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    protected synchronized void buildGoogleApiClient() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10 * 1000);
        mLocationRequest.setFastestInterval(1 * 1000);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        if (ActivityCompat.checkSelfPermission(MainMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainMapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,GeoLocMapActivity.this );
                        loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (loc != null) {
//            dLat = loc.getLatitude();
//            dLong = loc.getLongitude();
//            //Does this log?
//            Log.d(getClass().getSimpleName(), String.valueOf(dLat) + ", " + String.valueOf(dLong));

//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title("Assistant Location"));
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(loc.getLatitude(), loc.getLongitude())));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()),14));



                        } else {
                            Toast.makeText(MainMapActivity.this, "no location detected", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
//                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//                    }
//                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
//            mGoogleApiClient.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mGoogleApiClient != null)
            if (mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
                mGoogleApiClient.disconnect();
                mGoogleApiClient.connect();
            } else if (!mGoogleApiClient.isConnected()){
                mGoogleApiClient.connect();
            }
//        Toast.makeText(GeoLocMapActivity.this, "Loc", Toast.LENGTH_SHORT).show();

        LatLng ll = new LatLng(loc.getLatitude(),loc.getLongitude());//for test onlyyy //ay location we hyt8yr lma ysm3
        drivermarker =  mMap.addMarker(new MarkerOptions()
                .position(ll)
                .title("My Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smallorangeshark)));

        //move to this location
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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

}
