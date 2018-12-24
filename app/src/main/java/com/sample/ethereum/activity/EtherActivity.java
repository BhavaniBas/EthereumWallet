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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;

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
                ShowPasswordDialog(0);
                break;
            case R.id.btn_import:
                ShowPasswordDialog(1);
                break;

        }
    }

    private void ShowPasswordDialog(int position) {
        final Dialog commonDialog = new Dialog(this);
        commonDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        commonDialog.setCancelable(false);
        commonDialog.setCanceledOnTouchOutside(false);
        commonDialog.setContentView(R.layout.layout_password_dialog);
        Window window = commonDialog.getWindow();
        EditText mPassword = commonDialog.findViewById(R.id.ed_password);
        ImageView mEyeImg = commonDialog.findViewById(R.id.eye_img);
        Button mSubmit = commonDialog.findViewById(R.id.btn_submit);
        Button mCancel = commonDialog.findViewById(R.id.btn_cancel);
        TextView mText = commonDialog.findViewById(R.id.text);
        if (position == 1) {
            mEyeImg.setVisibility(View.GONE);
            mText.setText("Private Key");
            commonDialog.show();
            // TODO: Button Click Listener
            mSubmit.setOnClickListener(v -> {
                if (mPassword.getText().toString().isEmpty()) {
                    Toasty.error(EtherActivity.this, "Please enter private key",
                            Toast.LENGTH_SHORT).show();
                } else {
                    commonDialog.dismiss();
                    Intent intent = new Intent(EtherActivity.this, CreateWalletActivity.class);
                    intent.putExtra("private_key",1);
                    intent.putExtra("key_value",mPassword.getText().toString());
                    startActivity(intent);
                }
                commonDialog.dismiss();
            });
            mCancel.setOnClickListener(v -> commonDialog.dismiss());
        } else {
            // TODO: password Toggle Show (or) hide
            mEyeImg.setTag(0);
            mPassword.setTransformationMethod(new PasswordTransformationMethod());
            commonDialog.show();
            // TODO: Button Click Listener
            mSubmit.setOnClickListener(v -> {
                if (mPassword.getText().toString().isEmpty()) {
                    Toasty.error(EtherActivity.this, "Please enter password",
                            Toast.LENGTH_SHORT).show();
                } else {
                    commonDialog.dismiss();
                    Intent intent = new Intent(EtherActivity.this, CreateWalletActivity.class);
                    intent.putExtra("password", mPassword.getText().toString());
                    intent.putExtra("private_key",0);
                    intent.putExtra("private_key",2); // impor
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
