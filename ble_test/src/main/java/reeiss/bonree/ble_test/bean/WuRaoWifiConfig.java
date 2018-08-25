package reeiss.bonree.ble_test.bean;


import org.litepal.crud.LitePalSupport;

public class WuRaoWifiConfig extends LitePalSupport {
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    private String wifiName;
    private String wifiNick;
    private String wifiMac;

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiNick() {
        return wifiNick;
    }

    public void setWifiNick(String wifiNick) {
        this.wifiNick = wifiNick;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
    }

    public WuRaoWifiConfig(String wifiName, String wifiNick, String wifiMac) {
        this.wifiName = wifiName;
        this.wifiNick = wifiNick;
        this.wifiMac = wifiMac;
    }
}
