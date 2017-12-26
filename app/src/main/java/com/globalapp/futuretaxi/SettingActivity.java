package com.globalapp.futuretaxi;


import android.app.AlertDialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.net.Uri;
import android.os.Handler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Switch aSwitch;
    TextView txtLan;
    String[] lan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("TaxiShared", Context.MODE_PRIVATE);

        String languageToLoad = sharedPreferences.getString("language", "en"); // your language
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_setting);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lan = new String[]{getString(R.string.english), getString(R.string.arabic)};

        aSwitch = (Switch) findViewById(R.id.swNotification);
        txtLan = (TextView) findViewById(R.id.txtLanguage);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Notification", b);
                editor.apply();
            }
        });

        ListView list = (ListView) findViewById(R.id.ListSetting);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setLanguage();
                        break;
                    case 1:
                        take_tour();
                        break;
                    case 2:
                        about();
                        break;
                    case 3:
                        Uri uri = Uri.parse("market://details?id=com.globalapp.futuretaxi");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        // To count with Play market backstack, After pressing back button,
                        // to taken back to our application, we need to add following flags to intent.
                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=com.globalapp.futuretaxi")));
                        }
                        break;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        aSwitch.setChecked(sharedPreferences.getBoolean("Notification", true));
        txtLan.setText(lan[sharedPreferences.getInt("lanCaption", 0)]);

    }

    public void setLanguage() {

        final String[] loca = {"en", "ar"};
        AlertDialog.Builder Dsetting = new AlertDialog.Builder(this);
        Dsetting.setTitle(getString(R.string.choose_lan))
                .setItems(lan, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txtLan.setText(lan[i]);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("language", loca[i]);
                        editor.putInt("lanCaption", i);
                        editor.apply();
                        Toast.makeText(SettingActivity.this, getString(R.string.restart), Toast.LENGTH_SHORT).show();


                        restartApp();
                    }
                })
                .show();
    }

    private void restartApp() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                System.exit(0);
            }
        }, 3000);


    }

    public void take_tour() {
        Intent tour = new Intent(getApplicationContext(), IntroActivity.class);
        startActivity(tour);
    }

    public void about() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(getString(R.string.about_message))
                .setTitle(getString(R.string.about))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }
}
