package reeiss.bonree.ble_test.utils;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import reeiss.bonree.ble_test.smarthardware.fragment.FirstFragment;
import reeiss.bonree.ble_test.smarthardware.fragment.FourFragment;
import reeiss.bonree.ble_test.smarthardware.fragment.SecondFragment;
import reeiss.bonree.ble_test.smarthardware.fragment.ThreeFragment;

/**
 * Created by GodRui on 2017/12/27.
 */
public class FragmentFactory {

    /**
     * 测试报告
     */
    public final static int FIRST = 1;
    /**
     * 用户中心
     */
    public final static int SECOND = 2;
    /**
     * 博睿NET
     */
    public final static int THREE = 3;
    /**
     * NET报警
     */
    public final static int FOUR = 4;

    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private ThreeFragment threeFragment;
    private FourFragment fourFragment;

    private Fragment cacheFragment;
    private static FragmentFactory mFactory;

    private FragmentFactory() {

    }

    public static FragmentFactory getInstance() {
        Log.e("jerry", "工厂实例地址 : " + mFactory);
        if (mFactory == null) {
            synchronized (FragmentFactory.class) {
                if (mFactory == null) {
                    mFactory = new FragmentFactory();
                }
            }
        }
        return mFactory;
    }

    public Fragment getNowFragment() {
        return cacheFragment;
    }

    /**
     * 切换Fragment
     *
     * @param type            Fragment类型
     * @param resourceId      Fragment布局
     * @param fragmentManager 管理器
     */
    public void changeFragment(int type, int resourceId,
                               FragmentManager fragmentManager) {

        Fragment target = getFragment(type);
        // 缓存cache第一次进来是空的要单独处理
        if (cacheFragment == null) {
            // 第一次进来要切换的fragment作为当前fragment加入到transaction中并显示
            fragmentManager.beginTransaction().add(resourceId, target)
                    .show(target).commit();
            cacheFragment = target;
            return;
        }
        // 如果目标fragment与当前fragment一样则直接返回不执行
        if (cacheFragment == target) {
            return;
        }
        // 如果目标fragment没有添加进来则添加进transaction中
        if (!target.isAdded()) {
            fragmentManager.beginTransaction().add(resourceId, target).commit();
        }
        // 隐藏当前的fragment示目标的fragment
        fragmentManager.beginTransaction().hide(cacheFragment).show(target)
                .commit();
        //将目标fragment复制给缓存标记
        cacheFragment = target;
    }

    /**
     * 获取fragment
     *
     * @param type fragment类型
     * @return fragment
     */
    public Fragment getFragment(int type) {
        Log.e("JerryZhuMM", " Factory :getFrag   " + firstFragment);
        switch (type) {
            case FIRST:
                if (firstFragment == null) {
                    Log.e("JerryZhuMM", " Factory :onlineTestFragment是空   " + firstFragment);
                    firstFragment = new FirstFragment();
                }
                return firstFragment;

            case SECOND:
                if (secondFragment == null) {
                    secondFragment = new SecondFragment();
                }
                return secondFragment;

            case THREE:
                if (threeFragment == null) {
                    threeFragment = new ThreeFragment();
                }
                return threeFragment;
            case FOUR:
                if (fourFragment == null) {
                    fourFragment = new FourFragment();
                }
                return fourFragment;

        }
        return null;
    }

    public boolean getFragmentStatus(int type) {
        switch (type) {
            case FIRST:
                if (firstFragment == null)
                    return false;
                else return true;
            case SECOND:
                if (secondFragment == null) return false;
                else return true;
            case THREE:
                if (threeFragment == null) return false;
                else return true;
            case FOUR:
                return false;
        }
        return false;
    }


    public void exit(FragmentManager fragmentManager) {
        if (fragmentManager != null)
            fragmentManager.beginTransaction().remove(firstFragment).remove(secondFragment).remove(threeFragment).remove(fourFragment).commit();
        firstFragment = null;
        secondFragment = null;
        threeFragment = null;
        fourFragment = null;
        cacheFragment = null;
    }

    public void saveInstanceState(Bundle outState, FragmentManager fragmentManager) {
        if (firstFragment != null)
            fragmentManager.putFragment(outState, FIRST + "", firstFragment);
        if (secondFragment != null)
            fragmentManager.putFragment(outState, SECOND + "", secondFragment);
        if (threeFragment != null)
            fragmentManager.putFragment(outState, THREE + "", threeFragment);
        if (fourFragment != null)
            fragmentManager.putFragment(outState, FOUR + "", fourFragment);
    }

    public void finish() {
        cacheFragment = null;
    }
}
