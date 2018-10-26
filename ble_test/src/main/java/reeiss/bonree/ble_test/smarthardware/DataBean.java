package reeiss.bonree.ble_test.smarthardware;

public class DataBean {
    private int open;
    private boolean alert;
    private String status;
    private String time;

    public DataBean() {
    }

    public DataBean(int open, boolean alert, String status, String time) {

        this.open = open;
        this.alert = alert;
        this.status = status;
        this.time = time;
    }

    public DataBean(int open, boolean alert, String time) {
        this.open = open;
        this.alert = alert;
        this.time = time;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
