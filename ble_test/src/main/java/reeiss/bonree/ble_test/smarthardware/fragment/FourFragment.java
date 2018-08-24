package reeiss.bonree.ble_test.smarthardware.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.smarthardware.activity.WifiSpoceActivity;

/**
 * Wang YaHui
 * 2018/6/1822:50
 */

public class FourFragment extends Fragment {

    private Switch swWifi;
    private Switch swSleep;
    private SharedPreferences myPreference;

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
        myPreference = ((getActivity()).getSharedPreferences("myPreference", Context.MODE_PRIVATE));
        boolean isOpenWuRao = myPreference.getBoolean("isOpenWuRao", false);
        swWifi.setChecked(isOpenWuRao);

        swWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (TextUtils.isEmpty(CURRENT_DEV_MAC)) return;

                SharedPreferences.Editor edit = myPreference.edit();
                if (isChecked) {
                    edit.putBoolean("isOpenWuRao", true).apply();
                    startActivity(new Intent(getActivity(), WifiSpoceActivity.class));
                } else {
                    edit.putBoolean("isOpenWuRao", false).apply();
                 /*   BleDevConfig currentDev = XFBluetooth.getCurrentDevConfig();
                    if (currentDev == null) return;
                    currentDev.setIsWuRao("false");
                    currentDev.update(currentDev.getId());*/
                }
            }
        });

        swSleep = view.findViewById(R.id.sw_sleep);
    }

}
