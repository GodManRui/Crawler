package reeiss.bonree.ble_test.smarthardware;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.bean.EventAddDev;
import reeiss.bonree.ble_test.bean.EventEditName;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.smarthardware.activity.BindDevActivity;
import reeiss.bonree.ble_test.smarthardware.adapter.MyFragAdapter;
import reeiss.bonree.ble_test.smarthardware.fragment.FirstFragment;
import reeiss.bonree.ble_test.smarthardware.fragment.FourFragment;
import reeiss.bonree.ble_test.smarthardware.fragment.SecondFragment;
import reeiss.bonree.ble_test.smarthardware.fragment.ThreeFragment;
import reeiss.bonree.ble_test.smarthardware.service.BlueService;
import reeiss.bonree.ble_test.utils.BottomNavigationViewHelper;
import reeiss.bonree.ble_test.utils.T;

public class MainActivity extends AppCompatActivity {

    public BlueService iService;
    private BottomNavigationView mBottomNavigationView;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BlueService.MyBinder binder = (BlueService.MyBinder) service;
            iService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private NoScrollViewPager vpFragment;
    private ArrayList<Fragment> listFragment;
    private String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbaidu);
        Log.e("JerryZhuMM", "Main onCreate: 创建activity实例 " + savedInstanceState);
//        FragmentFactory.getInstance().exit(null);
        if (savedInstanceState != null) {
            String currentName = savedInstanceState.getString("currentName");
//            listFragment = (ArrayList<Fragment>) savedInstanceState.getSerializable("listFragment");
            this.currentName = TextUtils.isEmpty(currentName) ? "设备管理" : currentName;
            Log.e("jerry", "onCreate: 设置标题: 设备管理");
            setTitle(this.currentName);
        }
        initView();


        Intent intent = new Intent(this, BlueService.class);
        startService(intent);
        bindService(intent, conn, BIND_AUTO_CREATE);

        if (!XFBluetooth.getInstance(getApplicationContext()).isOpenBlueTooth()) {
            T.show(this, "设备不支持蓝牙或没有相关权限");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        vpFragment = (NoScrollViewPager) findViewById(R.id.vp_fragment);
        vpFragment.setScroll(false);
        vpFragment.setOffscreenPageLimit(4);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onTabItemSelected(item.getItemId());
            }
        });
        vpFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mBottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        if (listFragment == null) {
            listFragment = new ArrayList<>();
            listFragment.add(new FirstFragment());
            listFragment.add(new SecondFragment());
            listFragment.add(new ThreeFragment());
            listFragment.add(new FourFragment());
        }
        MyFragAdapter myAdapter = new MyFragAdapter(getSupportFragmentManager(), this, listFragment);
        vpFragment.setAdapter(myAdapter);
        mBottomNavigationView.setSelectedItemId(R.id.tab_menu_home);
//        onTabItemSelected(R.id.tab_menu_home);
    }

    private boolean onTabItemSelected(int itemId) {
        View v = findViewById(R.id.action_add);
        switch (itemId) {
            case R.id.tab_menu_home:
                vpFragment.setCurrentItem(0);
                setSelectTitle(0);
                if (v != null && v.getVisibility() == View.GONE)
                    v.setVisibility(View.VISIBLE);
                return true;
            case R.id.tab_menu_discovery:
                vpFragment.setCurrentItem(1);
                setSelectTitle(1);
                if (v != null && v.getVisibility() == View.VISIBLE)
                    v.setVisibility(View.GONE);
                return true;
            case R.id.tab_menu_attention:
                vpFragment.setCurrentItem(2);
                setSelectTitle(2);
                if (v != null && v.getVisibility() == View.VISIBLE)
                    v.findViewById(R.id.action_add).setVisibility(View.GONE);
                return true;
            case R.id.tab_menu_profile:
                vpFragment.setCurrentItem(3);
                setSelectTitle(3);
                if (v != null && v.getVisibility() == View.VISIBLE)
                    v.findViewById(R.id.action_add).setVisibility(View.GONE);
                return true;
            default:
                break;
        }
        return false;
    }

    public void setSelectTitle(int selectTitle) {
      /*  SecondFragment fragment = (SecondFragment) listFragment.get(1);
        if (selectTitle == 1) {
            fragment.onMyResume();
        } else {
            fragment.onMyPause();
        }*/
        switch (selectTitle) {
            case 0:
                currentName = "设备管理";
                break;
            case 1:
                currentName = "拍照";
                break;
            case 2:
                currentName = "定位";
                break;
            case 3:
                currentName = "更多";
                break;
        }
        setTitle(currentName);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("JerryZhuMM", " Main onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("currentName", currentName);
//        outState.putSerializable("listFragment", listFragment);
        super.onSaveInstanceState(outState);
        Log.e("JerryZhuMM", " Main onSaveInstanceState(Bundle outState保存状态)");
    }

    /*   @Override
       public void onBackPressed() {
           Intent intent = new Intent(Intent.ACTION_MAIN);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           intent.addCategory(Intent.CATEGORY_HOME);
           startActivity(intent);
       }
   */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addDev();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //跳转到添加设备界面
    public void addDev() {
        if (!XFBluetooth.getInstance(this).getAdapter().isEnabled()) {
            T.show(this, "请先打开蓝牙再扫描");
            return;
        }
        Intent intent = new Intent(this, BindDevActivity.class);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 200) {
            boolean addBindDev = data.getBooleanExtra("addBindDev", true);
            if (addBindDev) {
                EventBus.getDefault().post(new EventAddDev());
            }
        } else if (resultCode == 100) {
            String newName = data.getStringExtra("newName");
            EventBus.getDefault().post(new EventEditName(newName));
        }
    }
}
