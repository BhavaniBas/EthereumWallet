package com.sample.ethereum.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.sample.ethereum.R;

public class Common {

    private static ProgressDialog mProgressDialog;

    public static void showProgressbar(Context mContext) {
        dismissProgressbar();
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.show();
        if (mProgressDialog.getWindow() != null)
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mProgressDialog.setContentView(R.layout.progress_dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    public static void dismissProgressbar() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.cancel();
    }
}
