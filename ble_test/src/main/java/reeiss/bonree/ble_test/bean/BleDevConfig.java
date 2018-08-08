package reeiss.bonree.ble_test.bean;

import org.litepal.crud.LitePalSupport;

public class BleDevConfig extends LitePalSupport {
    public long id;
    private String mac;
    private String alias;
    private boolean isAlert;
    private int ringPosition;
    private int ringResId;

    public BleDevConfig(String mac) {
        this.mac = mac;
    }

    public BleDevConfig(String mac, String alias, boolean isAlert, int ringPosition, int ringResId) {
        this.mac = mac;
        this.alias = alias;
        this.isAlert = isAlert;
        this.ringPosition = ringPosition;
        this.ringResId = ringResId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isAlert() {
        return isAlert;
    }

    public void setAlert(boolean alert) {
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
