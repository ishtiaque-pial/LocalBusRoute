package com.pial.localbusroute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceResponse {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("uniqueId")
    @Expose
    private String uniqueId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("lastUpdate")
    @Expose
    private String lastUpdate;
    @SerializedName("positionId")
    @Expose
    private Integer positionId;
    @SerializedName("groupId")
    @Expose
    private Integer groupId;
    @SerializedName("geofenceIds")
    @Expose
    private List<Object> geofenceIds = null;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("category")
    @Expose
    private Object category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Integer getPositionId() {
        return positionId;
    }

    public void setPositionId(Integer positionId) {
        this.positionId = positionId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public List<Object> getGeofenceIds() {
        return geofenceIds;
    }

    public void setGeofenceIds(List<Object> geofenceIds) {
        this.geofenceIds = geofenceIds;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Object getCategory() {
        return category;
    }

    public void setCategory(Object category) {
        this.category = category;
    }
    public class Attributes {


    }
}
