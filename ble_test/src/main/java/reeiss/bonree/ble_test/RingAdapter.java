package reeiss.bonree.ble_test;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class RingAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<Integer> resList;

    public RingAdapter(Context context) {
        this.context = context;
        resList = new ArrayList<>();
        resList.add(R.raw.ring0);
        resList.add(R.raw.ring1);
        resList.add(R.raw.ring2);
        resList.add(R.raw.ring3);
        resList.add(R.raw.ring4);
        resList.add(R.raw.ring5);
        resList.add(R.raw.ring6);
        resList.add(R.raw.ring7);
        resList.add(R.raw.ring8);
       /* Field[] fields = R.raw.class.getDeclaredFields();
        String rawName;
        for (int i = 1; i < fields.length; i++) {
            rawName = fields[i].getName();
            try {
                resList.add(fields[i].getInt(R.raw.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        fields = null;
       // context.getResources().openRawResource(R.raw.ring0);*/
    }

    @Override
    public int getCount() {
        return resList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_dialog_ring, null);

        return null;
    }
}
