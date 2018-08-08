package reeiss.bonree.ble_test.smarthardware.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import reeiss.bonree.ble_test.R;

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
        swSleep = view.findViewById(R.id.sw_sleep);
    }
}
