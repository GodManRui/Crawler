package reeiss.bonree.ble_test.smarthardware;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import reeiss.bonree.ble_test.R;
import reeiss.bonree.ble_test.utils.T;

public class SecondFragment extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout mCameraLayout;
    private int mCameraId = CameraInfo.CAMERA_FACING_BACK;
    private String picturePath;
    private ImageView imPicture;
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            releaseCamera();
        } else {
            openCamera();
        }
    }

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
        mCameraLayout = (FrameLayout) getView().findViewById(R.id.camera_preview);
        //Button mTakePictureBtn = (Button) getView().findViewById(R.id.button_capture);
        // Button mChangeCarema = (Button) getView().findViewById(R.id.change);
        getView().findViewById(R.id.button_capture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCamera != null) {
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
                //将图片转换为bitmap格式
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));    //将bitmap转换为uri
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

    @Override
    public void onResume() {
        super.onResume();
//        openCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
//        releaseCamera();
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
    private void openCamera() {
        if (!checkCameraHardware(getActivity())) {
            T.show(getActivity(), "权限被拒绝！");
            return;
        }
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(getActivity(), mCamera);

        Camera.Parameters parameters = mCamera.getParameters();
        //自动对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setPreviewFrameRate(20);
        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

        mPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mCamera.autoFocus(null);
                return false;
            }
        });
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


}
