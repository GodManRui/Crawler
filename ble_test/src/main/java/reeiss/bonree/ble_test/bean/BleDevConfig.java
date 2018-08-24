package reeiss.bonree.ble_test.bean;

import org.litepal.crud.LitePalSupport;

public class BleDevConfig extends LitePalSupport {
    private long id;
    private String mac;
    private String alias;
    private String isAlert;
    private int ringPosition;
    private String ringName;
    private int ringResId;

    public BleDevConfig() {
    }


    public BleDevConfig(String mac) {
        this.mac = mac;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIsAlert() {
        return isAlert;
    }

    public void setIsAlert(String isAlert) {
        this.isAlert = isAlert;
    }

    public BleDevConfig(String mac, String alias, String isAlert, String ringName, int ringPosition, int ringResId) {
        this.mac = mac;
        this.alias = alias;
        this.isAlert = isAlert;
        this.ringPosition = ringPosition;
        this.ringName = ringName;
        this.ringResId = ringResId;

    }

    public String getRingName() {
        return ringName;
    }

    public void setRingName(String ringName) {
        this.ringName = ringName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlert() {
        return isAlert;
    }

    public void setAlert(String alert) {
        isAlert = alert;
    }

    public int getRingPosition() {
        return ringPosition;
    }

    public void setRingPosition(int ringPosition) {
        this.ringPosition = ringPosition;
    }

    public int getRingResId() {
        return ringResId;
    }

    public void setRingResId(int ringResId) {
        this.ringResId = ringResId;
    }
}
