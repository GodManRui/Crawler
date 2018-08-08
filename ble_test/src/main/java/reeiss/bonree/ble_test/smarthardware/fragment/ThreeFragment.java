package reeiss.bonree.ble_test.smarthardware.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.LinkedList;

import reeiss.bonree.ble_test.LocationApplication;
import reeiss.bonree.ble_test.LocationService;
import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.Location;
import reeiss.bonree.ble_test.utils.Utils;

/**
 * Wang YaHui
 * 2018/6/1822:49
 */

public class ThreeFragment extends Fragment {

    private TextureMapView map;
    private BaiduMap mBaiduMap;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>();
    private LocationService locationService;
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
                    Log.e("JerryZhu", "当前位置: " + location.getAddrStr() + "    描述：" + location.getLocationDescribe());
                    new Location(System.currentTimeMillis(), location.getLatitude(), location.getLongitude(), location.getAddrStr(), location.getLocationDescribe(), false).save();
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
                    // 在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(option);
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle("定位");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //SDKInitializer.initialize(getActivity().getApplication());
        View view = inflater.inflate(R.layout.fragment_three, null);
        getActivity().setTitle("定位");
        map = view.findViewById(R.id.map);
//        reset = view.findViewById(R.id.clear);
        mBaiduMap = this.map.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

        locationService = ((LocationApplication) getActivity().getApplication()).locationService;
        LocationClientOption mOption = locationService.getDefaultLocationClientOption();
        mOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mOption.setCoorType("bd09ll");
        mOption.setScanSpan(6000);
        mOption.setIsNeedAddress(true);

        locationService.setLocationOption(mOption);
        locationService.registerListener(listener);
        locationService.start();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        map.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        locationService.unregisterListener(listener);
        locationService.stop();
        map.onDestroy();
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
