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
    private int deviceId=3;
    private int positionID=-1;


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


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void getData(int positionID) {
        Call<ArrayList<PositionResponse>> arrayListCall = apiInterface.getLatlngList(URLHelper.token,positionID);
        arrayListCall.enqueue(new Callback<ArrayList<PositionResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<PositionResponse>> call, Response<ArrayList<PositionResponse>> response) {
                if (response.isSuccessful()) {
                    Log.e("response",new Gson().toJson(response.body()));
                    for (int i=0;i<response.body().size();i++) {
                        if (i==0) {
                            LatLng sydney = new LatLng(response.body().get(i).getLatitude(),response.body().get(i).getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                        }
                        if (socketMarker.get(String.valueOf(response.body().get(i).getId())) == null) //create
                        {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .anchor(0.5f, 0.75f)
                                    .position(new LatLng(response.body().get(i).getLatitude(),response.body().get(i).getLongitude()));
                            Marker mar1 = mMap.addMarker(markerOptions);
                            mar1.setVisible(true);
                            socketMarker.put(String.valueOf(response.body().get(i).getId()), mar1);
                            mar1.setTag("updated");
                        } else { //update

                            Marker mar = socketMarker.get(String.valueOf(response.body().get(i).getId()));
                            mar.setVisible(true);
                            mar.setRotation((float) Utilities.getBearing(mar.getPosition().latitude, mar.getPosition().longitude, response.body().get(i).getLatitude(),response.body().get(i).getLongitude()));
                            Utilities.animateMarkerTo(mar, response.body().get(i).getLatitude(), response.body().get(i).getLongitude());
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
            public void onFailure(Call<ArrayList<PositionResponse>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
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
        Call<ArrayList<DeviceResponse>> call = apiInterface.getPositionList(URLHelper.token,deviceId);
        call.enqueue(new Callback<ArrayList<DeviceResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<DeviceResponse>> call, Response<ArrayList<DeviceResponse>> response) {
                if(response.isSuccessful() && !response.body().isEmpty()) {
                    positionID = response.body().get(0).getPositionId();
                    requestProviderRunable = new Runnable() {
                        @Override
                        public void run() {
                            getData(positionID);
                            handleRequestProvider.postDelayed(this, 30*1000);
                        }
                    };
                    handleRequestProvider.removeCallbacks(requestProviderRunable);
                    handleRequestProvider.post(requestProviderRunable);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<DeviceResponse>> call, Throwable t) {

            }
        });

        // Add a marker in Sydney and move the camera
        

    }




}
