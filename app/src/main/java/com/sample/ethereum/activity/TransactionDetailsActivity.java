package com.sample.ethereum.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sample.ethereum.R;
import com.sample.ethereum.response.EtherResult;
import com.sample.ethereum.utils.Common;

import org.web3j.utils.Convert;

import java.math.BigDecimal;

public class TransactionDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private EtherResult etherResult;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        ImageView image = findViewById(R.id.image);
        TextView amount = findViewById(R.id.tv_amount);
        TextView sender = findViewById(R.id.tv_sender);
        TextView transaction = findViewById(R.id.tv_trans);
        TextView network_fee = findViewById(R.id.tv_net_fee);
        TextView confirmations = findViewById(R.id.tv_confirm);
        TextView transaction_time = findViewById(R.id.tv_time);
        TextView nonce = findViewById(R.id.tv_nonce);
        TextView moreDetails = findViewById(R.id.tv_more_details);

        mBackArrow.setVisibility(View.VISIBLE);
        mBackArrow.setOnClickListener(this);
        moreDetails.setOnClickListener(this);

        etherResult = getIntent().getParcelableExtra(Common.Constants.etherResult);
        String transactionAddress = getIntent().getStringExtra(Common.Constants.transaction);
        mToolBarTittle.setText(getString(R.string.transaction_details));

        if (etherResult != null) {
            if (transactionAddress.equals(etherResult.getFrom())) {
                image.setImageResource(R.drawable.ic_up);
                String wei = etherResult.getValue();
                BigDecimal etherBalance = Convert.fromWei(wei, Convert.Unit.ETHER);
                amount.setText(etherBalance.toString());
                amount.setTextColor(getResources().getColor(R.color.red));
            } else {
                image.setImageResource(R.drawable.ic_down);
                String wei = etherResult.getValue();
                BigDecimal etherBalance = Convert.fromWei(wei, Convert.Unit.ETHER);
                amount.setText(etherBalance.toString());
                amount.setTextColor(getResources().getColor(R.color.green));
            }
            sender.setText(etherResult.getFrom());
            transaction.setText(etherResult.getHash());
            double gas = Double.parseDouble(String.valueOf(etherResult.getGas()));
            double gasPrice = Double.parseDouble(String.valueOf(etherResult.getGasPrice()));
            String netFee = String.valueOf(gas * gasPrice);
            network_fee.setText(netFee);
            confirmations.setText(etherResult.getConfirmations());
            transaction_time.setText(Common.getDate(Long.parseLong(etherResult.getTimeStamp())));
            nonce.setText(etherResult.getNonce());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.tv_more_details:
                String url = "https://ropsten.etherscan.io/tx/" + etherResult.getHash();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }
}
