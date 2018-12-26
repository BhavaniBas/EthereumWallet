package com.sample.ethereum.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.ethereum.BuildConfig;
import com.sample.ethereum.R;
import com.sample.ethereum.network.APIClient;
import com.sample.ethereum.network.ApiInterface;
import com.sample.ethereum.response.EtherResult;
import com.sample.ethereum.response.EthereumBalance;
import com.sample.ethereum.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sample.ethereum.utils.Common.dismissProgressbar;
import static com.sample.ethereum.utils.Common.showProgressbar;

public class TransactionActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private TextView mNoData;
    private String transactionAddress;
    private List<EtherResult> mEtherList = new ArrayList<>();
    private TransactionAdapter mTransactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        mNoData = findViewById(R.id.tv_no_data);
        mBackArrow.setVisibility(View.VISIBLE);
        mToolBarTittle.setText(getString(R.string.transaction));
        transactionAddress = getIntent().getStringExtra("account_address");
        mBackArrow.setOnClickListener(this);
        initViews();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_transaction);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getTransactionCount();
    }

    private void getTransactionCount() {
        if (NetworkUtils.isNetworkConnected(TransactionActivity.this)) {
            showProgressbar(this);
            ApiInterface service = APIClient.getAPIClient();
            Call<EthereumBalance> ethereumBalanceCall = service.getTransactionList("account",
                    "txlist", transactionAddress, "asc", BuildConfig.API_KEY);
            ethereumBalanceCall.enqueue(new Callback<EthereumBalance>() {
                @Override
                public void onResponse(@NonNull Call<EthereumBalance> call,
                                       @NonNull Response<EthereumBalance> response) {
                    dismissProgressbar();
                    EthereumBalance ethereumBalance = response.body();
                    mEtherList = Objects.requireNonNull(ethereumBalance).getResult();
                    if (mEtherList != null && !mEtherList.isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        mNoData.setVisibility(View.GONE);
                        mTransactionAdapter = new TransactionAdapter(
                                TransactionActivity.this, mEtherList, transactionAddress,
                                mTransactionListener);
                        recyclerView.setAdapter(mTransactionAdapter);
                        mTransactionAdapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        mNoData.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EthereumBalance> call, @NonNull Throwable t) {
                    dismissProgressbar();
                    Toasty.error(TransactionActivity.this, "Something went wrong!!!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toasty.info(TransactionActivity.this, "Please check your internet connection",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public interface TransactionListener {
        void transactionClicked(EtherResult etherResult);
    }

    private TransactionListener mTransactionListener = (etherResult) -> {
        Intent intent = new Intent(TransactionActivity.this,TransactionDetailsActivity.class);
        intent.putExtra("etherResult",etherResult);
        intent.putExtra("transaction",transactionAddress);
        startActivity(intent);
    };
}
