package com.bonree.mobile.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bonree.mobile.tools.FileUtils;

import executable.commands.shellservice.uiautomation.SystemManager;

public class MainActivity extends Activity {

    private static final String ASSETS_ADDON = "addon";

    private AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean b = SystemManager.upgradeRootPermission(getPackageCodePath());
        Log.e("currentprice", b + "");
        ImageView image = (ImageView) findViewById(R.id.bg);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setImageResource(R.drawable.newb);

        SharedPreferences shared = getSharedPreferences("init", Context.MODE_PRIVATE);
        if (shared.getBoolean("first_boot", true)) {
            Editor editor = shared.edit();
            editor.putBoolean("first_boot", false);
            editor.apply();

            loadFiles();
        }

        Intent startIntent = getIntent();
        if (startIntent != null) {
            String packageName = startIntent.getStringExtra("stock_package");
            createConfigTxt(packageName);
        }
    }

    private void loadFiles() {
        FileUtils.setMod(getFilesDir(), true, true, true);

        //for hook use
        File md5Dir = new File(getFilesDir(), ".md5");
        FileUtils.mkdir(md5Dir.getAbsolutePath());
        FileUtils.setMod(md5Dir, true, true, true);

        assetManager = getAssets();
        copyFileList(ASSETS_ADDON, getFilesDir().getAbsolutePath());
    }

    private void copyFileList(String assetsDir, String dstDir) {
        try {
            String[] fileNames = assetManager.list(assetsDir);

            if (fileNames == null) {
                return;
            }

            if (fileNames.length == 0) {
                //运行到这，有2种可能：
                //1、这是一个普通文件
                //2、这是一个不存在的文件
                //按我们的程序流程，这只能是一个普通文件
                FileUtils.copy(assetManager.open(assetsDir), dstDir);
                FileUtils.setMod(dstDir, true, true, true);
                return;
            }

            //到这说明这是一个文件夹
            FileUtils.mkdir(dstDir);
            FileUtils.setMod(dstDir, true, true, true);
            for (String fileName : fileNames) {
                copyFileList(assetsDir + File.separator + fileName, dstDir + File.separator + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfigTxt(String packageName) {
        if (packageName == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("PackageName=").append(packageName).append('\n')
            .append("Port=10020").append('\n')
            .append("NetHook=0").append('\n')
            .append("AppStartedVerify=null").append('\n');

        File configTxt = new File(getFilesDir(), "Config.txt");
        FileUtils.copy(new ByteArrayInputStream(builder.toString().getBytes(Charset.forName("utf-8"))), configTxt);
        FileUtils.setMod(configTxt, true, true, true);
    }
}
