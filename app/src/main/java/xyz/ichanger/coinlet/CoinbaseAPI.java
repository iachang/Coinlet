package xyz.ichanger.coinlet;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by johannespitts on 7/7/17.
 */

public interface CoinbaseAPI {
    @GET("prices/BTC-USD/spot/")
    Call<Coin> getBTC(@Query("currency") String status);

    @GET("prices/ETH-USD/spot/")
    Call<Coin> getETH(@Query("currency") String status);

    @GET("prices/LTC-USD/spot/")
    Call<Coin> getLTC(@Query("currency") String status);
}
