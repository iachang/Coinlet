package xyz.ichanger.coinlet;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


/**
 * Created by johannespitts on 7/7/17.
 */

public class Controller {

    static final String BASE_URL = "https://api.coinbase.com/v2/";
    Coin coin;
    Boolean good = false;

    public void btcStart() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CoinbaseAPI coinbaseAPI = retrofit.create(CoinbaseAPI.class);

        Call<Coin> call = coinbaseAPI.getBTC("USD");
        call.enqueue(new Callback<Coin>() { //Errors here "Expected data_Type instead of ..." because the data type of Call<Data Type> is INCORRECT. Make sure you use the correct data type, a list doesn't always suffice and sometimes an Object will do
            @Override
            public void onResponse(Response<Coin> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    coin = response.body();
                    //Log.d("Change Amount: ", coin.amount);
                    good = true;
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Error? ", t.toString());
            }
        });
    }

    public void ethStart() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CoinbaseAPI coinbaseAPI = retrofit.create(CoinbaseAPI.class);

        Call<Coin> call = coinbaseAPI.getETH("USD");
        call.enqueue(new Callback<Coin>() { //Errors here "Expected data_Type instead of ..." because the data type of Call<Data Type> is INCORRECT. Make sure you use the correct data type, a list doesn't always suffice and sometimes an Object will do
            @Override
            public void onResponse(Response<Coin> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    coin = response.body();
                    //Log.d("Change Amount: ", coin.amount);
                    good = true;
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Error? ", t.toString());
            }
        });
    }

    public void ltcStart() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        CoinbaseAPI coinbaseAPI = retrofit.create(CoinbaseAPI.class);

        Call<Coin> call = coinbaseAPI.getLTC("USD");
        call.enqueue(new Callback<Coin>() { //Errors here "Expected data_Type instead of ..." because the data type of Call<Data Type> is INCORRECT. Make sure you use the correct data type, a list doesn't always suffice and sometimes an Object will do
            @Override
            public void onResponse(Response<Coin> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    coin = response.body();
                    //Log.d("Change Amount: ", coin.amount);
                    good = true;
                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("Error? ", t.toString());
            }
        });
    }

    public String getAmount(){
        if(good) {
            return coin.amount;
        }
        else {
            return "fail";
        }
    }
}
