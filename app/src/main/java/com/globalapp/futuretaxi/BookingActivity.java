package com.globalapp.futuretaxi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.DatePicker;

import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import com.google.api.client.json.GenericJson;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

import java.util.Calendar;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    TextView txtStartDate, txtEndDate, txtTime, txtEndTime;
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
        setContentView(R.layout.activity_booking);
        final Switch SW = (Switch) findViewById(R.id.returnSwitch);
        SW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (SW.isChecked()) {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.layoutReturn);
                    layout.setVisibility(View.VISIBLE);
                } else {
                    LinearLayout layout = (LinearLayout) findViewById(R.id.layoutReturn);
                    layout.setVisibility(View.GONE);
                }
            }
        });
        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtEndTime = (TextView) findViewById(R.id.txtEbdTime);

        this.setFinishOnTouchOutside(true);
    }

    public void getStartDate(final View view) {
        DatePicker date = new DatePicker(this);
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.add(Calendar.DAY_OF_MONTH, 1);
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int mon = month + 1;

                txtStartDate.setText(String.format("%s/%s/%s", dayOfMonth, mon, year));

            }
        }, date.getYear(), date.getMonth(), date.getDayOfMonth());

        dialog.getDatePicker().setMinDate(c.getTimeInMillis());

        dialog.show();
    }

    public void getTime(View view) {

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                TextView txtTime = (TextView) findViewById(R.id.txtTime);
                txtTime.setText(String.format("%s:%s", hourOfDay, minute));
            }
        }, Calendar.getInstance(Locale.getDefault()).getTime().getHours(), Calendar.getInstance(Locale.getDefault()).getTime().getMinutes(), true);

        dialog.show();

    }

    public void getEndDate(final View view) {
        DatePicker date = new DatePicker(this);
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.add(Calendar.DAY_OF_MONTH, 2);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int mon = month + 1;

                txtEndDate.setText(String.format("%s/%s/%s", dayOfMonth, mon, year));

            }
        }, date.getYear(), date.getMonth(), date.getDayOfMonth());
        dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        dialog.show();
    }

    public void getEndTime(View view) {

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                txtEndTime.setText(String.format("%s:%s", hourOfDay, minute));
            }
        }, Calendar.getInstance(Locale.getDefault()).getTime().getHours(), Calendar.getInstance(Locale.getDefault()).getTime().getMinutes(), true);

        dialog.show();

    }

    public void createOrder(final View view) {


        Client kin = new Client.Builder(this.getApplicationContext()).build();

        GenericJson appData = new GenericJson();
        appData.put("_id", "order" + kin.user().getId());
        appData.put("user_phone", sharedPreferences.getString("PhoneNumber", ""));
//        appData.put("user_dist", txtSearch.getText().toString());
//        appData.put("user_lat", mMap.getCameraPosition().target.latitude);
//        appData.put("user_long", mMap.getCameraPosition().target.longitude);
        appData.put("Start Date", txtStartDate.getText().toString());
        appData.put("End Date", txtEndDate.getText().toString());
        appData.put("Time", txtTime.getText().toString());
        appData.put("End Time", txtEndTime.getText().toString());

        appData.put("state", "Requesting");
        AsyncAppData<GenericJson> Travels = kin.appData("ScheduledTrips", GenericJson.class);

        Travels.save(appData, new KinveyClientCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson genericJson) {


                view.setEnabled(false);
                Snackbar.make(view, getString(R.string.request_submitted), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.cancel_order), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setDuration(3000)
                        .setActionTextColor(getResources().getColor(R.color.bg_screen2))
                        .show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                        finish();
                    }
                }, 5000);


            }

            @Override
            public void onFailure(Throwable throwable) {

//                Toast.makeText(BookingActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public void close(View view) {
        this.finish();
    }
}
