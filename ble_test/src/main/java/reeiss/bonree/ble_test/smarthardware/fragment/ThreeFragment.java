package reeiss.bonree.ble_test.smarthardware.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.EventBusLocation;
import reeiss.bonree.ble_test.bean.Location;
import reeiss.bonree.ble_test.smarthardware.activity.LostHistory;

/**
 * Wang YaHui
 * 2018/6/1822:49
 */

public class ThreeFragment extends Fragment {

    private TextureMapView map;
    private BaiduMap mBaiduMap;
    //    private boolean isLost = false;

    /***
     * 接收定位结果消息，并显示在地图上
     */


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("JerryZhu", "onHiddenChanged: Three");
        if (!hidden) {
            getActivity().setTitle("定位");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_three, null);
//        getActivity().setTitle("定位");
        map = view.findViewById(R.id.map);
        view.findViewById(R.id.bt_lost).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LostHistory.class));
            }
        });

        mBaiduMap = this.map.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));

        Location location = LitePal.findLast(Location.class);
        if (location != null) {
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark);
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            mBaiduMap.addOverlay(option);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));
        }
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().unregister(this);
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        locationService.unregisterListener(listener);
//        locationService.stop();
        map.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void EventLocation(EventBusLocation messageEvent) {
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(messageEvent.options);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(messageEvent.point));
    }


}
