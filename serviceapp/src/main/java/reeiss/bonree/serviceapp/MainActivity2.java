package reeiss.bonree.serviceapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity2 extends Activity implements OnClickListener {
    private Button bt_play;
    private Button bt_pause;
    private Button bt_stop;
    private EditText et_path;
    private IService iService;
    private MyConn myConn;
    /**
     * 绑定服务是否成功
     */
    private boolean bind_success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        bt_play = (Button) findViewById(R.id.play);
        bt_pause = (Button) findViewById(R.id.pause);
        bt_stop = (Button) findViewById(R.id.stop);
        bt_pause.setOnClickListener(this);
        bt_stop.setOnClickListener(this);
        bt_play.setOnClickListener(this);
        et_path = (EditText) findViewById(R.id.et_path);

        et_path.setText(Environment.getExternalStorageDirectory().getPath() + "/aaa.mp3");
        // 绑定服务
        Intent intent = new Intent(this, MusicService.class);
        myConn = new MyConn();
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(myConn);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("提醒:");
        builder.setMessage("是否在后台继续播放音乐?");
        builder.setPositiveButton("继续播放",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        Toast.makeText(MainActivity2.this, "此功能在只绑定服务的条件下不能实现！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton("停止播放",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        builder.show();
    }

    public void onClick(View v) {
        String path = et_path.getText().toString().trim();
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "路径不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(path);
        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bind_success) {
            // 初始化媒体播放器 （只需要初始化一次即可）
            iService.init(path);
            bind_success = false;
        }

        switch (v.getId()) {
            case R.id.pause:// 暂停
                iService.pauseInService();
                break;
            case R.id.stop:// 停止
                iService.stopInService();
                break;
            case R.id.play:// 播放
                iService.playInService();
                break;
        }
    }

    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("绑定服务成功");
            iService = (IService) service;
//			iService.init(path);
            bind_success = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}
