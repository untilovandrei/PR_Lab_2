package com.example.andrei.pr_lab2_metricsaggregator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by andrei on 3/1/18.
 */
//XML
@Root
public class Device implements Serializable{
    //JSON
    @SerializedName("device_id")
    @Expose
    //XML
    @Element
    private String deviceId;
    @SerializedName("sensor_type")
    @Expose
    @Element
    private int sensorType;
    @SerializedName("value")
    @Expose
    @Element
    private double value;

    public Device(){

    }

    public Device(String deviceId) {
        this.deviceId = deviceId;
    }

    public Device(String deviceId, int sensorType, double value) {
        this.deviceId = deviceId;
        this.sensorType = sensorType;
        this.value = value;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSensorType(int sensorType) {
        this.sensorType = sensorType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Device{"+"device_id = "+deviceId+" ;\n "+"sensorType = "+sensorType+" ;\n "+"value = "+value+"}";
    }
}
