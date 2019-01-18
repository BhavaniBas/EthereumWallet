package com.sample.ethereum.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.network.APIClient;
import com.sample.ethereum.network.ApiInterface;
import com.sample.ethereum.BuildConfig;
import com.sample.ethereum.response.EthereumBalance;
import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.utils.NetworkUtils;

import org.ethereum.crypto.ECKey;
import org.ethereum.geth.Account;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.spongycastle.jcajce.provider.digest.SHA3;
import org.spongycastle.util.encoders.Hex;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;



import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sample.ethereum.utils.Common.dismissProgressbar;
import static com.sample.ethereum.utils.Common.showProgressbar;


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
        TextView mLogout = findViewById(R.id.tv_logout);

        mBackArrow.setOnClickListener(this);
        mLogout.setOnClickListener(this);
        mPrivateKey.setOnClickListener(this);
        mToolBarTittle.setText(getString(R.string.create_wallet));

        // TODO: Get values from previous screen
        int mPrivateKeyInt = getIntent().getIntExtra("private_key", 0);
        String keyValue = getIntent().getStringExtra("key_value");
        if (mPrivateKeyInt == 1) {
            // TODO: How to get public key from private key in Web3j
            mPublicKey.setText(getPublicKeyInHex(keyValue));
            mPrivateKey.setText(keyValue);
            BigInteger pk = new BigInteger(keyValue, 16);
            ECKey key = ECKey.fromPrivate(pk);
            mAccountAddress.setText(Hex.toHexString(key.getAddress()));
            SharedHelper.putKey(CreateWalletActivity.this,"address",
                    mAccountAddress.getText().toString());
            getBalance(1);
        } else {
            String password = getIntent().getStringExtra("password");
            SharedHelper.putKey(CreateWalletActivity.this, "password", password);

            // TODO: Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    try {
                        walletCreation();
                    } catch (CipherException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    try {
                        walletCreation();
                    } catch (CipherException e) {
                        e.printStackTrace();
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void getBalance(int position) {
        if (NetworkUtils.isNetworkConnected(CreateWalletActivity.this)) {
            showProgressbar(this);
            ApiInterface service = APIClient.getAPIClient();
            Call<EthereumBalance> balanceCall = service.getBalance("account", "balance",
                    SharedHelper.getKey(this, "address"), "latest", BuildConfig.API_KEY);
            balanceCall.enqueue(new Callback<EthereumBalance>() {
                @Override
                public void onResponse(@NonNull Call<EthereumBalance> call,
                                       @NonNull Response<EthereumBalance> response) {
                    dismissProgressbar();
                    if (response.isSuccessful()) {
                        EthereumBalance balance = response.body();
                        if (balance != null) {
                            mBalance.setText(balance.getResult());
                        }
                        if(position == 0)
                        mAccountAddress.setText(SharedHelper.getKey(CreateWalletActivity.this, "address"));
                        mPrivateKey.setText(SharedHelper.getKey(CreateWalletActivity.this, "privateKey"));
                        mPublicKey.setText(SharedHelper.getKey(CreateWalletActivity.this, "publicKey"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EthereumBalance> call, @NonNull Throwable t) {
                    dismissProgressbar();
                    Toasty.error(CreateWalletActivity.this, "Something went wrong!!!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toasty.info(CreateWalletActivity.this, "Please check your internet connection",
                    Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("SetTextI18n")
    public void walletCreation() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        if (SharedHelper.getKey(this, "fileName").equals("")) {
            myDownload = new File(Environment.getExternalStoragePublicDirectory(Environment.
                    DIRECTORY_DOWNLOADS) + "/NewWallet");
            mKeyStore = new KeyStore(myDownload.getAbsolutePath(), Geth.LightScryptN, Geth.LightScryptP);
            String fileName = WalletUtils.generateLightNewWalletFile(SharedHelper.getKey(CreateWalletActivity.this, "password"),
                    new File(String.valueOf(myDownload)));
            SharedHelper.putKey(this, "fileName", fileName);
            printAddress(SharedHelper.getKey(this, "fileName"));
        } else {
            getBalance(0);
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
                getBalance(0);
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
