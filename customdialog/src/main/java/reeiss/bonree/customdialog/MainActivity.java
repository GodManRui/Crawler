package reeiss.bonree.customdialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.RadioButton;
import reeiss.bonree.customdialog.Rotate3dAnimation;
public class MainActivity extends AppCompatActivity {

    private AutoDisplayChildViewContainers auto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auto = findViewById(R.id.vg);
        for (int i = 0; i < 1; i++) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.net_task_infodata_radiobutton, null);
            radioButton.setTag(i);
            radioButton.setText("Button " + (i + 1));
            auto.addView(radioButton);
        }

    }

    public void start(View view) {
        //rotateOnXCoordinate(0, 180, Rotate3dAnimation.ROTATE_X_AXIS);
        rotateOnYCoordinate();

           /* RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.net_task_infodata_radiobutton, null);

            radioButton.setText("Button "  );
            auto.addView(radioButton);*/
    }

    // 以X轴为轴心旋转
    private void rotateOnYCoordinate() {
        rotateOnXCoordinate(0, 180, Rotate3dAnimation.ROTATE_Y_AXIS);
    }

    private void rotateOnXCoordinate(int fromDegrees, int toDegrees, Byte rotateXAxis) {
        float centerX = auto.getWidth() / 2.0f;
        float centerY = auto.getHeight() / 2.0f;
        float depthZ = 0f;
        Rotate3dAnimation rotate3dAnimationX = new Rotate3dAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, rotateXAxis, true);
        rotate3dAnimationX.setDuration(1000);
        auto.startAnimation(rotate3dAnimationX);
    }

    // 以Z轴为轴心旋转---等价于普通平面旋转动画
    private void rotateAnimHorizon() {
        rotateOnXCoordinate(180, 0, Rotate3dAnimation.ROTATE_Z_AXIS);

        /*// 下面是使用android自带的旋转动画
         RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         rotateAnimation.setDuration(1000);
         auto.startAnimation(rotateAnimation);*/
    }
}
