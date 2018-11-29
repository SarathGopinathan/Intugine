package com.example.user.intugine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.intugine.Model.ModelBloreTemp;
import com.example.user.intugine.Model.ModelBloreTempDetails;
import com.example.user.intugine.Model.ModelDelTemp;
import com.example.user.intugine.Model.ModelMumTemp;
import com.example.user.intugine.Utilities.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private TextView textLocation;
    private Button temperature;
    private LocationManager locationManager;
    private String tempblore,mintempblore,maxtempblore,humidityblore,tempdel,mintempdel,maxtempdel,
    humdel,tempmum,mintempmum,maxtempmum,hummum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textLocation = (TextView) findViewById(R.id.tv_location);
        temperature = (Button) findViewById(R.id.btn_temperature);
        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getBloreTempAPI();
        getDelhiTempAPI();
        getMumTempAPI();

        temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.temp);

                TextView tempBlore = (TextView)dialog.findViewById(R.id.txt_tempblore);
                TextView mintempBlore = (TextView)dialog.findViewById(R.id.txt_mintempblore);
                TextView maxtempBlore = (TextView)dialog.findViewById(R.id.txt_maxtempblore);
                TextView humBlore = (TextView)dialog.findViewById(R.id.txt_humblore);

                TextView tempDel = (TextView)dialog.findViewById(R.id.txt_tempdel);
                TextView mintempDel = (TextView)dialog.findViewById(R.id.txt_mintempdel);
                TextView maxtempDel = (TextView)dialog.findViewById(R.id.txt_maxtempdel);
                TextView humDel = (TextView)dialog.findViewById(R.id.txt_humdel);

                TextView tempMum = (TextView)dialog.findViewById(R.id.txt_tempmum);
                TextView mintempMum = (TextView)dialog.findViewById(R.id.txt_mintempmum);
                TextView maxtempMum = (TextView)dialog.findViewById(R.id.txt_maxtempmum);
                TextView humMum = (TextView)dialog.findViewById(R.id.txt_hummum);

                tempBlore.setText(tempblore);
                mintempBlore.setText(mintempblore);
                maxtempBlore.setText(maxtempblore);
                humBlore.setText(humidityblore);

                tempDel.setText(tempdel);
                mintempDel.setText(mintempdel);
                maxtempDel.setText(maxtempdel);
                humDel.setText(humdel);

                tempMum.setText(tempmum);
                mintempMum.setText(mintempmum);
                maxtempMum.setText(maxtempmum);
                humMum.setText(hummum);

                dialog.show();


            }
        });

    }

    private void getBloreTempAPI() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://openweathermap.org/data/2.5/weather?lat=12.97&lon=77.59&appid=b6907d289e10d714a6e88b30761fae22",
                null, new MainActivity.bloreTempRespHandler());

    }

    private void getDelhiTempAPI() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://openweathermap.org/data/2.5/weather?lat=28.666668&lon=77.216667&appid=b6907d289e10d714a6e88b30761fae22",
                null, new MainActivity.delhiTempRespHandler());

    }

    private void getMumTempAPI() {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://openweathermap.org/data/2.5/weather?lat=19.01441&lon=72.847939&appid=b6907d289e10d714a6e88b30761fae22",
                null, new MainActivity.mumTempRespHandler());

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GPSTracker tracker = new GPSTracker(MainActivity.this);
        LatLng source = new LatLng(tracker.getLatitude(), tracker.getLongitude());

        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(tracker.getLatitude(),tracker.getLongitude(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        textLocation.setText(addresses.get(0).getLocality()+","+addresses.get(0).getCountryName());

        mMap.addMarker(new MarkerOptions().position(source).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(source));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source, 15f));


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        String timestamp = simpleDateFormat.format(new Date());

        pushLocationAPI(source,timestamp);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Your location"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));

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
                }
        );
    }

    private void pushLocationAPI(LatLng latLng, String timestamp) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("name","test");
        params.add("loc", String.valueOf(latLng));
        params.add("time",timestamp);
        client.setTimeout(20 * 10000);
        client.post("https://96gw5cphgi.execute-api.ap-south-1.amazonaws.com/latest/",params,new MainActivity.ResponceHandlerClass());

    }

    public class ResponceHandlerClass extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            //Response is not mentioned in the API documentation! I have no clue if success = 1 or not!
            //I havent created a model class cause I dont know the response of the API.
            Toast.makeText(MainActivity.this, "LatLng pushed to the server", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(MainActivity.this,"Seems like your network connectivity is down or very slow!",Toast.LENGTH_SHORT).show();
        }
    }

    public class bloreTempRespHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


            ModelBloreTemp model = new Gson().fromJson(new String (responseBody),ModelBloreTemp.class);
            tempblore = model.main.temp;
            mintempblore = model.main.tempMin;
            maxtempblore = model.main.tempMax;
            humidityblore = model.main.humidity;

        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(MainActivity.this,"Seems like your network connectivity is down or very slow!",Toast.LENGTH_SHORT).show();
        }
    }

    public class delhiTempRespHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            ModelDelTemp model = new Gson().fromJson(new String (responseBody),ModelDelTemp.class);
            tempdel = model.main.temp;
            mintempdel = model.main.tempMin;
            maxtempdel = model.main.tempMax;
            humdel = model.main.humidity;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(MainActivity.this,"Seems like your network connectivity is down or very slow!",Toast.LENGTH_SHORT).show();
        }
    }

    public class mumTempRespHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            ModelMumTemp model = new Gson().fromJson(new String (responseBody),ModelMumTemp.class);
            tempmum = model.main.temp;
            mintempmum = model.main.tempMin;
            maxtempmum = model.main.tempMax;
            hummum = model.main.humidity;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(MainActivity.this,"Seems like your network connectivity is down or very slow!",Toast.LENGTH_SHORT).show();
        }
    }
}
