package com.equidais.mybeacon.controller.main;


import android.os.Bundle;

import com.equidais.mybeacon.R;

import com.equidais.mybeacon.controller.common.BaseActivity;



public class TransActivity extends BaseActivity {

    int mType = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);
//        getSupportActionBar().hide();
//
//        mType = getIntent().getIntExtra("type", 0);
//        if (mType == 0){
////            Toast toast = Toast.makeText(this, R.string.alert_welcome, Toast.LENGTH_LONG);
////            toast.setGravity(Gravity.CENTER, 0, 0);
////            toast.show();
//        }
//        else{
////            Toast toast = Toast.makeText(this, R.string.alert_thank, Toast.LENGTH_LONG);
////            toast.setGravity(Gravity.CENTER, 0, 0);
////            toast.show();
//        }
        //finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
