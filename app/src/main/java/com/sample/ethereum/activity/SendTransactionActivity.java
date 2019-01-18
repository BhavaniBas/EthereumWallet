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
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.sample.ethereum.activity.CreateWalletActivity.web3;

public class SendTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEdAddress;
    private EditText mEdGasPrice;
    private EditText mEdGasLimit;
    private EditText mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_transaction);

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
                if (mEdAddress.getText().toString().isEmpty()) {
                    Toasty.error(this, getString(R.string.empty_wallet), Toast.LENGTH_SHORT).show();
                } else if (mAmount.getText().toString().isEmpty()) {
                    Toasty.error(this, getString(R.string.empty_amount), Toast.LENGTH_SHORT).show();
                } else if (mAmount.getText().toString().equals("0")) {
                    Toasty.error(this, getString(R.string.valid_amount), Toast.LENGTH_SHORT).show();
                } else {
                    sendIndividualTransaction();
                }
                break;
            case R.id.price_up_arrow:
                int gasPrice = Integer.parseInt(mEdGasPrice.getText().toString());
                gasPrice = gasPrice + 1;
                mEdGasPrice.setText(String.valueOf(gasPrice));
                break;
            case R.id.price_down_arrow:
                gasPrice = Integer.parseInt(mEdGasPrice.getText().toString());
                gasPrice = gasPrice - 1;
                mEdGasPrice.setText(String.valueOf(gasPrice));
                break;
            case R.id.limit_up_arrow:
                int gasLimit = Integer.parseInt(mEdGasLimit.getText().toString());
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
        BigInteger privateKey = new BigInteger(SharedHelper.getKey(SendTransactionActivity.this, Common.Constants.privateKey), 16);
        BigInteger publicKey = new BigInteger(SharedHelper.getKey(SendTransactionActivity.this, Common.Constants.publicKey), 16);
        ECKeyPair ecKeyPair = new ECKeyPair(privateKey, publicKey);
        Credentials credentials = Credentials.create(ecKeyPair);
        // TODO: Sending an Ether transaction
        // TODO: Now I am using Convert.Unit.ETHER Sometime Android webj3 “insufficient funds for gas * price + value” exception
        BigDecimal gasPrice = Convert.toWei(BigDecimal.valueOf(Double.parseDouble(
                mEdGasPrice.getText().toString())), Convert.Unit.GWEI);
        BigInteger bi_gas_price = gasPrice.toBigInteger();
        Log.i("TAG", "gasprice:::" + bi_gas_price);
        BigInteger gaslimit = BigInteger.valueOf(Long.parseLong(mEdGasLimit.getText().toString()));
        Log.i("TAG", "gaslimit:::" + gaslimit);

        runOnUiThread(() -> {
            // TODO: get the next available nonce
            EthGetTransactionCount ethGetTransactionCount;
            try {
                ethGetTransactionCount = web3.ethGetTransactionCount(
                        credentials.getAddress(), DefaultBlockParameterName.LATEST).
                        send();
                // TODO: create our transaction
                BigDecimal etherBalance = Convert.toWei(BigDecimal.valueOf(Double.parseDouble(
                        mAmount.getText().toString())), Convert.Unit.ETHER);
                BigInteger bi = etherBalance.toBigInteger();
                RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                        ethGetTransactionCount.getTransactionCount(), bi_gas_price, gaslimit,
                        mEdAddress.getText().toString(), bi);
                // TODO: sign & send our transaction
                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                String hexValue = Numeric.toHexString(signedMessage);
                EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
                String transactionHash = ethSendTransaction.getTransactionHash();
                Log.i("TAG", "Transaction Hash" + transactionHash);
                Toasty.success(getApplicationContext(), getString(R.string.transaction_send), Toast.LENGTH_SHORT).show();
                finish();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.i("TAG", "exception" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
