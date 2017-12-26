package com.globalapp.futuretaxi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.Locale;

public class ComingActivity extends Activity {
    public static String Name = "", Phone = "", image = "", car = "", state = "", CarColor = "", CarModel = "";
    public static TextView txtState, txtCounter;
    public static boolean isFinished = false;
    AQuery aQuery;
    SharedPreferences sharedPreferences;
    CountDownTimer downTimer;
    private int secs, mins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("TaxiShared", Context.MODE_PRIVATE);

        String languageToLoad = sharedPreferences.getString("language", "en");
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.activity_coming);
        aQuery = new AQuery(this);
        TextView txtDriverName = (TextView) findViewById(R.id.Coming_DriverName);
        TextView txtDriverPhone = (TextView) findViewById(R.id.Coming_driverPhone);
        TextView txtCarColor = (TextView) findViewById(R.id.txtCarColor);
        TextView txtCarModel = (TextView) findViewById(R.id.txtCarModel);
        txtState = (TextView) findViewById(R.id.txtState);
        txtState.setText(state);
        TextView txtCar = (TextView) findViewById(R.id.txtCarNo);
        txtCounter = (TextView) findViewById(R.id.txtCounter);
        aQuery.id(R.id.Coming_DriverImage).image(image, true, true);
        txtDriverName.setText(Name);
        txtDriverPhone.setText(Phone);
        txtCarColor.setText(CarColor);
        txtCarModel.setText(CarModel);
        txtCar.setText(car);
        loadState();


    }


    public void call(View view) {
        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Phone));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(phoneCallIntent);
    }

    @Override
    public void onBackPressed() {

        if (isFinished) {
            super.onBackPressed();
            isFinished = false;
        }

        super.onBackPressed();
    }

    private void loadState() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        String text = intent.getStringExtra("text");
                        txtState.setText(text);

                        if (text.equals(getString(R.string.arrived))) {
                            counter();
                        } else if (text.equals(getString(R.string.on_trip))) {
                            downTimer.cancel();
                            txtCounter.setText("");
                            finish();
                        }


                    }
                }, new IntentFilter(GCMService.ACTION)
        );
    }


    private void counter() {

        downTimer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secs = (int) (millisUntilFinished / 1000);
                mins = secs / 60;
                mins = mins % 60;
                secs = secs % 60;

                String time = String.format("%02d", mins) + ":" + String.format("%02d", secs);
                txtCounter.setText(time);
            }

            @Override
            public void onFinish() {
                txtCounter.setText("");
            }
        };
        downTimer.start();
    }

}
