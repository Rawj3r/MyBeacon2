package com.equidais.mybeacon.controller;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.equidais.mybeacon.R;
import com.equidais.mybeacon.apiservice.ApiClient;

import com.equidais.mybeacon.common.GlobalFunc;
import com.equidais.mybeacon.common.LocalData;
import com.equidais.mybeacon.controller.service.MyService;
import com.equidais.mybeacon.controller.service.QRCode;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainApplication extends Application  implements BeaconManagerListener {
    private static final String TAG = MainApplication.class.getSimpleName();
    private SensoroManager sensoroManager;

    public static final int STATE_INIT = 0;
    public static final int STATE_ENTER_DOOR = 1;
    public static final int STATE_IN_ROOM = 2;
    public static final int STATE_OUT_ROOM_ENTER_DOOR = 3;
    public static final String WELCOME = "Welcome to OR Tambo International. Your Bidvest Car Rental is waiting for you at Drop & Go (Terminal A). Please present this QR Code/Voucher to collect your car";
    public int mState = STATE_INIT;
    String mBeaconUDID = "";
    public Date mInTime;
    public Date mOutTime;
    Thread enterNotification;
    @Override
    public void onCreate() {
        super.onCreate();

        initSensoroSDK();

        /**
         * Start SDK in Service.
         */
        Intent intent = new Intent();
        intent.setClass(this, MyService.class);
        startService(intent);

        enterNotification = new Thread(new Runnable() {
            @Override
            public void run() {

                enterGymNot();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * Initial Sensoro SDK.
     */
    private void initSensoroSDK() {
        sensoroManager = SensoroManager.getInstance(getApplicationContext());
        sensoroManager.setCloudServiceEnable(true);
        sensoroManager.setBeaconManagerListener(this);
    }

    /**
     * Start Sensoro SDK.
     */
    public void startSensoroSDK() {
        mBeaconUDID = "";
        mState = STATE_INIT;
        Intent intent = new Intent("changeState");
        sendBroadcast(intent);
        try {
            sensoroManager.startService();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void stopSensoroSDK() {
        mBeaconUDID = "";
        mState = STATE_INIT;
        Intent intent = new Intent("changeState");
        sendBroadcast(intent);
        try {
            sensoroManager.stopService();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check whether bluetooth enabled.
     * @return
     */

    public boolean isBluetoothEnabled(){
        return sensoroManager.isBluetoothEnabled();
    }

    @Override
    public void onNewBeacon(Beacon beacon) {
        /**
         * Check whether SDK started in logs.
         */
        Log.e(TAG, beacon.getProximityUUID());

        String beaconUUID = beacon.getProximityUUID();


        if (beaconUUID.equals(mBeaconUDID)){
            if (mState == STATE_INIT){

                mState = STATE_ENTER_DOOR;


            }else if (mState == STATE_IN_ROOM){

                mState = STATE_OUT_ROOM_ENTER_DOOR;
                mOutTime = new Date();
                Log.e(TAG, "out room");

                exitGymNot();
                //send server
                sendServer(beaconUUID);

            }else{
                mState = STATE_ENTER_DOOR;
                if (mState == STATE_ENTER_DOOR) {
                    mState = STATE_IN_ROOM;
                    mInTime = new Date();
                    Log.e(TAG, "enter room");
                    //enterGymNot();

                    enterNotification.start();

                }else{
                    mState = STATE_INIT;
                }
            }
        }else{
            mState = STATE_ENTER_DOOR;
        }

//        mBeaconUDID = beaconUUID;
//        Intent intent = new Intent("changeState");
//        sendBroadcast(intent);
//        Intent intent1 = new Intent(this, TransActivity.class);
//        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
//        intent1.putExtra("type", 0);
//        startActivity(intent1);


    }

    private void exitGymNot() {

        Intent intent = new Intent(this, QRCode.class);
        intent.putExtra("Gravity", "Gym");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("Gravity");
        builder.setContentText("Good bye, please come again");

        builder.setSmallIcon(R.drawable.ic_action_alarm);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(992983649, notification);
    }

    private void enterGymNot() {
        Intent intent = new Intent(this, QRCode.class);
        intent.putExtra("Gravity", "reantal");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("Liberty");
        builder.setContentText(WELCOME);
        builder.setSmallIcon(R.drawable.ic_action_clock);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(992983649, notification);
    }

    @Override
    public void onGoneBeacon(Beacon beacon) {
        Log.e(TAG, "gone");
        String beaconUUID = beacon.getProximityUUID();
//        if (beaconUUID.equals(mBeaconUDID)){
////            if (mState == STATE_ENTER_DOOR){
////                mState = STATE_IN_ROOM;
////                mInTime = new Date();
////                Log.e(TAG, "enter room");
////                enterGymNot();
////
////            }else{
////                mState = STATE_INIT;
////            }
//        }else{
//            mState = STATE_INIT;
//        }
//        mBeaconUDID = beaconUUID;
//        Intent intent = new Intent("changeState");
//        sendBroadcast(intent);
//        Intent intent1 = new Intent(this, TransActivity.class);
//        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | intent.FLAG_ACTIVITY_NEW_TASK);
//        intent1.putExtra("type", 1);
//        startActivity(intent1);
    }

    @Override
    public void onUpdateBeacon(ArrayList<Beacon> arrayList) {
        for (int i = 0; i<arrayList.size(); i++){
            Beacon beacon = arrayList.get(i);
            updateView(beacon);
        }
    }

    private void updateView(Beacon beacon) {
        if (beacon == null) {
            return;
        }
        DecimalFormat format = new DecimalFormat("#");
        String distance = format.format(beacon.getAccuracy() * 100);
        Log.e(beacon.getSerialNumber(), "" + distance + " cm");
    }

    private void sendServer(String beaconUDID){
        if (LocalData.getUserID(this) > 0){
            Map<String, Object> map = new HashMap<>();
            map.put("userid", LocalData.getUserID(this));
            map.put("uuid", beaconUDID);
            map.put("deviceudid", GlobalFunc.getDeviceUDID(this));
            map.put("timein", GlobalFunc.getStringParamDate(mInTime));
            map.put("timeout", GlobalFunc.getStringParamDate(mOutTime));
            ApiClient.getApiClient().addPersonGymVisit(map, new Callback<Integer>() {
                @Override
                public void success(Integer integer, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }


}
