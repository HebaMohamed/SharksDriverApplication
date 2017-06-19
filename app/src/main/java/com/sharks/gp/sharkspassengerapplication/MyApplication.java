package com.sharks.gp.sharkspassengerapplication;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
//import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;
import com.sharks.gp.sharkspassengerapplication.myclasses.Driver;
import com.sharks.gp.sharkspassengerapplication.myclasses.MgrInstruction;
import com.sharks.gp.sharkspassengerapplication.myclasses.Passenger;
import com.sharks.gp.sharkspassengerapplication.myclasses.Trip;
import com.sharks.gp.sharkspassengerapplication.myclasses.Vehicle;
import com.sharks.gp.sharkspassengerapplication.myservices.LocationService;
import com.sharks.gp.sharkspassengerapplication.myservices.MyFirebaseInstanceIDService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dell on 1/31/2017.
 */
public class MyApplication  extends android.support.multidex.MultiDexApplication{
    private static final String TAG = "MyApplication";//android.app.Application {
    private static MyApplication mInstance;
    public static SharedPreferences prefs;
    private static Context mycontext;
//    public static Pubnub pubnub;


    private static String regId;
    private static GoogleCloudMessaging gcm;

    public static Firebase myFirebaseRef;

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        mInstance = this;
        MyApplication.mycontext=getApplicationContext();//
        prefs = PreferenceManager.getDefaultSharedPreferences(this);//("mypref", Context.MODE_PRIVATE);

//        pubnub = new Pubnub( AppConstants.PUB_PUBLISH_KEY, AppConstants.PUB_SUBSCRIBE_KEY);
//        register();//for gcm services

        //for firebase
        Firebase.setAndroidContext(getApplicationContext());
        if(myFirebaseRef==null)
            myFirebaseRef = new Firebase(AppConstants.FIREBASE_DB);

        try {
            FirebaseApp.getInstance();
        } catch (IllegalStateException ex) {
            FirebaseApp.initializeApp(mycontext, FirebaseOptions.fromResource(mycontext));
        }


        String firebasetoken = FirebaseInstanceId.getInstance().getToken();
        int did = MyApplication.getLoggedDriverID();
        if(did!=0)
            MyFirebaseInstanceIDService.sendRegistrationToServer(firebasetoken,did);
        if(firebasetoken!=null)
            Log.d("token: ",firebasetoken);

    }
    public static Context getAppContext() {
        return MyApplication.mycontext;
    }
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    ///////////////////////////////////////////////////////////////////////////////login methods
//    private void register() {
//        if (checkPlayServices()) {
//            gcm = GoogleCloudMessaging.getInstance(this);
//            try {
//                regId = getRegistrationId(getApplicationContext());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            if (regId.isEmpty()) {
//                registerInBackground();
//            } else {
////                Toast.makeText(getAppContext(), "Registration ID already exists:" + regId, Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Log.e(TAG, "No valid Google Play Services APK found.");
//        }
//    }
//    private String getRegistrationId(Context context) throws Exception {
////        prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
//        String registrationId = prefs.getString(AppConstants.PROPERTY_REG_ID, "");
//        if (registrationId.isEmpty()) {
//            return "";
//        }
//        return registrationId;
//    }
//    private void sendRegistrationId(String regId) {
//        pubnub.enablePushNotificationsOnChannel(
//                AppConstants.CHANNEL_NOTIFY,
//                regId);
//    }
//    private void registerInBackground() {
//        new AsyncTask() {
//            @Override
//            protected String doInBackground(Object[] params) {
//                String msg;
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//                    }
//                    regId = gcm.register(AppConstants.SENDER_ID);
//                    msg = "Device registered, registration ID: " + regId;
//
//                    sendRegistrationId(regId);
//
//                    storeRegistrationId(getApplicationContext(), regId);
//                    Log.i(TAG, msg);
//                } catch (Exception ex) {
//                    msg = "Error :" + ex.getMessage();
//                    Log.e(TAG, msg);
//                }
//                return msg;
//            }
//        }.execute(null, null, null);
//    }
//    private void storeRegistrationId(Context context, String regId) throws Exception {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(AppConstants.PROPERTY_REG_ID, regId);
//        editor.apply();
//    }
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                //GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Log.e(TAG, "This device is not supported.");
//                //finish();
//            }
//            return false;
//        }
//        return true;
//    }

    ///////////////////////////////////////////////////////////////////////////////logout methods
//    private static void unregister() {
//        new AsyncTask() {
//            @Override
//            protected Object doInBackground(Object[] params) {
//                try {
//                    if (gcm == null) {
//                        gcm = GoogleCloudMessaging.getInstance(MyApplication.getAppContext());
//                    }
//                    // Unregister from GCM
//                    gcm.unregister();
//                    // Remove Registration ID from memory
//                    removeRegistrationId(MyApplication.getAppContext());
//                    // Disable Push Notification
//                    pubnub.disablePushNotificationsOnChannel(AppConstants.CHANNEL_NOTIFY, regId);
//                } catch (Exception e) { }
//                return null;
//            }
//        }.execute(null, null, null);
//    }
//    private static void removeRegistrationId(Context context) throws Exception {
////        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
//
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.remove(AppConstants.PROPERTY_REG_ID);
//        editor.apply();
//    }

    //////////////////////////////////////////////////////////////////////////////test send notification methods

//    public static void sendNotification() {
//        PnGcmMessage gcmMessage = new PnGcmMessage();
//        JSONObject jso = new JSONObject();
//        try {
////            jso.put("GCMSays", "hi");
//
//            jso.put("type", "triprequest");
//            jso.put("lat", 30.123177);
//            jso.put("lng", 31.009540);
//            jso.put("details", "I'm over here a");
//            jso.put("tripid", 1);
//            jso.put("timestamp", System.currentTimeMillis());
//
//        } catch (JSONException e) { }
//        gcmMessage.setData(jso);
//
//        PnMessage message = new PnMessage(
//                pubnub,
//                AppConstants.CHANNEL_NOTIFY,
//                callback,
//                gcmMessage);
//        try {
//            message.publish();
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void sendNotification(JSONObject jso) {
//        PnGcmMessage gcmMessage = new PnGcmMessage();
//        gcmMessage.setData(jso);
//        PnMessage message = new PnMessage(
//                pubnub,
//                AppConstants.CHANNEL_NOTIFY,
//                callback,
//                gcmMessage);
//        try {
//            message.publish();
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void sendNotificationToChannel(JSONObject jso, String channel) {
//        PnGcmMessage gcmMessage = new PnGcmMessage();
//        gcmMessage.setData(jso);
//        PnMessage message = new PnMessage(
//                pubnub,
//                channel,
//                callback,
//                gcmMessage);
//        try {
//            message.publish();
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//    }

//    public static Callback callback = new Callback() {
//        @Override
//        public void successCallback(String channel, Object message) {
//            Log.i(TAG, "Success on Channel " + AppConstants.CHANNEL_NOTIFY  + " : " + message);
//        }
//        @Override
//        public void errorCallback(String channel, PubnubError error) {
//            Log.i(TAG, "Error On Channel " + AppConstants.CHANNEL_NOTIFY + " : " + error);
//        }
//    };


    //////////////////////////////////////////////////////////////////////////////////loc
    public static String getLocationAddress(Location loc) throws IOException {
        try{
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(mycontext, Locale.getDefault());
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String approxAddress = addresses.get(0).getAddressLine(0);
            return approxAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return"Unknown Address";
        }
    }

    /////////////////////////////////////////////////////////////////////////////////app state
    public static String getAppState(){
        String appstate = prefs.getString(AppConstants.PROPERTY_APP_STATE, "");
        return appstate;
    }

    public static void storeRequest(double lat, double lng, String details, int tripid, Long timestamp, int passengerid) throws Exception {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "request");
        editor.putString("lat", String.valueOf(lat));
        editor.putString("lng", String.valueOf(lng));
        editor.putString("details", details);
        editor.putString("tripid", String.valueOf(tripid));
        editor.putString("passengerid", String.valueOf(passengerid));
        editor.putString("timestamp", String.valueOf(timestamp));

        editor.apply();
    }
    public static void endRequest() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "ended");
        editor.apply();
    }

    public static void beReady() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "ready");
        editor.apply();
    }

    public static void removeNotifications(int notfication_id){
        NotificationManager notifManager= (NotificationManager) MyApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notifManager.cancelAll();
        notifManager.cancel(notfication_id);
    }

    public static Trip getTripRequest() throws Exception {
//        prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
//        String appstate = prefs.getString(AppConstants.PROPERTY_APP_STATE, "");
//        if (!appstate.equals("request")) {
//            return null;
//        }
        double lat = Double.parseDouble(prefs.getString("lat", "0"));
        double lng = Double.parseDouble(prefs.getString("lng", "0"));
        String details = prefs.getString("details", "");
        int tripid = Integer.parseInt(prefs.getString("tripid", "0"));
        Long timestamp = Long.parseLong(prefs.getString("timestamp", "0"));
        int passengerid = Integer.parseInt(prefs.getString("passengerid","0"));

        Trip trip = new Trip(lat, lng, details, tripid, timestamp);
        //id the destination added
        double dlat = Double.parseDouble(prefs.getString("distlat", "0"));
        double dlng = Double.parseDouble(prefs.getString("distlng", "0"));
        trip.setDestination(dlat,dlng);
        trip.p=new Passenger(passengerid);
        return trip;
    }
    public static void storeTripAcceptance(Passenger p){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "accept");
        editor.putString("pid", String.valueOf(p.id));
        editor.putString("pname", p.fullName);
//        editor.putString("pgender", p.gender);
//        editor.putString("page", String.valueOf(p.age));
        editor.putString("pphone", String.valueOf(p.phone));
//        editor.putString("prelativephone", String.valueOf(p.relative_phones));
//        editor.putString("planguage", p.language);
//        editor.putString("pemail", p.email);
        editor.apply();
    }
    public static Passenger getTripPassenger() throws Exception {
        int pid = Integer.parseInt(prefs.getString("pid", "0"));
        String pname = prefs.getString("pname", "");
//        String pgender = prefs.getString("pgender", "");
//        int page = Integer.parseInt(prefs.getString("page", "0"));
        int pphone = Integer.parseInt(prefs.getString("pphone", "0"));
//        int prelativephone = Integer.parseInt(prefs.getString("prelativephone", "0"));
//        String planguage = prefs.getString("planguage", "");
//        String pemail = prefs.getString("pemail", "");

//        Passenger p = new Passenger(pid,"",pname,pgender,page,pphone,prelativephone,planguage,pemail);
        Passenger p = new Passenger(pid);
        p.fullName=pname;
        p.phone=pphone;
        return p;
    }

    public static void startTrip(Location destination, String placename) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.PROPERTY_APP_STATE, "started");
        editor.putString("distlat", String.valueOf(destination.getLatitude()));
        editor.putString("distlng", String.valueOf(destination.getLongitude()));
        editor.putString("distname", placename);
        editor.apply();
    }

        ///////////////////////////////////////////////////////////////////////////////////////get last location
    public static Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(mycontext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mycontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;
        LocationManager mLocationManager;
        mLocationManager = (LocationManager)mycontext.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

        ///////////////////////////////////////////////////////////////////////////////login register
        public static void storeLogin(Driver d) throws Exception {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(AppConstants.PROPERTY_APP_STATE, "ready");
            editor.putInt("did", d.id);
            editor.putString("dname", d.name);
            editor.putString("dimage", d.image);
            editor.putInt("vid", d.vehicle.ID);

            editor.putString("vnum", d.vehicle.Plate_number);
            editor.putString("vcolor", d.vehicle.Color);
            editor.putString("vmodel", d.vehicle.Model);


//            editor.putString("demail", d.email);
//            editor.putString("dimage", d.image);
            editor.apply();
        }
        public static Driver getLoggedDriver() {
            int did = prefs.getInt("did", 0);
            Driver d;
            if(did != 0) {//3shn lw mfish wla driver logged
                String dname = prefs.getString("dname", "");
//                String demail = prefs.getString("demail", "");
                String dimage = prefs.getString("dimage", "");
//                d = new Driver(did, dname, demail, "", dimage);
                d = new Driver(did, dname, "", "", dimage);

                String vnum = prefs.getString("vnum", "");
                String vmodel = prefs.getString("vmodel", "");
                String vcolor = prefs.getString("vcolor", "");
                int vid = prefs.getInt("vid", 0);

                d.vehicle = new Vehicle(vid);
                d.vehicle.Color=vcolor;
                d.vehicle.Model=vmodel;
                d.vehicle.Plate_number=vnum;

            }
            else
                d=null;
            return d;
        }
        public static int getLoggedDriverID() {
            int did = prefs.getInt("did", 0);
            return did;
        }
        public static int getLoggedDriverVehicleID() {
            int did = prefs.getInt("vid", 0);
            return did;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////logout methods
        public static void storelogout(){
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

//            unregister();//3shn l notifications

//            removeNotifications(1);
//            removeNotifications(2);
//            removeNotifications(3);
        }


    ///////////////////////////////////////////////////////////////////////////////////////////manager instuctions storage
        public static void addMgrInstruction(String inst) {
            //get last instructions count
            int mgrinstcount = prefs.getInt("mgrinstcount", 0);
            int new_mgrinstcount = mgrinstcount+1;

            //save it
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("mgrinstruction"+new_mgrinstcount, inst);
            editor.putLong("mgrinstructiontimestmap"+new_mgrinstcount, System.currentTimeMillis());
            editor.putInt("mgrinstcount", new_mgrinstcount);

            editor.apply();
        }
    public static ArrayList<MgrInstruction> getMgrsInstructions(){
        ArrayList<MgrInstruction> instructions = new ArrayList<>();
        int mgrinstcount = prefs.getInt("mgrinstcount", 0); //get count to loop
        for (int i = 0; i <= mgrinstcount; i++) {
            String mgrinstruction = prefs.getString("mgrinstruction"+i, "");
            Long mgrinstructiontimestmap = prefs.getLong("mgrinstructiontimestmap"+i, 0);
            MgrInstruction inst = new MgrInstruction(mgrinstruction,mgrinstructiontimestmap);
            if(!mgrinstruction.equals(""))
                instructions.add(inst);
        }
        return instructions;
    }


    ////////////////////////////////////////////////////////////female
    public static void addFemaleHelp(Long fid) {
        //save it
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("fid", fid);
        editor.apply();
    }
    public static long getFemaleHelp() {
        long fid = prefs.getLong("fid", 0);
        return fid;
    }
    public static void removeFemaleHelp() {
        //save it
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("fid");
        editor.apply();
    }
}
