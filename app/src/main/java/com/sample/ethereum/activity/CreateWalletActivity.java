package com.sample.ethereum.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.sample.ethereum.BuildConfig;
import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.network.APIClient;
import com.sample.ethereum.network.ApiInterface;
import com.sample.ethereum.response.CurrentEthereumValue;
import com.sample.ethereum.response.EtherResult;
import com.sample.ethereum.response.EthereumBalance;
import com.sample.ethereum.response.TokenResponse;
import com.sample.ethereum.utils.Common;
import com.sample.ethereum.utils.NetworkUtils;

import org.ethereum.geth.Account;
import org.ethereum.geth.Geth;
import org.ethereum.geth.KeyStore;
import org.web3j.abi.datatypes.Int;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.exceptions.MessageDecodingException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sample.ethereum.utils.Common.dismissProgressbar;
import static com.sample.ethereum.utils.Common.showProgressbar;


public class CreateWalletActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mAccountAddress;
    private File myDownload;
    private String baseUrl;
    private String walletUrl;
    private KeyStore mKeyStore;
    private Account account;
    private TextView mCoinName;
    private TextView mUsdAmount;
    private TextView mCurrentAmount;
    private RecyclerView rvTransaction;
    private TextView emptyTransaction;
    private TextView showMore;
    private ImageView qrCodeImage;
    private Bitmap bitmap;
    public final static int QRcodeWidth = 100;
    public final static int QRcodeHeight = 100;
    private static final String HEX_PREFIX = "0x";
    private List<EtherResult> mEtherList = new ArrayList<>();
    private TransactionAdapter mTransactionAdapter;
    public static Web3j web3 = Web3jFactory.build(new
            HttpService("https://ropsten.infura.io/v3/549de8176ea04780b1dbeaa33cc50fc8"));

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        qrCodeImage = findViewById(R.id.iv_qr_code);
        mAccountAddress = findViewById(R.id.tv_acc_address);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mCoinName = findViewById(R.id.tv_coin_name);
        mCurrentAmount = findViewById(R.id.tv_current_amount);
        mUsdAmount = findViewById(R.id.tv_usd_amount);
        rvTransaction = findViewById(R.id.rv_transaction);
        emptyTransaction = findViewById(R.id.tv_empty_transaction);
        showMore = findViewById(R.id.tv_show_more);
        Button btnReceive = findViewById(R.id.btn_receive);
        Button btnSend = findViewById(R.id.btn_send);
        setSupportActionBar(toolbar);
        mBackArrow.setOnClickListener(this);
        btnReceive.setOnClickListener(this);
        btnSend.setOnClickListener(this);
        showMore.setOnClickListener(this);
        mAccountAddress.setOnClickListener(this);


        // TODO: Get values from previous screen
        int mPrivateKeyInt = getIntent().getIntExtra(Common.Constants.private_key, 0);
        String keyValue = getIntent().getStringExtra(Common.Constants.key_value);
        String url = getIntent().getStringExtra(Common.Constants.url);
        if (mPrivateKeyInt == 3) {
            // TODO: Keystore Wallet
            mToolBarTittle.setText(getString(R.string.keystore_wallet));
            Credentials credentials;
            try {
                credentials = WalletUtils.loadCredentials(keyValue, url);
                if (credentials != null) {
                    String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
                    String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
                    SharedHelper.putKey(this, Common.Constants.address, credentials.getAddress());
                    SharedHelper.putKey(this, Common.Constants.privateKey, String.valueOf(privateKey));
                    SharedHelper.putKey(this, Common.Constants.publicKey, String.valueOf(publicKey));
                    mAccountAddress.setText(SharedHelper.getKey(this, Common.Constants.address));
                    currentEther();
                }
            } catch (IOException | CipherException e) {
                e.printStackTrace();
                if (e.getMessage().equalsIgnoreCase(getString(R.string.invalid_password_provided))) {
                    Toasty.error(CreateWalletActivity.this, getString(R.string.invalid_password_provided),
                            Toast.LENGTH_SHORT).show();
                }
            }

        } else if (mPrivateKeyInt == 1) {
            mToolBarTittle.setText(getString(R.string.import_wallet));

            // TODO: How to get public key from private key in Web3j
            String public_key = getPublicKeyInHex(keyValue);
            SharedHelper.putKey(this, Common.Constants.publicKey, public_key);
            SharedHelper.putKey(this, Common.Constants.privateKey, keyValue);
            try {
                BigInteger privateKey = new BigInteger(SharedHelper.getKey(CreateWalletActivity.this,
                        Common.Constants.privateKey), 16);
                BigInteger publicKey = new BigInteger(SharedHelper.getKey(CreateWalletActivity.this,
                        Common.Constants.publicKey), 16);
                ECKeyPair ecKeyPair = new ECKeyPair(privateKey, publicKey);
                Credentials credentials = Credentials.create(ecKeyPair);
                SharedHelper.putKey(this, Common.Constants.address, credentials.getAddress());
                // TODO:  get Accurate Ethereum Balance
                EthGetBalance ethGetBalance = web3
                        .ethGetBalance(SharedHelper.getKey(this, Common.Constants.address),
                                DefaultBlockParameterName.LATEST).sendAsync().get();
                BigInteger wei = ethGetBalance.getBalance();
                final BigDecimal etherBalance = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
                System.out.println("Account Address : " + etherBalance);
                mAccountAddress.setText(SharedHelper.getKey(this, Common.Constants.address));
                currentEther();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mToolBarTittle.setText(getString(R.string.create_wallet));
            String password = getIntent().getStringExtra(Common.Constants.password);
            SharedHelper.putKey(CreateWalletActivity.this, Common.Constants.password, password);

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

    private void currentEther() {
        if (NetworkUtils.isNetworkConnected(this)) {
            mAccountAddress.setText(SharedHelper.getKey(this, Common.Constants.address));
            ApiInterface service = APIClient.getAPIClient();
            Call<List<CurrentEthereumValue>> etherCall = service.getCurrentEther();
            etherCall.enqueue(new Callback<List<CurrentEthereumValue>>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<List<CurrentEthereumValue>> call,
                                       @NonNull Response<List<CurrentEthereumValue>> response) {
                    if (response.isSuccessful()) {
                        List<CurrentEthereumValue> value = response.body();
                        if (value != null) {
                            mCoinName.setText(value.get(0).getSymbol());
                            SharedHelper.putKey(CreateWalletActivity.this, "symbol", mCoinName.getText().toString());
                            mCurrentAmount.setText(Common.round(Double.parseDouble(getCurrentEtherAmount()), 2) + " " +
                                    SharedHelper.getKey(CreateWalletActivity.this, "symbol"));
                            String usd_price = value.get(0).getPriceUsd();
                            SharedHelper.putKey(CreateWalletActivity.this, "usd_price", usd_price);
                            String currentEther = getCurrentEtherAmount();
                            String price = String.valueOf(Double.parseDouble(usd_price) * Double.parseDouble(currentEther));
                            mUsdAmount.setText("$" + " " + Common.round(Double.parseDouble(price), 2) + " " + "USD");
                        }
                    }
                    try {
                        bitmap = TextToImageEncode(SharedHelper.getKey(CreateWalletActivity.this, Common.Constants.address));
                        qrCodeImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<CurrentEthereumValue>> call, @NonNull Throwable t) {
                    // Un Used
                    Toasty.error(CreateWalletActivity.this, t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toasty.info(CreateWalletActivity.this, getString(R.string.check_your_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getTransactionCount() {
        if (NetworkUtils.isNetworkConnected(CreateWalletActivity.this)) {
            showProgressbar(this);
            ApiInterface service = APIClient.getAPIClient();
            Call<EthereumBalance> ethereumBalanceCall = service.getTransactionList("account",
                    "txlist", SharedHelper.getKey(this, Common.Constants.address), "asc", BuildConfig.API_KEY);
            ethereumBalanceCall.enqueue(new Callback<EthereumBalance>() {
                @Override
                public void onResponse(@NonNull Call<EthereumBalance> call,
                                       @NonNull Response<EthereumBalance> response) {
                    dismissProgressbar();
                    EthereumBalance ethereumBalance = response.body();
                    mEtherList = Objects.requireNonNull(ethereumBalance).getResult();
                    if (mEtherList != null && !mEtherList.isEmpty()) {
                        rvTransaction.setVisibility(View.VISIBLE);
                        emptyTransaction.setVisibility(View.GONE);
                        showMore.setVisibility(View.VISIBLE);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        rvTransaction.setLayoutManager(layoutManager);
                        mTransactionAdapter = new TransactionAdapter(
                                CreateWalletActivity.this, mEtherList,
                                SharedHelper.getKey(CreateWalletActivity.this, Common.Constants.address),
                                mTransactionListener, 0);
                        rvTransaction.setAdapter(mTransactionAdapter);
                        mTransactionAdapter.notifyDataSetChanged();
                    } else {
                        rvTransaction.setVisibility(View.GONE);
                        showMore.setVisibility(View.GONE);
                        emptyTransaction.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EthereumBalance> call, @NonNull Throwable t) {
                    dismissProgressbar();
                    Toasty.error(CreateWalletActivity.this, getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toasty.info(CreateWalletActivity.this, getString(R.string.check_your_internet_connection),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentEtherAmount() {
        // TODO:  get Accurate Ethereum Balance
        EthGetBalance ethGetBalance;
        BigDecimal etherBalance = null;
        String etherBal = null;
        try {
            ethGetBalance = web3
                    .ethGetBalance(SharedHelper.getKey(this, Common.Constants.address),
                            DefaultBlockParameterName.LATEST).sendAsync().get();
            BigInteger wei = ethGetBalance.getBalance();
            // String value = String.valueOf(decodeQuantity(wei.toString()));
            etherBalance = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
            etherBal = etherBalance.toString();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return etherBal;
    }

    public static BigInteger decodeQuantity(String value) {
        if (!isValidHexQuantity(value)) {
            throw new MessageDecodingException("Value must be in format 0x[1-9]+[0-9]* or 0x0");
        }
        try {
            return new BigInteger(value.substring(2), 16);
        } catch (NumberFormatException e) {
            throw new MessageDecodingException("Negative ", e);
        }
    }

    private static boolean isValidHexQuantity(String value) {
        if (value == null) {
            return false;
        }
        if (value.length() < 3) {
            return false;
        }
        return value.startsWith(HEX_PREFIX);
    }

    @SuppressLint("SetTextI18n")
    public void walletCreation() throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        if (SharedHelper.getKey(this, Common.Constants.address).equals("")) {
            myDownload = new File(Environment.getExternalStoragePublicDirectory(Environment.
                    DIRECTORY_DOWNLOADS) + "/NewWallet");
            myDownload.mkdirs();
            mKeyStore = new KeyStore(myDownload.getAbsolutePath(), Geth.LightScryptN, Geth.LightScryptP);
            String fileName = WalletUtils.generateLightNewWalletFile(SharedHelper.getKey(CreateWalletActivity.this, "password"),
                    new File(String.valueOf(myDownload)));
            File file = new File(myDownload, fileName);
            file.createNewFile();
            printAddress(fileName);
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
                                SharedHelper.getKey(CreateWalletActivity.this, Common.Constants.password),
                                baseUrl);
                String address = account.getAddress().getHex();
                String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
                String publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
                SharedHelper.putKey(this, Common.Constants.address, address);
                SharedHelper.putKey(this, Common.Constants.privateKey, String.valueOf(privateKey));
                SharedHelper.putKey(this, Common.Constants.publicKey, String.valueOf(publicKey));
                Log.i("TAG", "address" + address);
                Log.i("TAG", "private_key" + privateKey);
                Log.i("TAG", "public_key" + publicKey);
                mAccountAddress.setText(address);
                currentEther();
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
            case R.id.tv_acc_address:
                // TODO: Get the Operation System SDK version as an int
                int sdkVer = android.os.Build.VERSION.SDK_INT;
                //For Older Android SDK versions
                if (sdkVer < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    @SuppressWarnings("deprecation")
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(mAccountAddress.getText().toString());
                }
                // TODO: For Newer Versions
                else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Message", mAccountAddress.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                Toasty.success(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_show_more:
                Intent transactionIntent = new Intent(CreateWalletActivity.this, TransactionActivity.class);
                transactionIntent.putExtra(Common.Constants.account_address, mAccountAddress.getText().toString());
                startActivity(transactionIntent);
                break;
            case R.id.btn_send:
                Intent intent = new Intent(this, SendTransactionActivity.class);
                startActivity(intent);
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

    // To Pass Private Key get Public Key Value.
    public static String getPublicKeyInHex(String privateKeyInHex) {
        BigInteger privateKeyInBT = new BigInteger(privateKeyInHex, 16);
        ECKeyPair aPair = ECKeyPair.create(privateKeyInBT);
        BigInteger publicKeyInBT = aPair.getPublicKey();
        return publicKeyInBT.toString(16);
    }

    private CreateWalletActivity.CreateTransactionListencer mTransactionListener = etherResult -> {
        Intent intent = new Intent(CreateWalletActivity.this, TransactionDetailsActivity.class);
        intent.putExtra(Common.Constants.etherResult, etherResult);
        intent.putExtra(Common.Constants.transaction, SharedHelper.getKey(
                CreateWalletActivity.this, Common.Constants.address));
        startActivity(intent);
    };

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Get Current Ether Amount from CoinMarketCap Api.
        currentEther();
        getTransactionCount();
    }

    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.QR_CODE,
                    QRcodeWidth, QRcodeHeight, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, 100, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public interface CreateTransactionListencer {
        void createWalletClicked(EtherResult etherResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add_token) {
            Intent settingsIntent = new Intent(CreateWalletActivity.this, TokenActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.menu_logout) {
            SharedHelper.clearSharedPreferences(CreateWalletActivity.this);
            SharedHelper.putKey(this, Common.Constants.address, "");
            SharedHelper.putKey(this, Common.Constants.privateKey, "");
            SharedHelper.putKey(this, Common.Constants.publicKey, "");
            Intent intent = new Intent(CreateWalletActivity.this, EtherActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
