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
import com.sample.ethereum.utils.Common;
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
    private String transactionAddress;
    private List<EtherResult> mEtherList = new ArrayList<>();
    private TransactionAdapter mTransactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        mBackArrow.setVisibility(View.VISIBLE);
        mToolBarTittle.setText(getString(R.string.transaction));

        transactionAddress = getIntent().getStringExtra(Common.Constants.account_address);
        mBackArrow.setOnClickListener(this);
        initViews();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_trans);
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
                        mTransactionAdapter = new TransactionAdapter(TransactionActivity.this,
                                mEtherList, transactionAddress,
                                 mTransListener,2);
                        recyclerView.setAdapter(mTransactionAdapter);
                        mTransactionAdapter.notifyDataSetChanged();
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<EthereumBalance> call, @NonNull Throwable t) {
                    dismissProgressbar();
                    Toasty.error(TransactionActivity.this, getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toasty.info(TransactionActivity.this, getString(R.string.check_your_internet_connection),
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

    private TransactionActivity.TransactionListener mTransListener = new TransactionActivity.TransactionListener() {
        @Override
        public void transactionClicked(EtherResult etherResult) {
            Intent intent = new Intent(TransactionActivity.this, TransactionDetailsActivity.class);
            intent.putExtra(Common.Constants.etherResult, etherResult);
            intent.putExtra(Common.Constants.transaction, transactionAddress);
            TransactionActivity.this.startActivity(intent);
        }
    };
}
