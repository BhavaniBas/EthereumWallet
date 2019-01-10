package com.sample.ethereum.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.utils.Common;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;

import static com.sample.ethereum.activity.CreateWalletActivity.web3;

public class SendTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdAddress;
    private EditText mEdGasPrice;
    private EditText mEdGasLimit;
    private EditText mAmount;
    private int gasPrice;
    private int gasLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        mEdAddress = findViewById(R.id.ed_address);
        mEdGasPrice = findViewById(R.id.ed_gas_price);
        mEdGasLimit = findViewById(R.id.ed_gas_limit);
        mAmount = findViewById(R.id.ed_amount);
        ImageView priceUpArrow = findViewById(R.id.price_up_arrow);
        ImageView priceDownArrow = findViewById(R.id.price_down_arrow);
        ImageView limitUpArrow = findViewById(R.id.limit_up_arrow);
        ImageView limitDownArrow = findViewById(R.id.limit_down_arrow);
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        mBackArrow.setVisibility(View.VISIBLE);
        mToolBarTittle.setText("Send Transaction");
        mBackArrow.setOnClickListener(this);
        priceUpArrow.setOnClickListener(this);
        priceDownArrow.setOnClickListener(this);
        limitUpArrow.setOnClickListener(this);
        limitDownArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.btn_save:
                sendIndividualTransaction();
               /* if (mEdAddress.getText().toString().isEmpty()) {
                    Toasty.error(this, getString(R.string.empty_wallet), Toast.LENGTH_SHORT).show();
                } else if (mAmount.getText().toString().isEmpty()) {
                    Toasty.error(this, getString(R.string.empty_amount), Toast.LENGTH_SHORT).show();
                } else if (mAmount.getText().toString().equals("0")) {
                    Toasty.error(this, getString(R.string.valid_amount), Toast.LENGTH_SHORT).show();
                } else {
                    sendIndividualTransaction();
                }*/
                break;
            case R.id.price_up_arrow:
                gasPrice = Integer.parseInt(mEdGasPrice.getText().toString());
                gasPrice = gasPrice + 1;
                mEdGasPrice.setText(String.valueOf(gasPrice));
                break;
            case R.id.price_down_arrow:
                gasPrice = Integer.parseInt(mEdGasPrice.getText().toString());
                gasPrice = gasPrice - 1;
                mEdGasPrice.setText(String.valueOf(gasPrice));
                break;
            case R.id.limit_up_arrow:
                gasLimit = Integer.parseInt(mEdGasLimit.getText().toString());
                gasLimit = gasLimit + 1;
                mEdGasLimit.setText(String.valueOf(gasLimit));
                break;
            case R.id.limit_down_arrow:
                gasLimit = Integer.parseInt(mEdGasLimit.getText().toString());
                gasLimit = gasLimit - 1;
                mEdGasLimit.setText(String.valueOf(gasLimit));
                break;
        }
    }

    private void sendIndividualTransaction() {
        Log.i("TAG", "Hash" + "Hi");
        // TODO: Sending an Ether transaction
        // TODO: Now I am using Convert.Unit.ETHER Sometime Android webj3 “insufficient funds for gas * price + value” exception
        /*BigInteger gasprice = BigInteger.valueOf(Long.parseLong(mEdGasPrice.getText().toString()));
        Log.i("TAG", "gasprice:::" + gasprice);
        BigInteger gaslimit = BigInteger.valueOf(Long.parseLong(mEdGasLimit.getText().toString()));
        Log.i("TAG", "gaslimit:::" + gaslimit);
        // TODO: get the next available nonce
        EthGetTransactionCount ethGetTransactionCount;
        try {
            ethGetTransactionCount = web3.ethGetTransactionCount(
                    credentials.getAddress(), DefaultBlockParameterName.LATEST).
                    sendAsync().get();
            // TODO: create our transaction
            BigDecimal etherBalance = Convert.toWei(BigDecimal.valueOf(Double.parseDouble(
                    mAmount.getText().toString())), Convert.Unit.ETHER);
            BigInteger bi = etherBalance.toBigInteger();
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                    ethGetTransactionCount.getTransactionCount(), gasprice, gaslimit,
                    mEdAddress.getText().toString(), bi);
            // TODO: sign & send our transaction
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
            String transactionHash = ethSendTransaction.getTransactionHash();
            Log.i("TAG", "Transaction Hash" + transactionHash);
            Toasty.success(getApplicationContext(), getString(R.string.transaction_send), Toast.LENGTH_SHORT).show();
                finish();*/
        runOnUiThread(() -> {
            BigInteger privateKey = new BigInteger(SharedHelper.getKey(SendTransactionActivity.this, Common.Constants.privateKey), 16);
            BigInteger publicKey = new BigInteger(SharedHelper.getKey(SendTransactionActivity.this, Common.Constants.publicKey), 16);
            ECKeyPair ecKeyPair = new ECKeyPair(privateKey, publicKey);
            Credentials credentials = Credentials.create(ecKeyPair);
            TransactionReceipt transactionReceipt;
            try {
                transactionReceipt = Transfer.sendFunds(web3,
                        credentials, "0xcfF997Ac428D3986c32aB45Cb80FB780301319F4",
                        BigDecimal.valueOf(0.1),
                        Convert.Unit.ETHER).sendAsync().get();
                String transactionHash = transactionReceipt.getTransactionHash();
                Log.i("TAG", "Transaction Hash" + transactionHash);
                Toasty.success(getApplicationContext(), getString(R.string.transaction_send), Toast.LENGTH_SHORT).show();
                finish();
            } catch (ExecutionException | InterruptedException | IOException | TransactionException e) {
                e.printStackTrace();
                Log.i("TAG", "exception" + e.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
