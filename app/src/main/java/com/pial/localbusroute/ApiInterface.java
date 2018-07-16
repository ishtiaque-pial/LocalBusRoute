package com.pial.localbusroute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("Route")
    Call<ArrayList<BusRouteResponse>> ARRAY_LIST_CALL();

    @GET("api/devices/")
    Call<ArrayList<DeviceResponse>> getPositionList(@Header("Authorization") String basic,@Query("id") int id);

    @GET("api/positions/")
    Call<ArrayList<PositionResponse>> getLatlngList(@Header("Authorization") String basic,@Query("id") int id);
}
