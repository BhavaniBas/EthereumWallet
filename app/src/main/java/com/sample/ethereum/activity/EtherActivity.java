package com.sample.ethereum.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.utils.Common;

import java.io.IOException;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class EtherActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ether_wallet);

        Button createWallet = findViewById(R.id.btn_create);
        Button importWallet = findViewById(R.id.btn_import);

        createWallet.setOnClickListener(this);
        importWallet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                SharedHelper.clearSharedPreferences(EtherActivity.this);
                SharedHelper.putKey(this, Common.Constants.address, "");
                ShowPasswordDialog(0);
                break;
            case R.id.btn_import:
                showDialog();
                break;
        }
    }

    private void showDialog() {
        final Dialog commonDialog = new Dialog(EtherActivity.this);
        commonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commonDialog.setCancelable(false);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setContentView(R.layout.layout_custom_dialog);
        Window window = commonDialog.getWindow();
        Button btnKey = commonDialog.findViewById(R.id.btn_key);
        Button btnImportKey = commonDialog.findViewById(R.id.btn_import);
        btnKey.setOnClickListener(v -> {
            commonDialog.dismiss();
            ShowPasswordDialog(2);
        });
        btnImportKey.setOnClickListener(v -> {
            commonDialog.dismiss();
            ShowPasswordDialog(1);
        });
        commonDialog.show();
        assert window != null;
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(param);
        Objects.requireNonNull(commonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable
                (EtherActivity.this.getResources().getColor(R.color.white_transperent)));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void ShowPasswordDialog(int position) {
        final Dialog commonDialog = new Dialog(this);
        commonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commonDialog.setCancelable(false);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setContentView(R.layout.layout_password_dialog);
        Window window = commonDialog.getWindow();
        // TODO: Import,Create & Keystore Ethereum Wallet
        EditText mPassword = commonDialog.findViewById(R.id.ed_password);
        EditText mKeySyore = commonDialog.findViewById(R.id.ed_keystore);
        ImageView mEyeImg = commonDialog.findViewById(R.id.eye_img);
        Button mSubmit = commonDialog.findViewById(R.id.btn_submit);
        Button mCancel = commonDialog.findViewById(R.id.btn_cancel);
        TextView mText = commonDialog.findViewById(R.id.text);
        FrameLayout keyFrame = commonDialog.findViewById(R.id.flKeyStore);
        TextView tvKey = commonDialog.findViewById(R.id.tv_keystore);

        if (position == 1) {
            // TODO: Import Key
            keyFrame.setVisibility(View.GONE);
            tvKey.setVisibility(View.GONE);
            mEyeImg.setVisibility(View.VISIBLE);
            mText.setText(getString(R.string.private_key_));
            mEyeImg.setTag(0);
            commonDialog.show();
            mEyeImg.setOnClickListener(v -> {
                if (mEyeImg.getTag().equals(1)) {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                    mEyeImg.setImageDrawable(ContextCompat.getDrawable(EtherActivity.this, R.drawable.ic_eye_close));
                    mEyeImg.setTag(0);
                } else {
                    mEyeImg.setTag(1);
                    mPassword.setTransformationMethod(null);
                    mEyeImg.setImageDrawable(ContextCompat.getDrawable(EtherActivity.this, R.drawable.ic_eye_open));
                }
            });
            // TODO: Button Click Listener
            mSubmit.setOnClickListener(v -> {
                if (mPassword.getText().toString().isEmpty()) {
                    Toasty.error(EtherActivity.this, getString(R.string.empty_private_key),
                            Toast.LENGTH_SHORT).show();
                } else {
                    commonDialog.dismiss();
                    Intent intent = new Intent(EtherActivity.this, CreateWalletActivity.class);
                    intent.putExtra(Common.Constants.private_key, 1);
                    intent.putExtra(Common.Constants.key_value, mPassword.getText().toString());
                    startActivity(intent);
                }
                commonDialog.dismiss();
            });
            mCancel.setOnClickListener(v -> commonDialog.dismiss());
        } else if (position == 2) {
            // TODO: Keystore
            keyFrame.setVisibility(View.VISIBLE);
            tvKey.setVisibility(View.VISIBLE);
            mEyeImg.setVisibility(View.VISIBLE);
            mEyeImg.setTag(0);
            tvKey.setText(getString(R.string.key_store));
            mText.setText(getString(R.string.password));
            commonDialog.show();
            mEyeImg.setOnClickListener(v -> {
                if (mEyeImg.getTag().equals(1)) {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                    mEyeImg.setImageDrawable(ContextCompat.getDrawable(EtherActivity.this, R.drawable.ic_eye_close));
                    mEyeImg.setTag(0);
                } else {
                    mEyeImg.setTag(1);
                    mPassword.setTransformationMethod(null);
                    mEyeImg.setImageDrawable(ContextCompat.getDrawable(EtherActivity.this, R.drawable.ic_eye_open));
                }
            });
            // TODO: Button Click Listener
            mSubmit.setOnClickListener(v -> {
                if (mKeySyore.getText().toString().isEmpty()) {
                    Toasty.error(EtherActivity.this, getString(R.string.empty_key_store),
                            Toast.LENGTH_SHORT).show();
                } else if (mPassword.getText().toString().isEmpty()) {
                    Toasty.error(EtherActivity.this, getString(R.string.empty_private_key),
                            Toast.LENGTH_SHORT).show();
                } else {
                    commonDialog.dismiss();
                    String url = null;
                    try {
                        url = Common.save(mKeySyore.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(EtherActivity.this, CreateWalletActivity.class);
                    intent.putExtra(Common.Constants.private_key, 3);
                    intent.putExtra(Common.Constants.key_value, mPassword.getText().toString());
                    intent.putExtra(Common.Constants.url, url);
                    startActivity(intent);
                }
                commonDialog.dismiss();
            });
            mCancel.setOnClickListener(v -> commonDialog.dismiss());
        } else {
            // TODO: password Toggle Show (or) hide
            keyFrame.setVisibility(View.GONE);
            tvKey.setVisibility(View.GONE);
            mEyeImg.setTag(0);
            mPassword.setTransformationMethod(new PasswordTransformationMethod());
            commonDialog.show();
            // TODO: Button Click Listener
            mSubmit.setOnClickListener(v -> {
                if (mPassword.getText().toString().isEmpty()) {
                    Toasty.error(EtherActivity.this, getString(R.string.empty_password),
                            Toast.LENGTH_SHORT).show();
                } else {
                    commonDialog.dismiss();
                    Intent intent = new Intent(EtherActivity.this, CreateWalletActivity.class);
                    intent.putExtra(Common.Constants.password, mPassword.getText().toString());
                    intent.putExtra(Common.Constants.private_key, 2);
                    startActivity(intent);
                }
                commonDialog.dismiss();
            });
            mEyeImg.setOnClickListener(v -> {
                if (mEyeImg.getTag().equals(1)) {
                    mPassword.setTransformationMethod(new PasswordTransformationMethod());
                    mEyeImg.setImageDrawable(ContextCompat.getDrawable(EtherActivity.this, R.drawable.ic_eye_close));
                    mEyeImg.setTag(0);
                } else {
                    mEyeImg.setTag(1);
                    mPassword.setTransformationMethod(null);
                    mEyeImg.setImageDrawable(ContextCompat.getDrawable(EtherActivity.this, R.drawable.ic_eye_open));
                }
            });
        }
        mCancel.setOnClickListener(v -> commonDialog.dismiss());
        assert window != null;
        WindowManager.LayoutParams param = window.getAttributes();
        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(param);
        Objects.requireNonNull(commonDialog.getWindow()).setBackgroundDrawable(new ColorDrawable
                (this.getResources().getColor(R.color.white_transperent)));
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
