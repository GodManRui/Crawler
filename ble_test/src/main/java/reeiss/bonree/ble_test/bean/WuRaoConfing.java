package reeiss.bonree.ble_test.bean;

import org.litepal.LitePal;

public class WuRaoConfing extends LitePal {
    private String wifiName;

    public WuRaoConfing(String wifiName) {
        this.wifiName = wifiName;
    }

    public String getWifiName() {
        return wifiName;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }
}
