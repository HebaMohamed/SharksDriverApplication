package com.sharks.gp.sharkspassengerapplication.myservices;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.nearby.connection.dev.Strategy;
import com.sharks.gp.sharkspassengerapplication.MainActivity;
import com.sharks.gp.sharkspassengerapplication.MainMapActivity;
import com.sharks.gp.sharkspassengerapplication.ManagerInstructionActivity;
import com.sharks.gp.sharkspassengerapplication.MyApplication;
import com.sharks.gp.sharkspassengerapplication.R;
import com.sharks.gp.sharkspassengerapplication.TripRequestActivity;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by dell on 1/31/2017.
 */
public class GcmIntentService extends IntentService {


    private static final int NOTIFICATION_ID = 1;////

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty() && GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
//            try {
//                String str = extras.getString("GCMSays");
//                sendNotification("Received: " + str);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
                String type = extras.getString("type"); //managerinstruction or triprequest
                if(type.equals("triprequest")){
                    String s = MyApplication.getAppState();
                    if(s.equals("ready"))//3shn bbb3t l notifications mn hna 8er kda fl service htcheck eno msh fy ay trip
                        goTripRequest(Double.valueOf(extras.getString("lat")),Double.valueOf(extras.getString("lng")),extras.getString("details"),Integer.valueOf(extras.getString("tripid")),Long.valueOf(extras.getString("timestamp")));
                }
                else if(type.equals("managerinstruction")){
                    goManagerInstruction(extras.getString("mgrmsg"));
                }
                else if(type.equals("passengertalk")){
                    goPassengerTalk(extras.getString("msg"), extras.getString("msgflag"));
                }


        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    public void goTripRequest(double lat, double lng, String details, int tripid, Long timestamp) {
        try {
            sendNotification("Hurry up! and tap for more details", "Trip Request !", new TripRequestActivity(),1);
            //save request for activity use
            MyApplication.storeRequest(lat, lng, details, tripid, timestamp);
            //send broad cast to main map
            Intent intent = new Intent(AppConstants.BROADCAST_TRIP_REQUEST_ACTION);
//            intent.putExtra("msg", msg);
            sendBroadcast(intent);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goPassengerTalk(String msg, String msgFlag){ // sendBroadcastMessage
        try {
            Intent intent = new Intent(AppConstants.BROADCAST_MSG_ACTION);
            intent.putExtra("msg", msg);
            intent.putExtra("msgflag", msgFlag);
            sendBroadcast(intent);
            //send notification
            sendNotification(msg, "Passenger Message!", new TripRequestActivity(),2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void goManagerInstruction(String msg) {
        try {
            //save to settings
            MyApplication.addMgrInstruction(msg);
            //send notification
            sendNotification(msg, "Manager Instruction!", new ManagerInstructionActivity(),3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private void sendBroadcastMessage() {
//        Intent intent = new Intent(AppConstants.BROADCAST_MSG_ACTION);
//        intent.putExtra("counted",counted);
//        intent.putExtra("status",status);
//        sendBroadcast(intent);
//    }

    private void sendNotification(String msg, String header, Activity activity, int notificationID) throws JSONException {

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, activity.getClass()), 0);//TripRequestActivity.class

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.slogo)
                        .setContentTitle(header)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        mBuilder.setContentIntent(contentIntent);

        //added voice
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        mBuilder.setSound(alarmSound);
        //end added voice

        mNotificationManager.notify(notificationID, mBuilder.build());//NOTIFICATION_ID

    }

}