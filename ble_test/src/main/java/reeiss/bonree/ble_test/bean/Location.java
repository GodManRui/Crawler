package reeiss.bonree.ble_test.bean;

import org.litepal.crud.LitePalSupport;

public class Location extends LitePalSupport {
    private long time;
    private double latitude;        //维度
    private double longitude;        //经度
    private String addrStr;        //位置信息
    private String locationDescribe;        //位置描述
    private boolean isLose;

    public Location(long time, double latitude, double longitude, String addrStr, String locationDescribe, boolean isLose) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addrStr = addrStr;
        this.locationDescribe = locationDescribe;
        this.isLose = isLose;
    }

    public boolean isLose() {
        return isLose;
    }

    public void setLose(boolean lose) {
        isLose = lose;
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

    public String getAddrStr() {
        return addrStr == null ? "" : addrStr;
    }

    public void setAddrStr(String addrStr) {
        this.addrStr = addrStr;
    }

    public String getLocationDescribe() {
        return locationDescribe == null ? "" : locationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        this.locationDescribe = locationDescribe;
    }
}
