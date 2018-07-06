package com.pial.localbusroute;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    @GET("Route")
    Call<ArrayList<BusRouteResponse>> ARRAY_LIST_CALL();
}
