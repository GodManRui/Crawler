package reeiss.bonree.ble_test.smarthardware.service;

public interface IService {

    /**
     * 初始化媒体播放器
     * @param path
     */
    public void init(String path);

    public void connect(String mac);

}
