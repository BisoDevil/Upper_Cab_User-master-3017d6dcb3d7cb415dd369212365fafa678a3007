package com.globalapp.futuretaxi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class CounterActivity extends AppCompatActivity {
    public static TextView txtTotalDistance, txtMovingTime, txtStoppageTime, txtSpeed, txtTotalMoney, txtTotalTime;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("TaxiShared", Context.MODE_PRIVATE);

        String languageToLoad = sharedPreferences.getString("language", "en");
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        txtSpeed = (TextView) findViewById(R.id.txt_Speed);
        txtTotalDistance = (TextView) findViewById(R.id.txt_Distance);
        txtTotalMoney = (TextView) findViewById(R.id.txt_Total_Money_Fees);
        txtTotalTime = (TextView) findViewById(R.id.txt_Total_Time);
        txtStoppageTime = (TextView) findViewById(R.id.txt_Stopping_Time);
        txtMovingTime = (TextView) findViewById(R.id.txt_Moving_Time);
    }


}
