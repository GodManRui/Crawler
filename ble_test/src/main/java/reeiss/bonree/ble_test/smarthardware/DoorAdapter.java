package reeiss.bonree.ble_test.smarthardware;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import reeiss.bonree.ble_test.R;

public class DoorAdapter extends RecyclerView.Adapter {
    private List<DataBean> mData;
    private Context context;

    public DoorAdapter(Context context, List<DataBean> mData) {
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.item_door, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataBean dataBean = mData.get(position);
        MyHolder mHolder = (MyHolder) holder;
        mHolder.tvTime.setText(dataBean.getTime());
        mHolder.tvStatus.setText(dataBean.getStatus());
        mHolder.imStatus.setImageResource(dataBean.getOpen() == 1 ? R.mipmap.device_main : R.mipmap.widget_bar_location_over);

        mHolder.tvAlert.setVisibility(dataBean.isAlert() ? View.VISIBLE : View.GONE);
        if (position == 0)
            mHolder.vTop.setVisibility(View.INVISIBLE);
        else if (mHolder.vTop.getVisibility() == View.INVISIBLE)
            mHolder.vTop.setVisibility(View.VISIBLE);

        if (position == mData.size() - 1)
            mHolder.vBottom.setVisibility(View.INVISIBLE);
        else if (mHolder.vBottom.getVisibility() == View.INVISIBLE)
            mHolder.vBottom.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        private TextView tvTime, tvStatus;
        private ImageView imStatus;
        private View tvAlert, vTop, vBottom;

        public MyHolder(View root) {
            super(root);
            tvTime = root.findViewById(R.id.tv_time);
            imStatus = root.findViewById(R.id.im_status);
            tvAlert = root.findViewById(R.id.tv_alert);
            tvStatus = root.findViewById(R.id.tv_status);
            vTop = root.findViewById(R.id.v_top);
            vBottom = root.findViewById(R.id.v_bottom);
        }
    }
}
