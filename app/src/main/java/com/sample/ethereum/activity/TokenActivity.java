package com.sample.ethereum.activity;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.sample.ethereum.response.TokenResponse;
import com.sample.ethereum.utils.Common;
import com.sample.ethereum.utils.NetworkUtils;

import java.util.ArrayList;
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
    private String contractAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        EditText mEdTokenAddress = findViewById(R.id.ed_token_address);
        mTokenSymbol = findViewById(R.id.token_symbol);
        mTokenDecimal = findViewById(R.id.token_decimal);
        mSubmit = findViewById(R.id.token_submit);
        lnrTokenContainer = findViewById(R.id.lnrAddToken);
        lnrTokenBalContainer = findViewById(R.id.lnrTokenBalContainer);
        tvTokenBal = findViewById(R.id.tv_token_bal);
        tvEthTokenBal = findViewById(R.id.tv_eth_bal);

        mBackArrow.setVisibility(View.VISIBLE);
        mToolBarTittle.setText(getString(R.string.add_token));
        mBackArrow.setOnClickListener(this);
        mSubmit.setOnClickListener(this);

        try {
            if (SharedHelper.getListKey(TokenActivity.this, "addressList") == null) {
                address.add("0x0ba2235e47fe9f4c2d4db64beb9f5f73");
            } else {
                address = SharedHelper.getListKey(TokenActivity.this, "addressList");
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
                String editable = s.toString();
                if (editable.length() > 0) {
                    if (!address.isEmpty()) {
                        for (int i = 0; i < address.size(); i++) {
                            contractAddress = address.get(i);
                        }
                        if (contractAddress.equalsIgnoreCase(
                                "0xBe15E18924513829cFf5502D6D2806eb2F3d392b")) {
                            Toasty.error(TokenActivity.this, "Token has already been added",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (NetworkUtils.isNetworkConnected(TokenActivity.this)) {
                                showProgressbar(TokenActivity.this);
                                ApiInterface service = APIClient.getAPIClient();
                                Call<TokenResponse> tokenResponseCall = service.
                                        getTokenAddress(editable,
                                                SharedHelper.getKey(TokenActivity.this, "address"));
                                tokenResponseCall.enqueue(new Callback<TokenResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<TokenResponse> call,
                                                           @NonNull Response<TokenResponse> response) {
                                        dismissProgressbar();
                                        if (response.isSuccessful()) {
                                            TokenResponse tokenResponse = response.body();
                                            if (tokenResponse != null) {
                                                if (tokenResponse.getMessage() != null &&
                                                        tokenResponse.getMessage().equals("no contract code at given address")) {
                                                    Toasty.error(TokenActivity.this, "Invalid address",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    address.add("0xBe15E18924513829cFf5502D6D2806eb2F3d392b");
                                                    SharedHelper.putListKey(TokenActivity.this, "addressList", address);
                                                    mTokenSymbol.setText(tokenResponse.getSymbol());
                                                    mTokenDecimal.setText(String.valueOf(tokenResponse.getDecimals()));
                                                    mSubmit.setEnabled(true);
                                                    tokenBal = tokenResponse.getBalance();
                                                    tokenName = tokenResponse.getSymbol();
                                                    ethBal = tokenResponse.getEthBalance();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<TokenResponse> call,
                                                          @NonNull Throwable t) {
                                        dismissProgressbar();
                                        Toasty.error(TokenActivity.this, "Something went wrong!!!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toasty.info(TokenActivity.this, "Please check your internet connection",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toasty.error(TokenActivity.this, "Please enter token address",
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
        tvEthTokenBal.setText(round(Double.parseDouble(ethBal), 3) + " " + getString(R.string.eth));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.token_submit:
                showTokenBal();
                break;
        }
    }
}
