package com.sample.ethereum.network;


import com.sample.ethereum.response.EthereumBalance;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("api?")
    Call<EthereumBalance> getBalance(@Query("module") String module,
                                     @Query("action") String action,
                                     @Query("address") String address,
                                     @Query("tag") String tag,
                                     @Query("apiKey") String apiKey);

    @GET("api?")
    Call<EthereumBalance> getTransactionList(@Query("module") String module,
                                             @Query("action") String action,
                                             @Query("address") String address,
                                             @Query("sort") String sort,
                                             @Query("apiKey") String apiKey);


}
