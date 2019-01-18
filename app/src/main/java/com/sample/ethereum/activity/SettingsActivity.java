package com.sample.ethereum.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.utils.Common;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        LinearLayout mToken = findViewById(R.id.lnrTokenContainer);
        LinearLayout mNetwork = findViewById(R.id.lnrNetContainer);
        LinearLayout mProfile = findViewById(R.id.lnrProfileContainer);
        LinearLayout mLogout = findViewById(R.id.lnrLogoutContainer);
        mBackArrow.setVisibility(View.VISIBLE);
        mToolBarTittle.setText(getString(R.string.settings));
        mBackArrow.setOnClickListener(this);
        mToken.setOnClickListener(this);
        mNetwork.setOnClickListener(this);
        mProfile.setOnClickListener(this);
        mLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.lnrTokenContainer:
                Intent settingsIntent = new Intent(SettingsActivity.this, TokenListActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.lnrNetContainer:
                break;
            case R.id.lnrProfileContainer:
                break;
            case R.id.lnrLogoutContainer:
                SharedHelper.clearSharedPreferences(SettingsActivity.this);
                SharedHelper.putKey(this, Common.Constants.address, "");
                SharedHelper.putKey(this, Common.Constants.privateKey, "");
                SharedHelper.putKey(this, Common.Constants.publicKey, "");
                Intent intent = new Intent(SettingsActivity.this, EtherActivity.class);
                startActivity(intent);
                Toasty.success(this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
