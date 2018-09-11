package reeiss.bonree.ble_test.smarthardware.fragment;


import android.app.Fragment;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.blehelp.XFBluetooth;
import reeiss.bonree.ble_test.blehelp.XFBluetoothCallBack;
import reeiss.bonree.ble_test.smarthardware.customview.CameraPreview;
import reeiss.bonree.ble_test.utils.T;

public class SecondFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout mCameraLayout;
    private int mCameraId = CameraInfo.CAMERA_FACING_BACK;
    private String picturePath;
    private ImageView imPicture;
    private boolean takePicture;
    // 拍照回调
    private PictureCallback mPictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            File pictureDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            picturePath = pictureDir
                + File.separator
                + new DateFormat().format("yyyyMMddHHmmss", new Date())
                .toString() + ".jpg";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = new File(picturePath);
                    try {
                        // 获取当前旋转角度, 并旋转图片
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (mCameraId == CameraInfo.CAMERA_FACING_BACK) {
                            bitmap = CameraPreview.rotateBitmapByDegree(bitmap, 90);
                        } else {
                            bitmap = CameraPreview.rotateBitmapByDegree(bitmap, -90);
                        }
                        BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        bos.flush();
                        bos.close();
                        bitmap.recycle();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Uri uri = Uri.fromFile(new File(picturePath));
                                imPicture.setImageURI(uri);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            mCamera.startPreview();
        }
    };
    private XFBluetoothCallBack gattCallback = new XFBluetoothCallBack() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            String value = Arrays.toString(characteristic.getValue());
            if (value.equals("[1]")) {
                if (mCamera != null) {
                    /*mCamera.autoFocus(autoFocusCallback);     手动对焦，才设置回调
                    takePicture = true;*/
                    mCamera.takePicture(null, null, mPictureCallback);
                    playSound();
                }
            }
            Log.e("jerry", "onCharacteristicChanged: " + value);
        }
    };
    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //success表示对焦成功
            if (success && takePicture) {
                T.show(getActivity(), "对焦成功，拍照");
                //shutter是快门按下时的回调，raw是获取拍照原始数据的回调，jpeg是获取经过压缩成jpg格式的图像数据的回调。
                mCamera.takePicture(null, null, mPictureCallback);
                playSound();
                takePicture = false;
                Log.i("jerry", "成功:success...");
                //myCamera.setOneShotPreviewCallback(null);
            } else {
                T.show(getActivity(), "对焦失败");
                //未对焦成功
                Log.i("jerry", "myAutoFocusCallback: 失败了...");
            }
        }
    };
    private Camera.Parameters parameters;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            releaseCamera();
            XFBluetooth.getInstance(getActivity()).removeBleCallBack(gattCallback);
        } else {
            getActivity().setTitle("拍照");
            openCamera();
            XFBluetooth.getInstance(getActivity()).addBleCallBack(gattCallback);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("拍照");
        mCameraLayout = (FrameLayout) getView().findViewById(R.id.camera_preview);
        XFBluetooth.getInstance(getActivity()).addBleCallBack(gattCallback);
        //Button mTakePictureBtn = (Button) getView().findViewById(R.id.button_capture);
        // Button mChangeCarema = (Button) getView().findViewById(R.id.change);
        getView().findViewById(R.id.button_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
                   /* takePicture = true;           //手动对焦开启 回调
                    mCamera.autoFocus(autoFocusCallback);*/
                    //shutter是快门按下时的回调，raw是获取拍照原始数据的回调，jpeg是获取经过压缩成jpg格式的图像数据的回调。
                    mCamera.takePicture(null, null, mPictureCallback);
                    playSound();
                }
            }
        });
        getView().findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraId == CameraInfo.CAMERA_FACING_BACK)
                    mCameraId = CameraInfo.CAMERA_FACING_FRONT;
                else mCameraId = CameraInfo.CAMERA_FACING_BACK;
                releaseCamera();
                openCamera();
            }
        });
        imPicture = getView().findViewById(R.id.im_picture);
        imPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picturePath == null || picturePath.isEmpty()) return;
                Intent intent = new Intent(Intent.ACTION_VIEW);    //打开图片得启动ACTION_VIEW意图
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                if (bitmap == null) return;
                //将图片转换为bitmap格式
                String uriString = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null);
                Uri uri = Uri.parse(uriString);    //将bitmap转换为uri
                intent.setDataAndType(uri, "image/*");    //设置intent数据和图片格式
                startActivity(intent);
            }
        });
        openCamera();
    }

    /**
     * 播放系统拍照声音
     */
    public void playSound() {
        MediaPlayer mediaPlayer = null;
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (mediaPlayer == null)
                mediaPlayer = MediaPlayer.create(getActivity(),
                    Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }
    }

    // 释放相机
    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        mPreview = null;
        mCameraLayout.removeAllViews();
    }

    // 开始预览相机
    //  闪光灯      parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
    private void openCamera() {
        if (!checkCameraHardware(getActivity())) {
            T.show(getActivity(), "权限被拒绝！");
            return;
        }
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getActivity(), mCamera);

        parameters = mCamera.getParameters();
        parameters.setPictureFormat(PixelFormat.JPEG);
        if (!Build.MODEL.equals("KORIDY H30")) {
            T.show(getActivity(), "连续对焦");
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦  解开这个注释，并且跟手动对焦冲突，需要删掉手动对焦代码
        } else {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);       手动对焦，直接用这个模式

       /* WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int screenHeight = display.getHeight();
        parameters.setPictureSize(screenWidth, screenHeight);*/
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        List<Camera.Size> SupportedPreviewSizes = parameters.getSupportedPreviewSizes();// 获取支持预览照片的尺寸
        Camera.Size previewSize = SupportedPreviewSizes.get(0);// 从List取出Size
        parameters.setPreviewSize(previewSize.width, previewSize.height);// 设置预览照片的大小

        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();// 获取支持保存图片的尺寸
        Camera.Size pictureSize = supportedPictureSizes.get(0);// 从List取出Size
        parameters.setPictureSize(pictureSize.width, pictureSize.height);// 设置照片的大小

        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
        mCamera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上


     /*   mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCamera.autoFocus(autoFocusCallback);
                return false;
            }
        });*/
        mCameraLayout.addView(mPreview);
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
    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(mCameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /*
     *//**
     * 手动聚焦
     *
     * @param point 触屏坐标
     *//*
    protected boolean onFocus(Point point, Camera.AutoFocusCallback callback) {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameters = null;
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //不支持设置自定义聚焦，则使用自动聚焦，返回

        if (Build.VERSION.SDK_INT >= 14) {

            if (parameters.getMaxNumFocusAreas() <= 0) {
                return focus(callback);
            }

            //定点对焦
            List<Camera.Area> areas = new ArrayList<Camera.Area>();
            int left = point.x - 300;
            int top = point.y - 300;
            int right = point.x + 300;
            int bottom = point.y + 300;
            left = left < -1000 ? -1000 : left;
            top = top < -1000 ? -1000 : top;
            right = right > 1000 ? 1000 : right;
            bottom = bottom > 1000 ? 1000 : bottom;
            areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
            parameters.setFocusAreas(areas);
            try {
                //本人使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
                //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
                mCamera.setParameters(parameters);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                return false;
            }
        }


        return focus(callback);
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        try {
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }*/
}
