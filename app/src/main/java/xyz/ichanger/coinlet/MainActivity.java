package xyz.ichanger.coinlet;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    int time = 100;
    int refreshTime = 1000 * 60;
    float amt, total, price, profit;
    String coinType = "Bitcoin";
    TextView coinLbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE); //Displays no title bar
        setContentView(R.layout.activity_main);

        fixPreferences();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerCoins);
        coinLbl = (TextView) findViewById(R.id.lblCoinAmt);

        createSpinner();
        spinner.setOnItemSelectedListener(this);

        updateLabel();

        refreshClick(findViewById(R.id.activity_main));

        //Run a handler to repeatedly fetch the info every minute
        /*
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshClick(findViewById(R.id.activity_main));
                handler.postDelayed(this, refreshTime);
            }
        }, refreshTime);*/
    }

    public void refreshClick (View view){

        //Start the REST client
        final Controller controller = new Controller();

        switch (coinType) {
            case "Bitcoin":
                controller.btcStart();
                amt = Preferences.getPreferences("BTC Amount", MainActivity.this);
                total = Preferences.getPreferences("BTC Price", MainActivity.this);
                break;
            case "Ethereum":
                controller.ethStart();
                amt = Preferences.getPreferences("ETH Amount", MainActivity.this);
                total = Preferences.getPreferences("ETH Price", MainActivity.this);
                break;
            case "Litecoin":
                controller.ltcStart();
                amt = Preferences.getPreferences("LTC Amount", MainActivity.this);
                total = Preferences.getPreferences("LTC Price", MainActivity.this);
                break;
        }

        //Run a handler to delay time until after the currency price is retrieved.
        final ProgressDialog yenLo = new ProgressDialog(MainActivity.this);
        yenLo.setTitle("Retrieving Coin Info");
        yenLo.setMessage("Loading...");
        //yenLo.setCancelable(false);
        yenLo.show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String info = controller.getAmount();
                if(time >= 3000){
                    Toast.makeText(MainActivity.this, "Connection Error: Unable to get request within 3 seconds!", Toast.LENGTH_LONG).show();
                    time -= 100;
                    yenLo.cancel();
                }
                else if(controller.getAmount().equals("fail")){
                    //If fail to request within time, increase time limit by 50 milliseconds and repeat handler
                    time += 100;
                    handler.postDelayed(this, time);
                }
                else {
                    price = Float.parseFloat(info);
                    Float revenue = price * amt;
                    profit = (float) Math.round((revenue - total) * 100) / 100; //Find the total price and round it to 2 decimal places
                    updateLabel();
                    yenLo.cancel();
                }
            }
        }, time);
    }

    public void addCoinClick(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.input_add_dialog, null);

        final EditText mNumCoins = (EditText) mView.findViewById(R.id.txtNumCoins);
        final EditText mPrice = (EditText) mView.findViewById(R.id.txtPrice);
        Button mAdd = (Button) mView.findViewById(R.id.btnAdd);

        mBuilder.setView(mView);

        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mPrice.getText().toString().isEmpty() && !mNumCoins.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,
                            "Success!",
                            Toast.LENGTH_SHORT).show();
                    switch (coinType) {
                        case "Bitcoin":
                            Preferences.setPreferences("BTC Price",  (float) (Math.floor((Float.parseFloat(mPrice.getText().toString()) + Preferences.getPreferences("BTC Price", MainActivity.this)) * 100) / 100), MainActivity.this);
                            Preferences.setPreferences("BTC Amount",  (float) (Math.floor((Float.parseFloat(mNumCoins.getText().toString()) + Preferences.getPreferences("BTC Amount", MainActivity.this)) * 100000) / 100000), MainActivity.this);
                            break;
                        case "Ethereum":
                            Preferences.setPreferences("ETH Price",  (float) (Math.floor((Float.parseFloat(mPrice.getText().toString()) + Preferences.getPreferences("ETH Price", MainActivity.this)) * 100) / 100), MainActivity.this);
                            Preferences.setPreferences("ETH Amount",  (float) (Math.floor((Float.parseFloat(mNumCoins.getText().toString()) + Preferences.getPreferences("ETH Amount", MainActivity.this)) * 100000) / 100000), MainActivity.this);
                            break;
                        case "Litecoin":
                            Preferences.setPreferences("LTC Price",  (float) (Math.floor((Float.parseFloat(mPrice.getText().toString()) + Preferences.getPreferences("LTC Price", MainActivity.this)) * 100) / 100), MainActivity.this);
                            Preferences.setPreferences("LTC Amount",  (float) (Math.floor((Float.parseFloat(mNumCoins.getText().toString()) + Preferences.getPreferences("LTC Amount", MainActivity.this)) * 100000) / 100000), MainActivity.this);
                            break;
                    }
                    dialog.dismiss();
                    updateLabel();
                    refreshClick(findViewById(R.id.activity_main));
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please type in the coin amount and/or price!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sellCoinClick(View view) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.input_sell_dialog, null);

        final EditText mNumCoins = (EditText) mView.findViewById(R.id.txtNumCoins);
        final EditText mPrice = (EditText) mView.findViewById(R.id.txtPrice);
        Button mSell = (Button) mView.findViewById(R.id.btnSell);

        mBuilder.setView(mView);

        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        mSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mPrice.getText().toString().isEmpty() && !mNumCoins.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,
                            "Success!",
                            Toast.LENGTH_SHORT).show();
                    switch (coinType) {
                        case "Bitcoin":
                            Preferences.setPreferences("BTC Price",  (float) (Math.floor((Preferences.getPreferences("BTC Price", MainActivity.this) - Float.parseFloat(mPrice.getText().toString())) * 100) / 100), MainActivity.this);
                            Preferences.setPreferences("BTC Amount",  (float) (Math.floor((Preferences.getPreferences("BTC Amount", MainActivity.this) - Float.parseFloat(mNumCoins.getText().toString())) * 100000) / 100000), MainActivity.this);
                            break;
                        case "Ethereum":
                            Preferences.setPreferences("ETH Price",  (float) (Math.floor((Preferences.getPreferences("ETH Price", MainActivity.this) - Float.parseFloat(mPrice.getText().toString())) * 100) / 100), MainActivity.this);
                            Preferences.setPreferences("ETH Amount",  (float) (Math.floor((Preferences.getPreferences("ETH Amount", MainActivity.this) - Float.parseFloat(mNumCoins.getText().toString())) * 100000) / 100000), MainActivity.this);
                            break;
                        case "Litecoin":
                            Preferences.setPreferences("LTC Price",  (float) (Math.floor((Preferences.getPreferences("LTC Price", MainActivity.this) - Float.parseFloat(mPrice.getText().toString())) * 100) / 100), MainActivity.this);
                            Preferences.setPreferences("LTC Amount",  (float) (Math.floor((Preferences.getPreferences("LTC Amount", MainActivity.this) - Float.parseFloat(mNumCoins.getText().toString())) * 100000) / 100000), MainActivity.this);
                            break;
                    }
                    dialog.dismiss();
                    updateLabel();
                    refreshClick(findViewById(R.id.activity_main));
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please type in the coin amount and/or price!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void updateLabel() {
        switch (coinType) {
            case "Bitcoin": coinLbl.setText(
                                "BTC Price per Coin: $" + price
                                + "\n\nBTC Amount: " + Preferences.getPreferences("BTC Amount", MainActivity.this)
                                + " BTC\n\nInvestment: $" + Preferences.getPreferences("BTC Price", MainActivity.this)
                                        + "\n\nRevenue: $" + (Preferences.getPreferences("BTC Price", MainActivity.this) + profit)
                                + "\n\nProfit: $" + profit
                                , TextView.BufferType.SPANNABLE);
                            break;
            case "Ethereum": coinLbl.setText(
                                "ETH Price per Coin: $" + price
                                + "\n\nETH Amount: " + Preferences.getPreferences("ETH Amount", MainActivity.this)
                                + " ETH\n\nInvestment: $" + Preferences.getPreferences("ETH Price", MainActivity.this)
                                        + "\n\nRevenue: $" + (Preferences.getPreferences("ETH Price", MainActivity.this) + profit)
                                + "\n\nProfit: $" + profit
                                , TextView.BufferType.SPANNABLE);
                            break;
            case "Litecoin": coinLbl.setText(
                                "LTC Price per Coin: $" + price
                                + "\n\nLTC Amount: " + Preferences.getPreferences("LTC Amount", MainActivity.this)
                                + " LTC\n\nInvestment: $" + Preferences.getPreferences("LTC Price", MainActivity.this)
                                        + "\n\nRevenue: $" + (Preferences.getPreferences("LTC Price", MainActivity.this) + profit)
                                + "\n\nProfit: $" + profit
                            , TextView.BufferType.SPANNABLE);
                            break;
        }
        Spannable s = (Spannable) coinLbl.getText();
        int index = coinLbl.getText().toString().lastIndexOf('$');
        int start = index;
        int end = coinLbl.getText().toString().length();
        if (Math.signum(profit) == 0) {
            s.setSpan(new ForegroundColorSpan(0xFF777777), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (profit > 0) {
            s.setSpan(new ForegroundColorSpan(0xFF008000), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (profit < 0 ) {
            s.setSpan(new ForegroundColorSpan(0xFFFF0000), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void createSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCoins);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.coin_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

    }

    //If first time load, an "f" gets stored into the preferences
    public void fixPreferences() {
        if(Preferences.getPreferences("BTC Amount", MainActivity.this).equals("f") || Preferences.getPreferences("BTC Price", MainActivity.this).equals("f")){
            Preferences.setPreferences("BTC Amount", (float) 0, MainActivity.this);
            Preferences.setPreferences("BTC Price", (float) 0, MainActivity.this);
        }
        if(Preferences.getPreferences("ETH Amount", MainActivity.this).equals("f") || Preferences.getPreferences("ETH Price", MainActivity.this).equals("f")){
            Preferences.setPreferences("ETH Amount", (float) 0, MainActivity.this);
            Preferences.setPreferences("ETH Price", (float) 0, MainActivity.this);
        }
        if(Preferences.getPreferences("LTC Amount", MainActivity.this).equals("f") || Preferences.getPreferences("LTC Price", MainActivity.this).equals("f")){
            Preferences.setPreferences("LTC Amount", (float) 0, MainActivity.this);
            Preferences.setPreferences("LTC Price", (float) 0, MainActivity.this);
        }
    }

    //Handle the Spinner dropdown selection event
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        coinType = parent.getItemAtPosition(pos).toString();
        updateLabel();
        refreshClick(findViewById(R.id.activity_main));
    }

    //Handle the Spinner nothing selected event
    public void onNothingSelected(AdapterView<?> parent) {
        Log.d("Nothing", "was selected");
        // Another interface callback
    }


}
