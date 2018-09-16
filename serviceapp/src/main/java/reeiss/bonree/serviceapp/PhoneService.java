package reeiss.bonree.serviceapp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class PhoneService extends Service {

    private TelephonyManager tm;
    private MyPhoneListener listener;
    private MediaRecorder mRecorder;
    private int index = 0;
    private AlertDialog ad;

    @Override
    public void onCreate() {
        Log.e("JerryZhu", "onCreate: 服务被创建");
        //获取电话管理器
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyPhoneListener();
        //注册电话状态的监听器
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("JerryZhu", "onStart: ");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        index++;
        if (index > 1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("有新消息，是否查看？");
            alertDialog.setTitle("丢失报警");
            alertDialog.setPositiveButton("否",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

            alertDialog.setNegativeButton("是",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

            ad = alertDialog.create();
            ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            ad.setCanceledOnTouchOutside(false);//点击外面区域不会让dialog消失
            Toast.makeText(this, "弹窗出来", Toast.LENGTH_SHORT).show();
            ad.show();
        }
        Log.e("JerryZhu", "onStartCommand: ");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e("JerryZhu", "onDestroy: 服务被销毁");

        //注销电话状态的监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("JerryZhu", "onBind: null");

        return null;
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        mRecorder = new MediaRecorder();
        //设置声音来源  从麦克风录音
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//设置输出格式为3gp
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //设置输出到的文件
        String path = Environment.getExternalStorageDirectory().getPath() + "/abc.3gp";
        mRecorder.setOutputFile(path);
        //设置输出的音频编码
        mRecorder.setAudioEncoder(MediaRecorder.
            AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            Log.e("JerryZhu", " 录音机,录音.: prepare() failed" + e);
            e.printStackTrace();
            stopSelf();
        }
        mRecorder.start();
    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        mRecorder.stop();
        //释放录音器资源
        mRecorder.release();
        mRecorder = null;
    }

    private class MyPhoneListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                // 空闲状态,代表当前没有电话
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mRecorder != null) {
                        Log.e("JerryZhu", "停止录音: ");
                        stopRecording();
                    }
                    break;
                // 响铃状态
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
                // 通话状态
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.e("JerryZhu", "通话状态,开启录音机,录音.: ");
                    startRecording();
                    break;
            }
        }
    }


}
