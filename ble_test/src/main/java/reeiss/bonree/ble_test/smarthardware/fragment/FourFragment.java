package reeiss.bonree.ble_test.smarthardware.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.litepal.LitePal;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.BleDevConfig;
import reeiss.bonree.ble_test.smarthardware.activity.WifiSpoceActivity;

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
        swWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    startActivity(new Intent(getActivity(), WifiSpoceActivity.class));
                else {
                    BleDevConfig currentDev = LitePal.findFirst(BleDevConfig.class);
                    BleDevConfig bleDevConfig = new BleDevConfig();
                    bleDevConfig.setAlert("true");
                    bleDevConfig.update(currentDev.id);
                }
            }
        });

        swSleep = view.findViewById(R.id.sw_sleep);
    }

}
