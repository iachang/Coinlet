package xyz.ichanger.coinlet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by johannespitts on 7/23/17.
 */

public class Coin {
    @SerializedName("amount")
    String amount;

    @SerializedName("currency")
    String currency;

    public String getTotal() {
        return this.amount + this.currency;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}