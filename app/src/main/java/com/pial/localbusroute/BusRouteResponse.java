package com.pial.localbusroute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusRouteResponse {
    @SerializedName("bus_stop")
    @Expose
    private String busStop;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("long")
    @Expose
    private String _long;

    public String getBusStop() {
        return busStop;
    }

    public void setBusStop(String busStop) {
        this.busStop = busStop;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return _long;
    }

    public void setLong(String _long) {
        this._long = _long;
    }

}
