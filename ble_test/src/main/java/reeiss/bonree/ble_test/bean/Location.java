package reeiss.bonree.ble_test.bean;

import org.litepal.crud.LitePalSupport;

public class Location extends LitePalSupport {
    private long time;
    private double latitude;        //维度
    private double longitude;        //经度
    private String addStr;        //位置信息
    private String locationDescribe;        //位置描述
    private String mac;

    public Location() {
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Location(long time, double latitude, double longitude, String addrStr, String locationDescribe, boolean isLose) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addStr = addrStr;
        this.locationDescribe = locationDescribe;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddStr() {
        return addStr == null ? "" : addStr;
    }

    public void setAddStr(String addStr) {
        this.addStr = addStr;
    }

    public String getLocationDescribe() {
        return locationDescribe == null ? "" : locationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        this.locationDescribe = locationDescribe;
    }
}
