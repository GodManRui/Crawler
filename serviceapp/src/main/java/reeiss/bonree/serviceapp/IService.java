package reeiss.bonree.serviceapp;

public interface IService {
    /**
     * 播放音乐
     */
    public void playInService();

    /**
     * 暂停音乐
     */
    public void pauseInService();

    /**
     * 停止播放
     */
    public void stopInService();

    /**
     * 初始化媒体播放器
     *
     * @param path
     */
    public void init(String path);
}
