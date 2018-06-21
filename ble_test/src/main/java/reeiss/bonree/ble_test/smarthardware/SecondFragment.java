package reeiss.bonree.ble_test.smarthardware;


import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import reeiss.bonree.ble_test.R;

public class SecondFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout mCameralayout;
    // 拍照回调
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (pictureDir == null) {
                Log.d("jerryzhu",
                        "Error creating media file, check storage permissions!");
                return;
            }

            try {
                String pictureName = new DateFormat().format("yyyyMMddHHmmss",
                        new Date()).toString()
                        + ".png";
                FileOutputStream fos = new FileOutputStream(pictureDir
                        + File.separator + pictureName);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("jerryz", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("jerryz", "Error accessing file: " + e.getMessage());
            }
        }
    };
    private Button mTakePictureBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, null);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!checkCameraHardware(getActivity())) {

        } else {
            mCamera = getCameraInstance();
            mPreview = new CameraPreview(getActivity(), mCamera);
            mCameralayout = (FrameLayout) getView().findViewById(R.id.camera_preview);
            mCameralayout.addView(mPreview);
        }

        mTakePictureBtn = (Button) getView().findViewById(R.id.button_capture);
        mTakePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPictureCallback);
            }
        });
    }

    // 判断相机是否支持
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    // 获取相机
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
}
