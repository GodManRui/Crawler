package reeiss.bonree.ble_test.smarthardware.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.smarthardware.activity.WifiSpoceActivity;

import static reeiss.bonree.ble_test.blehelp.XFBluetooth.CURRENT_DEV_MAC;

/**
 * Wang YaHui
 * 2018/6/1822:50
 */

public class FourFragment extends Fragment {

    private Switch swWifi;
    private Switch swSleep;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle("更多");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fourth, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("更多");
        swWifi = view.findViewById(R.id.sw_wifi_wurao);

        BleDevConfig currentDevConfig = XFBluetooth.getCurrentDevConfig();
        if (currentDevConfig != null) {
            swWifi.setChecked(currentDevConfig.getIsWuRao().equals("true"));
        }
        swWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TextUtils.isEmpty(CURRENT_DEV_MAC)) return;
                if (isChecked)
                    startActivity(new Intent(getActivity(), WifiSpoceActivity.class));
                else {
                    BleDevConfig currentDev = XFBluetooth.getCurrentDevConfig();
                    if (currentDev == null) return;
                    currentDev.setIsWuRao("false");
                    currentDev.update(currentDev.getId());
                }
            }
        });

        swSleep = view.findViewById(R.id.sw_sleep);
    }

}
