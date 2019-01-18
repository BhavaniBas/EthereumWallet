package com.sample.ethereum.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.network.APIClient;
import com.sample.ethereum.network.ApiInterface;
import com.sample.ethereum.response.Token;
import com.sample.ethereum.response.TokenResponse;
import com.sample.ethereum.utils.Common;
import com.sample.ethereum.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sample.ethereum.utils.Common.dismissProgressbar;
import static com.sample.ethereum.utils.Common.round;
import static com.sample.ethereum.utils.Common.showProgressbar;

public class TokenActivity extends AppCompatActivity implements View.OnClickListener {

    private List<String> address = new ArrayList<>();
    private TextView mTokenSymbol;
    private TextView mTokenDecimal;
    private Button mSubmit;
    private LinearLayout lnrTokenContainer;
    private LinearLayout lnrTokenBalContainer;
    private String tokenBal;
    private String tokenName;
    private String ethBal;
    private TextView tvTokenBal;
    private TextView tvEthTokenBal;
    private List<Token> mTokenList = new ArrayList<>();
    private List<Token> mToken = new ArrayList<>();
    ArrayList<String> values = new ArrayList<>();
    ArrayList<String> values1 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        EditText mEdTokenAddress = findViewById(R.id.ed_token_address);
        mTokenSymbol = findViewById(R.id.token_symbol);
        mTokenDecimal = findViewById(R.id.token_decimal);
        Button tokenDone = findViewById(R.id.token_done);
        mSubmit = findViewById(R.id.token_submit);
        lnrTokenContainer = findViewById(R.id.lnrAddToken);
        lnrTokenBalContainer = findViewById(R.id.lnrTokenBalContainer);
        tvTokenBal = findViewById(R.id.tv_token_bal);
        tvEthTokenBal = findViewById(R.id.tv_eth_bal);

        mBackArrow.setVisibility(View.VISIBLE);
        mToolBarTittle.setText(getString(R.string.add_token));
        mBackArrow.setOnClickListener(this);
        mSubmit.setOnClickListener(this);
        tokenDone.setOnClickListener(this);

        try {
            if (SharedHelper.getListKey(TokenActivity.this, Common.Constants.addressList) == null) {
                address.add("0x0ba2235e47fe9f4c2d4db");
            } else {
                address = SharedHelper.getListKey(TokenActivity.this, Common.Constants.addressList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mEdTokenAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Un Used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Un Used
            }

            @Override
            public void afterTextChanged(Editable s) {
                String editable = s.toString().trim();
                if (editable.length() > 0) {
                    HashSet<String> hashSet = new HashSet<>(address);
                    values1.clear();
                    values1.addAll(hashSet);
                    if (!values1.isEmpty()) {
                        boolean verify = false;
                        for (int i = 0; i < values1.size(); i++) {
                            if (values1.get(i).equalsIgnoreCase(s.toString())) {
                                verify = true;
                                Toasty.error(TokenActivity.this, getString(R.string.token_already_added),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!verify) {
                            if (NetworkUtils.isNetworkConnected(TokenActivity.this)) {
                                showProgressbar(TokenActivity.this);
                                ApiInterface service = APIClient.getAPIClient();
                                Call<TokenResponse> tokenResponseCall = service.getTokenAddress(editable,
                                        SharedHelper.getKey(TokenActivity.this, Common.Constants.address));
                                tokenResponseCall.enqueue(new Callback<TokenResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<TokenResponse> call,
                                                           @NonNull Response<TokenResponse> response) {
                                        dismissProgressbar();
                                        if (response.isSuccessful()) {
                                            TokenResponse tokenResponse = response.body();
                                            if (tokenResponse != null) {
                                                address.add(tokenResponse.getToken());
                                                HashSet<String> hashSet = new HashSet<>(address);
                                                values.clear();
                                                values.addAll(hashSet);
                                                SharedHelper.putListKey(TokenActivity.this, Common.Constants.addressList, values);
                                                mTokenSymbol.setText(tokenResponse.getSymbol());
                                                mTokenDecimal.setText(String.valueOf(tokenResponse.getDecimals()));
                                                mSubmit.setEnabled(true);
                                                tokenBal = tokenResponse.getBalance();
                                                tokenName = tokenResponse.getSymbol();
                                                ethBal = tokenResponse.getEthBalance();
                                            }
                                        } else {
                                            if (response.code() == 404) {
                                                Toasty.error(TokenActivity.this, getString(R.string.invalid_address),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<TokenResponse> call,
                                                          @NonNull Throwable t) {
                                        dismissProgressbar();
                                        Toasty.error(TokenActivity.this, getString(R.string.something_went_wrong),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toasty.info(TokenActivity.this, getString(R.string.check_your_internet_connection),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toasty.error(TokenActivity.this, getString(R.string.enter_token_address),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showTokenBal() {
        lnrTokenContainer.setVisibility(View.GONE);
        lnrTokenBalContainer.setVisibility(View.VISIBLE);
        tvTokenBal.setText(tokenBal + " " + tokenName);
        tvEthTokenBal.setText(round(Double.parseDouble(ethBal), 2) + " " + getString(R.string.eth));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                Intent intent = new Intent(this, TokenListActivity.class);
                intent.putExtra("tokenName", "");
                startActivity(intent);
                finish();
                break;
            case R.id.token_submit:
                showTokenBal();
                break;
            case R.id.token_done:
                mTokenList.clear();
                mTokenList.add(new Token(mTokenSymbol.getText().toString(), false));
                if (mTokenList != null && !mTokenList.isEmpty()) {
                    mToken = SharedHelper.getTokenListKey(this, "tokenList");
                    mToken.addAll(mTokenList);
                    SharedHelper.putTokenListKey(this, "tokenList", mToken);
                    Toasty.success(this, "Token Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent doneIntent = new Intent(this, TokenListActivity.class);
        doneIntent.putExtra("tokenName", "");
        startActivity(doneIntent);
        finish();
    }
}
