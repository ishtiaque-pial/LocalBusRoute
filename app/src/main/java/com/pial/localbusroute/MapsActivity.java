package com.pial.localbusroute;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog progress;
    ParserTask parserTask;
    private ApiInterface apiInterface;
    String origin="",destinatin="",waypoint="";
    String url="";

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        // Add a marker in Sydney and move the camera


        Call<ArrayList<BusRouteResponse>> arrayListCall = apiInterface.ARRAY_LIST_CALL();
        arrayListCall.enqueue(new Callback<ArrayList<BusRouteResponse>>() {
            @Override
            public void onResponse(Call<ArrayList<BusRouteResponse>> call, Response<ArrayList<BusRouteResponse>> response) {
                if (response.isSuccessful()) {



                    if (!response.body().isEmpty()) {

                        for (int i =0;i<response.body().size();i++) {
                            LatLng sydney = new LatLng(Double.parseDouble(response.body().get(i).getLat()),Double.parseDouble(response.body().get(i).getLong()));
                            mMap.addMarker(new MarkerOptions().position(sydney).title(response.body().get(i).getBusStop()));
                            if (i==0) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                                origin=response.body().get(i).getLat()+","+response.body().get(i).getLong();
                            } else if (i==response.body().size()-1){
                                destinatin=response.body().get(i).getLat()+","+response.body().get(i).getLong();
                            } else {
                                if (response.body().size() - 2 == i) {
                                    waypoint = (new StringBuilder()).append(waypoint).append(response.body().get(i).getLat() + "," + response.body().get(i).getLong()).toString();
                                } else {
                                    waypoint = (new StringBuilder()).append(waypoint).append(response.body().get(i).getLat() + "," + response.body().get(i).getLong() + "|").toString();
                                }
                            }
                        }
                        url="https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destinatin+"&mode=driving&waypoints="+waypoint+"&key=AIzaSyDPV0dUJK7Ad0eTO1BGC-faYU511Y9kVM0";
                        Log.e("Errrrror",url);
                        FetchUrl fetchUrl = new FetchUrl(url);
                        fetchUrl.execute(url);
                    }

                } else {
                    Toast.makeText(MapsActivity.this, "something wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BusRouteResponse>> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });


    }



    private class FetchUrl extends AsyncTask<String, Void, String> {
        String approx;

        public FetchUrl(String approx) {
            this.approx = approx;
        }

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {

            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObj = new JSONObject(result);
                if (!jsonObj.optString("status").equalsIgnoreCase("ZERO_RESULTS")) {
                    parserTask = new ParserTask(approx);
                    // Invokes the thread for parsing the JSON data
                    parserTask.execute(result);
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        DataParser parser;
        String approx;

        public ParserTask(String approx) {
            this.approx = approx;
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                parser = new DataParser();

                // Starts parsing data
                routes = parser.parse(jObject);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            if (result != null) {
                // Traversing through all the routes
                if (result.size() > 0) {
                    for (int i = 0; i < result.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = result.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(5);
                        lineOptions.color(Color.BLACK);


                    }

                    if (lineOptions != null && points != null) {
                        //mMap.addPolyline(lineOptions);
                        startAnim(points);
                    }
                }
            }

        }
    }

    private void startAnim(ArrayList<LatLng> routeList) {
        if (mMap != null && routeList.size() > 1) {
            MapAnimator.getInstance().animateRoute(this, mMap, routeList);
        }
    }
}
