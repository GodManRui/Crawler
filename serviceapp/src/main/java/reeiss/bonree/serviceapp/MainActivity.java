package reeiss.bonree.serviceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStop() {
        Log.e("JerryZhu", "MainAct :onStop: ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("JerryZhu", "MainAct: onDestroy: ");
        super.onDestroy();
    }

    public void start(View view) {
        //创建一个Intent 对象
        Intent intent = new Intent(this, PhoneService.class);
        //启动服务
        startService(intent);
    }

}
