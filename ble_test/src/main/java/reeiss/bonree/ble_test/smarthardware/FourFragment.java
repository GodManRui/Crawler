package reeiss.bonree.ble_test.smarthardware;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import reeiss.bonree.ble_test.R;

/**
 * Wang YaHui
 * 2018/6/1822:50
 */

public class FourFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fore, null);
        return view;
    }
}
