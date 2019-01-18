package com.sample.ethereum.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sample.ethereum.R;
import com.sample.ethereum.SharedHelper;
import com.sample.ethereum.response.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TokenListActivity extends AppCompatActivity implements View.OnClickListener {


    private List<Token> mTokenList = new ArrayList<>();
    private ListView mLvToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_list);

        ImageView mBackArrow = findViewById(R.id.iv_back_arrow);
        TextView mToolBarTittle = findViewById(R.id.toolbar_title);
        mLvToken = findViewById(R.id.lvToken);
        FloatingActionButton button = findViewById(R.id.btn_fab);
        mBackArrow.setVisibility(View.VISIBLE);
        mToolBarTittle.setText(getString(R.string.token_list));
        mBackArrow.setOnClickListener(this);
        button.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SharedHelper.getTokenListKey(this, "tokenList") == null) {
            mTokenList.add(new Token(getString(R.string.ethereum), true));
            mTokenList.add(new Token("YES", false));
            SharedHelper.putTokenListKey(this, "tokenList", mTokenList);
            setAdapter(mTokenList, true);
        } else {
            setAdapter(SharedHelper.getTokenListKey(this, "tokenList"), false);
        }
    }

    public void setAdapter(List<Token> list, boolean isSelection) {
        TokenViewAdapter adapter = new TokenViewAdapter(this, R.layout.layout_token_list_items,
                list, mAddTokenListencer, isSelection);
        mLvToken.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.btn_fab:
                Intent fabIntent = new Intent(this, TokenActivity.class);
                startActivity(fabIntent);
                break;
        }
    }

    public interface AddTokenListencer {
        void tokenClicked(Token token);
    }

    List<Token> previousList = new ArrayList<>();
    private TokenListActivity.AddTokenListencer mAddTokenListencer = new AddTokenListencer() {
        @Override
        public void tokenClicked(Token mTokenResult) {
            if (mTokenResult != null) {
                previousList = SharedHelper.getTokenListKey(TokenListActivity.this, "tokenList");
                for (int i = 0; i < previousList.size(); i++) {
                    if (previousList.get(i).getTokenName().equals(mTokenResult.getTokenName())) {
                        previousList.get(i).setIsSelected(true);
                        SharedHelper.putKey(TokenListActivity.this, "token_name", previousList.get(i).getTokenName());
                    } else {
                        previousList.get(i).setIsSelected(false);
                    }
                }
                SharedHelper.putTokenListKey(TokenListActivity.this, "tokenList", previousList);
                setAdapter(previousList, false);
            }
        }
    };
}
