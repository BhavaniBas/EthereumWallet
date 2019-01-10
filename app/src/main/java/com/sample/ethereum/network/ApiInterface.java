package com.sample.ethereum.network;


import com.sample.ethereum.response.CurrentEthereumValue;
import com.sample.ethereum.response.EthereumBalance;
import com.sample.ethereum.response.TokenResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("api?")
    Call<EthereumBalance> getTransactionList(@Query("module") String module,
                                             @Query("action") String action,
                                             @Query("address") String address,
                                             @Query("sort") String sort,
                                             @Query("apiKey") String apiKey);

    @GET("https://test.tokenbalance.com/token/{contract}/{eth_address}")
    Call<TokenResponse> getTokenAddress(@Path("contract") String contractAddress,
                                        @Path("eth_address") String accountAddress);

    @GET("https://api.coinmarketcap.com/v1/ticker/ethereum/")
    Call<List<CurrentEthereumValue>> getCurrentEther();


}
