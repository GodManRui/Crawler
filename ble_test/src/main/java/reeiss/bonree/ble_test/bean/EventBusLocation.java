package reeiss.bonree.ble_test.bean;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

public class EventBusLocation {
    public OverlayOptions options;
    public LatLng point;

    public EventBusLocation(OverlayOptions options, LatLng point) {
        this.options = options;
        this.point = point;
    }
}
