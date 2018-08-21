package reeiss.bonree.ble_test;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.LinkedList;

import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.bean.EventBusLocation;
import reeiss.bonree.ble_test.bean.Location;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.utils.Utils;
import tech.linjiang.pandora.Pandora;

/**
 * Wang YaHui
 * 2018/6/1817:08
 */

public class LocationApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>();
    public Location mLocation = new Location();


    /***
     * 接收定位结果消息，并显示在地图上
     */
    @SuppressLint("HandlerLeak")
    private Handler locHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try {
                BDLocation location = msg.getData().getParcelable("loc");
                int isCal = msg.getData().getInt("iscalculate");
                if (location != null) {             //纬度                        //经度
                    LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                    BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();

                    if (currentDevConfig != null) {
                        Log.e("JerryZhu", "当前位置Str: " + location.getAddrStr() + "    描述：" + location.getLocationDescribe() + "  ==  " + currentDevConfig.getAlias() + "   MAC =   " + currentDevConfig.getMac());
                        mLocation.setMac(currentDevConfig.getMac());
                        mLocation.setName(currentDevConfig.getAlias());

                        mLocation.setTime(System.currentTimeMillis());
                        mLocation.setLatitude(location.getLatitude());
                        mLocation.setLongitude(location.getLongitude());
                        mLocation.setAddStr(location.getAddrStr());
                        mLocation.setLocationDescribe(location.getLocationDescribe());
                    } else {
                        Log.e("JerryZhu", "空！！！！！！！！当前位置Str: " + location.getAddrStr() + "    描述：" + location.getLocationDescribe());
                    }

//                    T.show(getActivity(), location.getLocationDescribe());
                    // 构建Marker图标
                    BitmapDescriptor bitmap = null;
                    if (isCal == 0) {
                        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark); // 非推算结果
                    } else {
                        bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_focuse_mark); // 推算结果
                    }

                    // 构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
                    EventBus.getDefault().post(new EventBusLocation(option, point));

                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };
    BDAbstractLocationListener listener = new BDAbstractLocationListener() {
        //百度地图定位回调
        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub

            if (location != null && (location.getLocType() == 161 || location.getLocType() == 66)) {
                Message locMsg = locHandler.obtainMessage();
                Bundle locData;
                locData = Algorithm(location);
                if (locData != null) {
                    locData.putParcelable("loc", location);
                    locMsg.setData(locData);
                    locHandler.sendMessage(locMsg);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        LocationClientOption mOption = locationService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mOption.setCoorType("bd09ll");
        mOption.setScanSpan(10000);
        mOption.setIsNeedAddress(true);

        locationService.setLocationOption(mOption);
        locationService.registerListener(listener);
//        locationService.start();

        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
        XFBluetooth.getInstance(this);
        LitePal.initialize(this);
//        Pandora.init(this).enableShakeOpen();
        try {
            Pandora.get().open();
        } catch (Exception e) {

        }
    }


    private Bundle Algorithm(BDLocation location) {
        Bundle locData = new Bundle();
        double curSpeed = 0;
        if (locationList.isEmpty() || locationList.size() < 2) {
            LocationEntity temp = new LocationEntity();
            temp.location = location;
            temp.time = System.currentTimeMillis();
            locData.putInt("iscalculate", 0);
            locationList.add(temp);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
                        locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
                score += curSpeed * Utils.EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) { // 经验值,开发者可根据业务自行调整，也可以不使用这种算法
                location.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
                                / 2);
                location.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
                                / 2);
                locData.putInt("iscalculate", 1);
            } else {
                locData.putInt("iscalculate", 0);
            }
            LocationEntity newLocation = new LocationEntity();
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);

        }
        return locData;
    }

    class LocationEntity {
        BDLocation location;
        long time;
    }

}
