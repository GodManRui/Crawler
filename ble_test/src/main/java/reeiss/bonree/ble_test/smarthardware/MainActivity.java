package reeiss.bonree.ble_test.smarthardware;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.smarthardware.service.BlueService;
import reeiss.bonree.ble_test.smarthardware.service.IService;
import reeiss.bonree.ble_test.utils.BottomNavigationViewHelper;
import reeiss.bonree.ble_test.utils.FragmentFactory;

import static reeiss.bonree.ble_test.utils.FragmentFactory.FIRST;
import static reeiss.bonree.ble_test.utils.FragmentFactory.FOUR;
import static reeiss.bonree.ble_test.utils.FragmentFactory.SECOND;
import static reeiss.bonree.ble_test.utils.FragmentFactory.THREE;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private IService iService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbaidu);
        Log.e("JerryZhuMM", "Main onCreate: " + savedInstanceState);
        initView();
        if (savedInstanceState == null) {
            mBottomNavigationView.setSelectedItemId(R.id.tab_menu_home);
        }
        Intent intent = new Intent(this, BlueService.class);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iService = (IService) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },BIND_AUTO_CREATE);
    }

    private void initView() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.e("JerryZhuMM", "Main setOnNavigationItemSelectedListener: " + item.getItemId());
                onTabItemSelected(item.getItemId());
                return true;
            }
        });
//        onTabItemSelected(R.id.tab_menu_home);
    }

    private void onTabItemSelected(int id) {
        switch (id) {
            case R.id.tab_menu_home:
                FragmentFactory.getInstance().changeFragment(FIRST, R.id.fragment, getFragmentManager());
                break;
            case R.id.tab_menu_discovery:
                FragmentFactory.getInstance().changeFragment(SECOND, R.id.fragment, getFragmentManager());
                break;
            case R.id.tab_menu_attention:
                FragmentFactory.getInstance().changeFragment(THREE, R.id.fragment, getFragmentManager());
                break;
            case R.id.tab_menu_profile:
                FragmentFactory.getInstance().changeFragment(FOUR, R.id.fragment, getFragmentManager());
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("JerryZhuMM", " Main onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("JerryZhuMM", " Main onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("JerryZhuMM", " Main onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("JerryZhuMM", " Main onSaveInstanceState(Bundle outState");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("JerryZhuMM", " Main onPause");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("JerryZhuMM", " Main onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("JerryZhuMM", " Main onResume");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("JerryZhuMM", " Main onRestoreInstanceState(Bundle savedInstanceState)");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.e("JerryZhuMM", " Main onRestoreInstanceState(Bundle savedInstanceState  PersistableBundle persistentState)");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("JerryZhuMM", " Main onRestart");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.e("JerryZhuMM", " Main onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) ");
    }
}
