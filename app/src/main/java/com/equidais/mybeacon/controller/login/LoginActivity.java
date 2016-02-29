package com.equidais.mybeacon.controller.login;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.equidais.mybeacon.R;
import com.equidais.mybeacon.apiservice.ApiClient;
import com.equidais.mybeacon.common.GlobalFunc;
import com.equidais.mybeacon.common.LocalData;
import com.equidais.mybeacon.controller.common.BaseActivity;
import com.equidais.mybeacon.controller.main.MainActivity;
import com.equidais.mybeacon.model.LoginResult;
import com.sensoro.beacon.kit.Beacon;
import com.sensoro.beacon.kit.BeaconManagerListener;
import com.sensoro.cloud.SensoroManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
public class LoginActivity extends BaseActivity implements View.OnClickListener{
    public static final int REQUEST_ENABLE_BT = 3;
    EditText mEditEmail;
    EditText mEditPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEditEmail = (EditText)findViewById(R.id.edit_email);
        mEditPassword = (EditText)findViewById(R.id.edit_password);
        findViewById(R.id.btn_login).setOnClickListener(this);
        if (LocalData.getUserID(this) > 0){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }
        beatconTest();
    }
    private void beatconTest(){
        final SensoroManager sensoroManager = SensoroManager.getInstance(this);
        /**
         * Check whether the Bluetooth is on
         **/
        if (sensoroManager.isBluetoothEnabled()) {
            /**
             * Enable cloud service (upload sensor data, including battery status, UMM, etc.). Without setup, it keeps in closed status as default.
             **/
            sensoroManager.setCloudServiceEnable(true);
            /**
             * Enable SDK service
             **/
            try {
                sensoroManager.startService();
            } catch (Exception e) {
                e.printStackTrace(); // Fetch abnormal info
            }
            sensoroManager.addBroadcastKey("0117C5393A7A");
//            BeaconManagerListener beaconManagerListener = new BeaconManagerListener() {
//                @Override
//                public void onUpdateBeacon(ArrayList<Beacon> beacons) {
//                    // Refresh sensor info
//                    for (int i = 0; i<beacons.size(); i++){
//                        Beacon beacon = beacons.get(i);
//                        updateView(beacon);
//                    }
//                }
//                @Override
//                public void onNewBeacon(Beacon beacon) {
//                    // New sensor found
//                    if (beacon.getSerialNumber().equals("0117C5393A7A")){
////                        Toast.makeText(LoginActivity.this, "find beacon", Toast.LENGTH_SHORT).show();
//                        // Yunzi with SN "0117C5456A36" enters the range
//                    }
//                }
//                @Override
//                public void onGoneBeacon(Beacon beacon) {
//                    System.out.println("Out of range......");
////
//
//                }
//            };
//            sensoroManager.setBeaconManagerListener(beaconManagerListener);
        }else{
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);
        }

    }
    private void updateView(Beacon beacon) {
        if (beacon == null) {
            return;
        }
        DecimalFormat format = new DecimalFormat("#");
        String distance = format.format(beacon.getAccuracy() * 100);
//        System.out.println("" + distance + " cm");
//        ((TextView)findViewById(R.id.txt_test)).setText("" + distance + " cm");
    }
    ProgressDialog mProgressDlg;
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login){
            final String email = mEditEmail.getText().toString();
            final String password = mEditPassword.getText().toString();
            if (email.equals("")){
                GlobalFunc.showAlertDialog(LoginActivity.this, getResources().getString(R.string.warning),
                        getResources().getString(R.string.alert_input_email));
                return;
            }
            if (password.equals("")){
                GlobalFunc.showAlertDialog(LoginActivity.this, getResources().getString(R.string.warning),
                        getResources().getString(R.string.alert_input_password));
                return;
            }
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("username", email);
            hashMap.put("password", password);
            hashMap.put("deviceudid", GlobalFunc.getDeviceUDID(this));
            mProgressDlg = GlobalFunc.showProgressDialog(this);
            ApiClient.getApiClient().login(hashMap, new Callback<LoginResult>() {
                @Override
                public void success(LoginResult loginResult, Response response) {
                    mProgressDlg.dismiss();
                    if (loginResult.IsSuccess) {
                        LocalData.setUsernameAndPassword(LoginActivity.this, email, password, loginResult.UserID);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (loginResult.Message != null && !loginResult.Message.equals("")){
                        GlobalFunc.showAlertDialog(LoginActivity.this, getResources().getString(R.string.warning),
                                loginResult.Message);
                    } else {
                        GlobalFunc.showAlertDialog(LoginActivity.this, getResources().getString(R.string.warning),
                                getResources().getString(R.string.alert_connect_failed) + "nnnnnnnnnn");
                    }
                }
                @Override
                public void failure(RetrofitError error) {
                    mProgressDlg.dismiss();
                    GlobalFunc.showAlertDialog(LoginActivity.this, getResources().getString(R.string.warning),
                            getResources().getString(R.string.alert_connect_failed));
                    System.out.println("E101:"+error.toString());
                }
            });
        }
    }

}