package com.sample.ethereum.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.text.format.DateFormat;

import com.sample.ethereum.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

    public static String getDate(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh.mm aa"); // the format of your date
        return sdf.format(date);
    }

    public static double round(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_EVEN);
        return bigDecimal.doubleValue();
    }

    public static String save(String jsonString) throws IOException {
        File myDownload = new File(Environment.getExternalStoragePublicDirectory(Environment.
                DIRECTORY_DOWNLOADS) + "/keystoreWallet");
        myDownload.mkdirs();
        File jsonFile = new File(myDownload, "file.json");
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(jsonString);
        writer.close();
        return myDownload + "/" + "file.json";
    }

    public interface Constants {

        String address = "address";
        String private_key = "private_key";
        String key_value = "key_value";
        String url = "url";
        String password = "password";
        String privateKey = "privateKey";
        String publicKey = "publicKey";
        String account_address = "account_address";
        String addressList = "addressList";
        String etherResult = "etherResult";
        String transaction = "transaction";

    }
}
