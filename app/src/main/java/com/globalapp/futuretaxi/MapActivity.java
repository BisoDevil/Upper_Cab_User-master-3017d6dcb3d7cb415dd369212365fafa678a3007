package com.globalapp.futuretaxi;

import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;

import android.location.Address;
import android.location.Criteria;

import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.json.GenericJson;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyDeleteCallback;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.cache.CachePolicy;
import com.kinvey.java.cache.InMemoryLRUCache;
import com.kinvey.java.core.KinveyClientCallback;
import com.kinvey.java.model.KinveyDeleteResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, LocationListener {


    public static TextView txtSearch;
    public static boolean service_Running = false, OnTrip = false;
    public static Button order;
    final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    SharedPreferences sharedPreferences;
    TextView txtToolBar, txtUserNameHeader, txtMailHeader, txtEstimatedTime;
    Client mKinveyClient;
    CountDownTimer timer;

    ImageView imgDots;
    LatLng distLocation, Center;
    Polyline line;
    AQuery aQuery;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker[] mMarker = {null, null, null, null, null, null, null, null, null, null};
    String[] arr;
    //    Marker mark;
    Marker markerStart, markerEnd;
    private GoogleMap mMap;
    private Location GPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // test lan
        sharedPreferences = getSharedPreferences("TaxiShared", Context.MODE_PRIVATE);

        String languageToLoad = sharedPreferences.getString("language", "en");
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());


        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        // Init Views
        initViews();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case (R.id.nav_favorite):
                Intent profile = new Intent(getApplicationContext(), FavoriteActivity.class);
                startActivity(profile);

                break;
            case (R.id.nav_booking):
                Intent chat = new Intent(getApplicationContext(), BookingActivity.class);
                startActivity(chat);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                break;
            case (R.id.nav_feedback):
                Intent feedback = new Intent(getApplicationContext(), FeedbackActivity.class);
                FeedbackActivity.backbutton = true;
                startActivity(feedback);

                break;
            case (R.id.nav_invite):
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                String sAux = "\nLet me recommend you this application\n\n";
                sAux = sAux + "https://play.google.com/store/apps/details?id=com.globalapp.futuretaxi \n\n";
                i.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(i, getString(R.string.share_chooser)));
                break;
            case (R.id.nav_setting):
                Intent setting = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(setting);

                break;
            case (R.id.nav_myTrips):
                Intent trips = new Intent(getApplicationContext(), MyTripsActivity.class);
                startActivity(trips);


                break;
            case (R.id.nav_call_us):
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:01201513513"));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Change call number

                    return true;
                }
                startActivity(phoneIntent);
                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {


                } else {


                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                                    , Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                }
            }
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        mMap.setBuildingsEnabled(true);
        LocationManager Locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GPS = Locationmanager.getLastKnownLocation(getProviderName());
        if (GPS == null) {
            GPS = Locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        try {
            Center = new LatLng(GPS.getLatitude(), GPS.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Center, 17));


            Tracking();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return true;
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //       getArea get = new getArea(MapActivity.this);
//                if (get.getStatus() != AsyncTask.Status.RUNNING) {
//                    new getArea(MapActivity.this.getApplicationContext()).execute(mMap.getCameraPosition().target);
//
//                }
            }
        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                try {

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pick_animate_in);
                    txtEstimatedTime.setText("");
                    imgDots.setVisibility(View.VISIBLE);
                    imgDots.startAnimation(animation);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()

        {
            @Override
            public boolean onMyLocationButtonClick() {
                try {
                    if (line != null) {
                        line.remove();
                        markerStart.remove();
                        markerEnd.remove();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()

        {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLUE);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


    }


    private void initViews() {

        txtToolBar = (TextView) findViewById(R.id.txtToolBar);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/rosie.ttf");
        txtToolBar.setTypeface(custom_font);
        mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
        txtSearch = (TextView) findViewById(R.id.txtSearch);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(1) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(1).getParent()).findViewById(2);
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 250);
        }
        aQuery = new AQuery(this);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        txtUserNameHeader = (TextView) hView.findViewById(R.id.txtUserNameHeader);
        txtMailHeader = (TextView) hView.findViewById(R.id.txtMailHeader);


        txtEstimatedTime = (TextView) findViewById(R.id.txtEstimatedTime);

        imgDots = (ImageView) findViewById(R.id.imgDots);
        order = (Button) findViewById(R.id.fabOrder);


    }


//    test floating menu

    public void toggleCounter(View view) {
        Intent intro = new Intent(getApplicationContext(), CounterActivity.class);
        startActivity(intro);

    }

    public void createOrder(final View view) {
        if (mMarker[0] == null) {

            return;
        }
        timer.cancel();
        view.setEnabled(false);
        Client kin = new Client.Builder(this.getApplicationContext()).build();
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date());
        GenericJson appData = new GenericJson();
        appData.put("_id", "order" + kin.user().getId());
        appData.put("user_phone", sharedPreferences.getString("PhoneNumber", ""));
        appData.put("user_dist", txtSearch.getText().toString());
        appData.put("user_lat", mMap.getCameraPosition().target.latitude);
        appData.put("user_long", mMap.getCameraPosition().target.longitude);
        appData.put("state", "Requesting");
        appData.put("date", date);
        AsyncAppData<GenericJson> Travels = kin.appData("Trips", GenericJson.class);

        Travels.save(appData, new KinveyClientCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson genericJson) {

                txtSearch.setText(getString(R.string.search_hint));
                timer.start();
                view.setEnabled(false);
                Snackbar.make(view, getString(R.string.request_submitted), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.cancel_order), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelOrder();
                            }
                        })
                        .setDuration(30000)
                        .setActionTextColor(getResources().getColor(R.color.bg_screen2))
                        .show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(true);
                    }
                }, 15000);
            }

            @Override
            public void onFailure(Throwable throwable) {
                view.clearAnimation();
                view.setEnabled(true);

            }
        });

    }

    private void cancelOrder() {
        String id = "order" + mKinveyClient.user().getId();
        mKinveyClient.appData("Trips", GenericJson.class).delete(id, new KinveyDeleteCallback() {
            @Override
            public void onSuccess(KinveyDeleteResponse kinveyDeleteResponse) {
                Toast.makeText(MapActivity.this, getString(R.string.order_canceled), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void userLogout(View view) {
        mKinveyClient.user().logout().execute();
        Intent user = new Intent(getApplicationContext(), UserActivity.class);
        startActivity(user);
    }

    public void editProfile(View view) {
        try {
            Intent editProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(editProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void searchPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                txtSearch.setText(place.getAddress());
                distLocation = place.getLatLng();
                String url = makeURL(GPS.getLatitude(), GPS.getLongitude(), place.getLatLng().latitude, place.getLatLng().longitude);
                String dis = makeUrlDistance(GPS.getLatitude(), GPS.getLongitude(), place.getLatLng().latitude, place.getLatLng().longitude);
                aQuery.ajax(url, JSONObject.class, this, "jsonCallback");
                aQuery.ajax(dis, JSONObject.class, this, "jsonCallbackDis");

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void addToFavorite(View view) {
        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this);
        inputAlert.setTitle(getString(R.string.add_comment));

        LayoutInflater inflater = getLayoutInflater();
        View editView = inflater.inflate(R.layout.pop_up, null);
        inputAlert.setView(editView);


        final EditText userInput = (EditText) editView.findViewById(R.id.txtPopComment);
        inputAlert.setPositiveButton(getString(R.string.submit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInputValue = userInput.getText().toString();
                FavoriteDB db = new FavoriteDB(getApplicationContext());
                db.InsertRow(userInputValue, txtSearch.getText().toString());
                Toast.makeText(MapActivity.this, getString(R.string.added), Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = inputAlert.create();
        alertDialog.show();


    }

    private void clearMarker() {

        try {
            if (mMarker[0] != null) {
                for (Marker aMMarker : mMarker) {
                    aMMarker.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Tracking() {


        final Query q2 = new Query();
        timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {


                try {
                    if (OnTrip) {
                        q2.equals("phone", ComingActivity.Phone);
                    } else {

                        q2.nearSphere("_geoloc", mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude, 1.0);
                        q2.equals("state", "online");
                    }
                } catch (Exception e) {
                    Log.w("Error", e.getMessage());
                }
                AsyncAppData<GenericJson> appData = mKinveyClient.appData("locations", GenericJson.class);
                appData.setCache(new InMemoryLRUCache(), CachePolicy.CACHEFIRST);
                appData.get(q2, new KinveyListCallback<GenericJson>() {
                    @Override
                    public void onSuccess(GenericJson[] genericJsons) {
                        if (genericJsons.length == 0) {
                            imgDots.clearAnimation();
                            imgDots.setVisibility(View.GONE);
                            txtEstimatedTime.setText(getString(R.string.country_support));
                            clearMarker();
                            Arrays.fill(mMarker, null);
                        } else {
                            try {
                                Location toLOC = new Location("toLOC");
                                Location from = new Location("From");
                                from.setLatitude(mMap.getCameraPosition().target.latitude);
                                from.setLongitude(mMap.getCameraPosition().target.longitude);
                                toLOC.setLatitude(Double.valueOf(genericJsons[0].get("lat").toString()));
                                toLOC.setLongitude(Double.valueOf(genericJsons[0].get("long").toString()));

                                int time = (int) (from.distanceTo(toLOC) / 100);
                                imgDots.clearAnimation();
                                imgDots.setVisibility(View.GONE);
                                txtEstimatedTime.setText(String.valueOf(time) + " " + getString(R.string.min) + " " + getString(R.string.time_driver));


                                for (int n = 0; n < genericJsons.length; n++) {

                                    LatLng latlong = new LatLng(Double.valueOf(genericJsons[n].get("lat").toString()),
                                            Double.valueOf(genericJsons[n].get("long").toString()));


                                    if (mMarker[n] == null) {

                                        mMarker[n] = mMap.addMarker(new MarkerOptions().position(latlong)
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_pin))

                                        );

                                    } else {

                                        animateMarker(mMarker[n], latlong, false);


                                    }
                                }
                            } catch (Exception ex) {
                                imgDots.clearAnimation();
                                imgDots.setVisibility(View.GONE);
                                txtEstimatedTime.setText(getString(R.string.country_support));
//                                txtEstimatedTime.setText(ex.getMessage());
                                clearMarker();
                                Arrays.fill(mMarker, null);

                                Log.e("Drivers", ex.getMessage());

                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        imgDots.clearAnimation();
                        imgDots.setVisibility(View.GONE);
                        txtEstimatedTime.setText(getString(R.string.country_support));
//                        txtEstimatedTime.setText(throwable.getMessage());
                        Log.d("Cars", throwable.getLocalizedMessage());
                        clearMarker();
                        Arrays.fill(mMarker, null);

                    }

                });
                try {
                    markerEnd.showInfoWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish() {
                timer.start();

            }
        };
        timer.start();

    }

    // animating cars
    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    String getProviderName() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(false); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FeesCalculation.IS_SERVICE_RUNNING) {
            order.setEnabled(false);
        } else {
            order.setEnabled(true);
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        try {
            txtUserNameHeader.setText(sharedPreferences.getString("UserName", "User Name"));
            txtMailHeader.setText(sharedPreferences.getString("E_Mail", "Mail"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String imageURL = sharedPreferences.getString("imageURL", "");
        if (!imageURL.equals("")) {
            aQuery.id(hView.findViewById(R.id.profile_image)).image(imageURL, true, true);

        }
        mKinveyClient.push().initialize(getApplication());
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling


            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    public String makeURL(double sourcelat, double sourcelog, double destlat,
                          double destlog) {
        String urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=" +// from
                Double.toString(sourcelat) +
                "," +
                Double.toString(sourcelog) +
                "&destination=" +// to
                Double.toString(destlat) +
                "," +
                Double.toString(destlog) +
                "&mode=driving&alternatives=true&key=" +
                getString(R.string.google_maps_key);

        return urlString;
    }

    private String makeUrlDistance(double fromLat, double fromLong, double toLat, double toLong) {
        String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" +
                Double.toString(fromLat) +
                "," +
                Double.toString(fromLong) +
                "&destinations=" +
                Double.toString(toLat) +
                "," +
                Double.toString(toLong) +
                "&key=" +
                getString(R.string.google_maps_key);

        return URL;
    }

    public void jsonCallback(String url, JSONObject json, AjaxStatus status) {
        drawPath(json.toString());
        Log.e("path:", json.toString());

    }

    public void jsonCallbackDis(String url, JSONObject json, AjaxStatus status) {
        getEstimatedDistance(json.toString());
    }

    public String[] getEstimatedDistance(String result) {
        try {
            final JSONObject json = new JSONObject(result);
            String duration = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
            String distance = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("text");

            duration = duration.replace("mins", getString(R.string.min));
            duration = duration.replace("hours", getString(R.string.hours));

            distance = distance.replace("km", getString(R.string.km));
            String[] lt = distance.split(" ");
            Double dis = Double.parseDouble(lt[0]);

            Double mone = ((dis * 1.97) + 4);
            String money = String.format(Locale.US, "%.2f", mone) + " " + getString(R.string.le);


            arr = new String[]{duration, distance, money};
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public void drawPath(String result) {
        if (line != null) {
            line.remove();
        }

        try {
            markerEnd = mMap.addMarker(new MarkerOptions().position(distLocation).icon(BitmapDescriptorFactory.fromResource(R.drawable.mapicon_destination)).title(arr[2]).snippet(arr[1] + "\n" + arr[0]));

            markerStart = mMap.addMarker(new MarkerOptions().position(Center).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_start)));
        } catch (Exception ex) {
            Log.d("Drawing_Path_Error:", ex.getMessage());
        }
        try {
            // Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            PolylineOptions options = new PolylineOptions().width(12).color(getResources().getColor(R.color.colorFont));
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            line = mMap.addPolyline(options);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng latLng : list) {
                builder.include(latLng);
            }
            final LatLngBounds bounds = builder.build();
            //BOUND_PADDING is an int to specify padding of bound.. try 100.
            int BOUND_PADDING = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUND_PADDING);
            mMap.animateCamera(cu);


        } catch (Exception e) {
            Log.e("response", e.getMessage());
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (service_Running) {
            Center = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Center, 17));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void showBooking(View view) {
        Intent chat = new Intent(getApplicationContext(), BookingActivity.class);
        startActivity(chat);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

    }


    private class getArea extends AsyncTask<LatLng, Void, Void> {


        List<Address> list;
        Context mcontext;

        getArea(Context context) {
            super();
            mcontext = context;
        }

        @Override
        protected Void doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mcontext);
            try {
                list = geocoder.getFromLocation(params[0].latitude, params[0].longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!list.isEmpty()) {
                try {
                    String locality = list.get(0).toString();

                    if (locality != null) {
                        if (!locality.toLowerCase().contains("aswan")) {

                            Toast.makeText(mcontext, getString(R.string.country_support), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mcontext, getString(R.string.country_support), Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    Toast.makeText(mcontext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}