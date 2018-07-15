package com.pial.localbusroute;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog progress;
    private ApiInterface apiInterface;
    private Map<String, Marker> socketMarker = new HashMap<String, Marker>();
    private Runnable requestProviderRunable;
    private Handler handleRequestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while Updating...");
        progress.setCancelable(false);
        progress.show();
        apiInterface = RetrofitClient.getRetrofitClient().create(ApiInterface.class);
        handleRequestProvider = new Handler();
        requestProviderRunable = new Runnable() {
            @Override
            public void run() {
                getData();
                handleRequestProvider.postDelayed(this, 30*1000);
            }
        };

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getData() {
        Call<ArrayList<BusRouteResponse>> arrayListCall = apiInterface.ARRAY_LIST_CALL();
        arrayListCall.enqueue(new Callback<ArrayList<BusRouteResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<BusRouteResponse>> call, Response<ArrayList<BusRouteResponse>> response) {
                if (response.isSuccessful()) {
                    Log.e("response",new Gson().toJson(response.body()));
                    for (int i=0;i<response.body().size();i++) {
                        if (i==0) {
                            LatLng sydney = new LatLng(Double.parseDouble(response.body().get(i).getLat()),Double.parseDouble(response.body().get(i).getLong()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                        }
                        if (socketMarker.get(response.body().get(i).getBusStop()) == null) //create
                        {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .anchor(0.5f, 0.75f)
                                    .position(new LatLng(Double.parseDouble(response.body().get(i).getLat()),Double.parseDouble(response.body().get(i).getLong())));
                            Marker mar1 = mMap.addMarker(markerOptions);
                            mar1.setVisible(true);
                            socketMarker.put(response.body().get(i).getBusStop(), mar1);
                            mar1.setTag("updated");
                        } else { //update

                            Marker mar = socketMarker.get(response.body().get(i).getBusStop());
                            mar.setVisible(true);
                            mar.setRotation((float) Utilities.getBearing(mar.getPosition().latitude, mar.getPosition().longitude, Double.parseDouble(response.body().get(i).getLat()),Double.parseDouble(response.body().get(i).getLong())));
                            Utilities.animateMarkerTo(mar, Double.parseDouble(response.body().get(i).getLat()), Double.parseDouble(response.body().get(i).getLong()));
                            mar.setTag("updated");
                        }
                    }

                    try {
                        for (String value : socketMarker.keySet()) {
                            Marker marker = socketMarker.get(value);
                            if (marker.getTag().toString().equals("not") || marker.getTag().toString().contains("not") || marker.getTag().toString().equals("not")) {
                                marker.remove();
                                socketMarker.remove(value);
                            }
                            marker.setTag("not");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BusRouteResponse>> call, Throwable t) {

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (progress.isShowing()) {
            progress.dismiss();
        }
        mMap = googleMap;
        handleRequestProvider.removeCallbacks(requestProviderRunable);
        handleRequestProvider.post(requestProviderRunable);
        // Add a marker in Sydney and move the camera
        

    }




}
