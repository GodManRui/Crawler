package reeiss.bonree.crawler.testdialog;

import java.util.ArrayList;

/**
 * Created by GodRui on 2018/2/1.
 */

public class SmartPopupItem {
    private int resID;
    private ArrayList<String> list;
    private String[] btnName;

    public SmartPopupItem(int resID, String[] btnName) {
        this.resID = resID;
        this.btnName = btnName;
        list = new ArrayList<>(btnName.length);
        for (int i = 0; i < btnName.length; i++) {
            list.add(btnName[i]);
        }
    }


    public String[] getBtnName() {
        return btnName;
    }

    public void setBtnName(String[] btnName) {

    }


    public SmartPopupItem(int resID) {
        this.resID = resID;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public ArrayList<String> getList() {
        return list;
    }
}
