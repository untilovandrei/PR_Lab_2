package com.example.andrei.pr_lab2_metricsaggregator;

/**
 * Created by andrei on 3/12/18.
 */

public class AggregatedDevice {
    private String deviceId;
    private String sensorType;
    private String sensorValue;


    public AggregatedDevice(String deviceId, String sensorType, String sensorValue) {
        this.deviceId = deviceId;
        this.sensorType = sensorType;
        this.sensorValue = sensorValue;
    }
    public AggregatedDevice(){

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(String sensorValue) {
        this.sensorValue = sensorValue;
    }

    @Override
    public String toString() {
        return "Device{" + sensorType + ": \n "+"Device "+deviceId+" - "+sensorValue+"}";
    }
}
