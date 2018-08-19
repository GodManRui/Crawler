package reeiss.bonree.ble_test.smarthardware;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.utils.BottomNavigationViewHelper;
import reeiss.bonree.ble_test.utils.FragmentFactory;

import static reeiss.bonree.ble_test.utils.FragmentFactory.FIRST;
import static reeiss.bonree.ble_test.utils.FragmentFactory.FOUR;
import static reeiss.bonree.ble_test.utils.FragmentFactory.SECOND;
import static reeiss.bonree.ble_test.utils.FragmentFactory.THREE;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainbaidu);
        initView();
    }

    private void initView() {
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onTabItemSelected(item.getItemId());
                return true;
            }
        });

        onTabItemSelected(R.id.tab_menu_home);

        ActionBar actionBar = getActionBar();

    }

    private void onTabItemSelected(int id) {
        switch (id) {
            case R.id.tab_menu_home:
                FragmentFactory.getInstance().changeFragment(FIRST, R.id.fragment, getSupportFragmentManager());
                break;
            case R.id.tab_menu_discovery:
                FragmentFactory.getInstance().changeFragment(SECOND, R.id.fragment, getSupportFragmentManager());
                break;
            case R.id.tab_menu_attention:
                FragmentFactory.getInstance().changeFragment(THREE, R.id.fragment, getSupportFragmentManager());
                break;
            case R.id.tab_menu_profile:
                FragmentFactory.getInstance().changeFragment(FOUR, R.id.fragment, getSupportFragmentManager());
                break;
        }

    }

}
