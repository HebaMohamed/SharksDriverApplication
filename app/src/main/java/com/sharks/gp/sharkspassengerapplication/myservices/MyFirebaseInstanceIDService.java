package com.sharks.gp.sharkspassengerapplication.myservices;

import android.util.Log;

import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sharks.gp.sharkspassengerapplication.MyApplication;
import com.sharks.gp.sharkspassengerapplication.myclasses.AppConstants;

/**
 * Created by dell on 4/8/2017.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static Firebase myFirebaseRef;

    @Override
    public void onTokenRefresh() {

        if(myFirebaseRef==null)
            myFirebaseRef = new Firebase(AppConstants.FIREBASE_DB);

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);
       // sendRegistrationToServer(refreshedToken);
    }
//
    public static void sendRegistrationToServer(String token,int id) {

        MyApplication.myFirebaseRef.child("driver").child(String.valueOf(id)).child("token").setValue(token);
    }

//
//        StringRequest sr = new StringRequest(Request.Method.POST, MyURL.refreshtoken , new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject obj = new JSONObject(response);
//                    String msg = obj.getString("message");
//                    int success = Integer.parseInt(obj.getString("success"));
//
//                    Log.d("MyFirebaseInstanceIDS ",msg);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d("SaveTokenResponse ", "onResponse: "+response);
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        }){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
////                params.put("id",MyApplication.getuserid());
//                params.put("token",token);
//                params.put("user","Client");
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//
//
//        // Add the request to the queue
//        Volley.newRequestQueue(MyApplication.getAppContext()).add(sr);
//
//

}