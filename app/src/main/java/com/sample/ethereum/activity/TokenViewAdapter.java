package com.sample.ethereum.activity;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sample.ethereum.R;
import com.sample.ethereum.response.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TokenViewAdapter extends ArrayAdapter<Token> {

    private Context mContext;
    private int selectedPosition = -1;
    private boolean isCheck = false;
    private boolean mSelection;
    private TokenListActivity.AddTokenListencer mAddTokenListencer;

    TokenViewAdapter(Activity context, int resource, List<Token> objects,
                     TokenListActivity.AddTokenListencer addTokenListencer, boolean isSelection) {
        super(context, resource, objects);
        this.mContext = context;
        mSelection = isSelection;
        mAddTokenListencer = addTokenListencer;
        List<Token> mTokenList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_token_list_items, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mCheckBox.setTag(getItem(position)); // This line is important.
        if(!Objects.requireNonNull(getItem(position)).getTokenName().equalsIgnoreCase("")||
                !Objects.requireNonNull(getItem(position)).equals("null")) {
            holder.tvTokenName.setText(Objects.requireNonNull(getItem(position)).getTokenName());
            if(mSelection) {
                if (position == selectedPosition) {
                    holder.mCheckBox.setChecked(true);
                } else {
                    holder.mCheckBox.setChecked(false);
                }
                if (holder.tvTokenName.getText().equals(mContext.getString(R.string.ethereum)) && !isCheck) {
                    holder.mCheckBox.setChecked(Objects.requireNonNull(getItem(position)).getIsSelected());
                }
            } else {
                if(getItem(position).getIsSelected()) {
                    holder.mCheckBox.setChecked(true);
                } else {
                    holder.mCheckBox.setChecked(false);
                }
            }
            holder.mCheckBox.setOnClickListener(onStateChangedListener(holder.mCheckBox, position));
        }
        return convertView;
    }

    private View.OnClickListener onStateChangedListener(final CheckBox checkBox, final int position) {
        return v -> {
            isCheck = true;
            if (checkBox.isChecked()) {
                selectedPosition = position;
                mAddTokenListencer.tokenClicked(getItem(position));
            } else {
                selectedPosition = -1;
                mAddTokenListencer.tokenClicked(getItem(position));
            }
            notifyDataSetChanged();
        };

    }


    private static class ViewHolder {

        private TextView tvTokenName;
        private CheckBox mCheckBox;
        private LinearLayout mLayout;

        ViewHolder(View view) {

            mLayout = view.findViewById(R.id.layout);
            mCheckBox = view.findViewById(R.id.checkBox);
            tvTokenName = view.findViewById(R.id.tv_token_name);
        }
    }

}
