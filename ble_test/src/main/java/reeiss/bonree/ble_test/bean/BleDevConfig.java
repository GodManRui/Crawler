package reeiss.bonree.ble_test.bean;

import org.litepal.crud.LitePalSupport;

public class BleDevConfig extends LitePalSupport {
    private long id;
    private String mac;
    private String alias;
    private int ringPosition;
    private String ringName;
    private int ringResId;
    private int alertMargin = 2; //0近1中2远

    public int getAlertMargin() {
        return alertMargin;
    }

    public void setAlertMargin(int alertMargin) {
        this.alertMargin = alertMargin;
    }

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

    public BleDevConfig(String mac, String alias, String ringName, int ringPosition, int ringResId, int alertMargin) {
        this.mac = mac;
        this.alias = alias;
        this.ringPosition = ringPosition;
        this.ringName = ringName;
        this.ringResId = ringResId;
        this.alertMargin = alertMargin;
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
