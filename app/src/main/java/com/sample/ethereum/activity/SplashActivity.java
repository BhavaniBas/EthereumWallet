package com.sample.ethereum.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.utils.Common;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(getApplicationContext());
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> {
            if(SharedHelper.getKey(SplashActivity.this,Common.Constants.address).equals("")||
                    SharedHelper.getKey(SplashActivity.this,Common.Constants.address).equals("null")) {
                Intent intent = new Intent(SplashActivity.this, EtherActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SplashActivity.this, CreateWalletActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        // TODO: Disable Back Button press
        // super.onBackPressed();
    }
}
