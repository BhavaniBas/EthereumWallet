package com.sample.ethereum.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.utils.Common;

import org.ethereum.geth.Account;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;


public class CreateWalletActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mAccountAddress;
    private TextView mPrivateKey;
    private TextView mPublicKey;
    private TextView mBalance;
    private File myDownload;
    private String baseUrl;
    private String walletUrl;
    private KeyStore mKeyStore;
    private Account account;
    public static Web3j web3 = Web3jFactory.build(new
            HttpService("https://ropsten.infura.io/v3/549de8176ea04780b1dbeaa33cc50fc8"));
    private TextView mToken;
    private TextView mAddToken;
    private TextView mTransactionList;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        mAccountAddress = findViewById(R.id.tv_acc_address);
        mPrivateKey = findViewById(R.id.tv_private_key);
        mPublicKey = findViewById(R.id.tv_public_key);
        mBalance = findViewById(R.id.tv_bal);
        mToken = findViewById(R.id.tv_token);
        mTransactionList = findViewById(R.id.tv_transaction);
        mAddToken = findViewById(R.id.addToken);
        TextView mLogout = findViewById(R.id.tv_logout);

        mBackArrow.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        mPrivateKey.setOnClickListener(this);
        mTransactionList.setOnClickListener(this);
        mAddToken.setOnClickListener(this);

        // TODO: Get values from previous screen
        int mPrivateKeyInt = getIntent().getIntExtra("private_key", 0);
        String keyValue = getIntent().getStringExtra("key_value");
        if (mPrivateKeyInt == 1) {
            mToolBarTittle.setText(getString(R.string.import_wallet));
            //  lnrTransactionContainer.setVisibility(View.VISIBLE);

            // TODO: How to get public key from private key in Web3j
            mPublicKey.setText(getPublicKeyInHex(keyValue));
            mPrivateKey.setText(keyValue);
            try {
                BigInteger privateKey = new BigInteger(mPrivateKey.getText().toString(), 16);
                BigInteger publicKey = new BigInteger(mPublicKey.getText().toString(), 16);
                ECKeyPair ecKeyPair = new ECKeyPair(privateKey, publicKey);
                Credentials credentials = Credentials.create(ecKeyPair);
                mAccountAddress.setText(credentials.getAddress());
                // TODO:  get Accurate Ethereum Balance
                EthGetBalance ethGetBalance = web3
                        .ethGetBalance(mAccountAddress.getText().toString(),
                                DefaultBlockParameterName.LATEST).sendAsync().get();
                BigInteger wei = ethGetBalance.getBalance();
                final BigDecimal etherBalance = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
                System.out.println("Account Address : " + etherBalance);
                mBalance.setText(Common.round(Double.parseDouble(etherBalance.toString()),3) + " " + "ETH");

                SharedHelper.putKey(this, "address", mAccountAddress.getText().toString());
                SharedHelper.putKey(this, "privateKey", mPrivateKey.getText().toString());
                SharedHelper.putKey(this, "publicKey", mPublicKey.getText().toString());

                // TODO: Transaction Count Calculate current Account Address based on the nonce
                EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                        mAccountAddress.getText().toString(), DefaultBlockParameterName.LATEST).
                        sendAsync().get();
                // TODO: Sending an Ether transaction // Temporarily Hide...
                TransactionReceipt transactionReceipt = Transfer.sendFunds(web3,
                        credentials, "0x7b5481D6BE5956a76Fe83E380C219B00a0339d6f",
                        BigDecimal.valueOf(0.1), Convert.Unit.ETHER).sendAsync().get();
                String transactionHash = transactionReceipt.getTransactionHash();
                String contractAddress = transactionReceipt.getContractAddress();
                Log.i("TAG", "contractAddress Details" + contractAddress);
                // TODO: Transaction Details
                EthTransaction transaction = web3.ethGetTransactionByHash(transactionHash)
                        .sendAsync().get();
                Log.i("TAG", "Transaction Details" + transaction.getTransaction().toString());
              /*  mToken.setText(calculateContractAddress(mAccountAddress.getText().toString(),
                        BigInteger.valueOf(28)));*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mToolBarTittle.setText(getString(R.string.import_wallet));
            String password = getIntent().getStringExtra("password");
            SharedHelper.putKey(CreateWalletActivity.this, "password", password);

            // TODO: Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {

                    try {
                        walletCreation();
                    } catch (CipherException | IOException |
                            NoSuchProviderException | InvalidAlgorithmParameterException |
                            NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    try {
                        walletCreation();
                    } catch (CipherException | InvalidAlgorithmParameterException |
                            NoSuchAlgorithmException | NoSuchProviderException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void getBalance() {

        // TODO:  get Accurate Ethereum Balance
        web3 = Web3jFactory.build(new HttpService("https://ropsten.infura.io/v3/549de8176ea04780b1dbeaa33cc50fc8"));
        EthGetBalance ethGetBalance = null;
        try {
            ethGetBalance = web3
                    .ethGetBalance(SharedHelper.getKey(this, "address"),
                            DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();
            final BigDecimal etherBalance = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
            System.out.println("Account Address : " + etherBalance);
            mAccountAddress.setText(SharedHelper.getKey(CreateWalletActivity.this, "address"));
            mPrivateKey.setText(SharedHelper.getKey(CreateWalletActivity.this, "privateKey"));
            mPublicKey.setText(SharedHelper.getKey(CreateWalletActivity.this, "publicKey"));
            mBalance.setText(etherBalance.toString() + " " + "ETH");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    public void walletCreation() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        if (SharedHelper.getKey(this, "fileName").equals("")) {
            myDownload = new File(Environment.getExternalStoragePublicDirectory(Environment.
                    DIRECTORY_DOWNLOADS) + "/NewWallet");
            myDownload.mkdirs();
            mKeyStore = new KeyStore(myDownload.getAbsolutePath(), Geth.LightScryptN, Geth.LightScryptP);
            String fileName = WalletUtils.generateLightNewWalletFile(SharedHelper.getKey(CreateWalletActivity.this, "password"),
                    new File(String.valueOf(myDownload)));
            File file = new File(myDownload, fileName);
            file.createNewFile();
            SharedHelper.putKey(this, "fileName", fileName);
            printAddress(SharedHelper.getKey(this, "fileName"));
        } else {
            getBalance();
        }
    }

    private void printAddress(String fileName) {
        try {
            if (mKeyStore != null && mKeyStore.getAccounts() != null) {
                for (int i = 0; i < mKeyStore.getAccounts().size(); i++) {
                    account = mKeyStore.getAccounts().get(i);
                    walletUrl = account.getURL();
                }
                String url = myDownload + "/" + fileName;
                if (walletUrl.contains(url)) {
                    baseUrl = walletUrl.substring(11, walletUrl.length());
                }
                Credentials credentials =
                        WalletUtils.loadCredentials(
                                SharedHelper.getKey(CreateWalletActivity.this, "password"),
                                baseUrl);
                String address = account.getAddress().getHex();
                String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
                String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
                SharedHelper.putKey(this, "address", address);
                SharedHelper.putKey(this, "privateKey", String.valueOf(privateKey));
                SharedHelper.putKey(this, "publicKey", String.valueOf(publicKey));
                getBalance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.tv_logout:
                SharedHelper.clearSharedPreferences(CreateWalletActivity.this);
                Intent intent = new Intent(CreateWalletActivity.this, EtherActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_private_key:
                //Get the Operation System SDK version as an int
                int sdkVer = android.os.Build.VERSION.SDK_INT;
                //For Older Android SDK versions
                if (sdkVer < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    @SuppressWarnings("deprecation")
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(mPrivateKey.getText().toString());
                }
                //For Newer Versions
                else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Message", mPrivateKey.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                Toasty.success(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_transaction:
                Intent transactionIntent = new Intent(CreateWalletActivity.this, TransactionActivity.class);
                transactionIntent.putExtra("account_address", mAccountAddress.getText().toString());
                startActivity(transactionIntent);
                break;
            case R.id.addToken:
                Intent tokenIntent = new Intent(CreateWalletActivity.this, TokenActivity.class);
                startActivity(tokenIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public static String getPublicKeyInHex(String privateKeyInHex) {
        BigInteger privateKeyInBT = new BigInteger(privateKeyInHex, 16);
        ECKeyPair aPair = ECKeyPair.create(privateKeyInBT);
        BigInteger publicKeyInBT = aPair.getPublicKey();
        return publicKeyInBT.toString(16);
    }
}
