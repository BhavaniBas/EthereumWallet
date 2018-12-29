package com.sample.ethereum.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.sample.ethereum.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Common {

    private static ProgressDialog mProgressDialog;
    private static List<String> mContractAddress = new ArrayList<>();

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

    public static String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            Date currenTimeZone = calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception ignored) {
        }
        return "";
    }


    public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_EVEN);
        return bigDecimal.doubleValue();
    }
}
