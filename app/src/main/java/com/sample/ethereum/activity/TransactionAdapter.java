package com.sample.ethereum.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sample.ethereum.R;
import com.sample.ethereum.response.EtherResult;
import com.sample.ethereum.utils.Common;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<EtherResult> mList;
    private String mTransAddress;
    private int mPosition = 0;
    private CreateWalletActivity.CreateTransactionListencer createListencer;
    private TransactionActivity.TransactionListener transactionListencer;

    TransactionAdapter(Context mContext, List<EtherResult> mEtherList,
                       String transactionAddress,
                       CreateWalletActivity.CreateTransactionListencer mTransactionListener, int i) {
        context = mContext;
        mList = mEtherList;
        mTransAddress = transactionAddress;
        createListencer = mTransactionListener;
        mPosition = i;
    }

    TransactionAdapter(TransactionActivity mContext,
                       List<EtherResult> mEtherList,
                       String transactionAddress,
                       TransactionActivity.TransactionListener mTransListener,
                       int i) {

        context = mContext;
        mList = mEtherList;
        mTransAddress = transactionAddress;
        transactionListencer = mTransListener;
        mPosition = i;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_transaction_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final EtherResult etherResult = mList.get(position);
        if (etherResult != null) {
            holder.mTimeStamp.setText(Common.getDate(
                    Long.parseLong(etherResult.getTimeStamp())));
            String from = etherResult.getFrom();
            if (from != null)
                if (mTransAddress.equals(from)) {
                    holder.mTransStatus.setText(context.getResources().getString(R.string.send));
                    holder.mTransStatus.setTextColor(context.getResources().getColor(R.color.red));
                } else {
                    holder.mTransStatus.setText(context.getResources().getString(R.string.received));
                    holder.mTransStatus.setTextColor(context.getResources().getColor(R.color.green));
                }
            if (etherResult.getValue() != null) {
                String wei = etherResult.getValue();
                BigDecimal etherBalance = Convert.fromWei(wei, Convert.Unit.ETHER);
                holder.mTransAmount.setText(etherBalance.toString() + " " +
                        context.getResources().getString(R.string.eth));
            }
            if (etherResult.getHash() != null)
                holder.mTransHash.setText(etherResult.getHash());
            if(mPosition == 0) {
                holder.lnrTransaction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createListencer.createWalletClicked(etherResult);
                    }
                });
            } else {
                holder.lnrTransaction.setOnClickListener(v ->
                        transactionListencer.transactionClicked(etherResult));
            }

        }
    }

    @Override
    public int getItemCount() {
        if (mPosition == 0) {
            return 1;
        } else {
            return mList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTimeStamp, mTransStatus, mTransAmount, mTransHash;
        private LinearLayout lnrTransaction;

        ViewHolder(View view) {
            super(view);

            mTimeStamp = view.findViewById(R.id.tv_time_stamp);
            mTransStatus = view.findViewById(R.id.transaction_status);
            mTransAmount = view.findViewById(R.id.transaction_amount);
            mTransHash = view.findViewById(R.id.transaction_hash);
            lnrTransaction = view.findViewById(R.id.lnrTransactionContainer);
        }
    }
}
