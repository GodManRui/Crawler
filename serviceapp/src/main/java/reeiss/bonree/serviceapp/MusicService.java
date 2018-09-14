package reeiss.bonree.serviceapp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MusicService extends Service {
    //多媒体的播放器
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();//释放播放器.
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    /**
     * 初始化我们的媒体播放器
     *
     * @param path
     */
    private void initMediaPlayer(String path) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(MusicService.this, "播放完毕了", Toast.LENGTH_SHORT).show();
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepare();//准备开始播放音乐
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            //停止掉播放器之后立刻的重新准备好.
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class MyBinder extends Binder implements IService {
        @Override
        public void playInService() {
            play();
        }

        @Override
        public void pauseInService() {
            pause();
        }

        @Override
        public void stopInService() {
            stop();
        }

        @Override
        public void init(String path) {
            initMediaPlayer(path);
        }
    }

}
