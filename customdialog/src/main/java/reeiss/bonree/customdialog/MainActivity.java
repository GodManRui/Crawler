package reeiss.bonree.customdialog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import reeiss.bonree.customdialog.Rotate3dAnimation;
public class MainActivity extends AppCompatActivity implements OnClickListener {

  //  private AutoDisplayChildViewContainers auto;
    private RelativeLayout layout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

        layout1 = (RelativeLayout) findViewById(R.id.layout1);
        ImageView first_btn = (ImageView) findViewById(R.id.first_btn);
        first_btn.setOnClickListener(this);
       /* auto = findViewById(R.id.vg);
        for (int i = 0; i < 1; i++) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.net_task_infodata_radiobutton, null);
            radioButton.setTag(i);
            radioButton.setText("Button " + (i + 1));
            auto.addView(radioButton);
        }*/

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
        float centerX = layout1.getWidth() / 2.0f;
        float centerY = layout1.getHeight() / 2.0f;
        float depthZ = 0f;
        Rotate3dAnimation rotate3dAnimationX = new Rotate3dAnimation(fromDegrees, toDegrees, centerX, centerY, depthZ, rotateXAxis, true);
        rotate3dAnimationX.setDuration(1000);
        layout1.startAnimation(rotate3dAnimationX);
    }

    // 以Z轴为轴心旋转---等价于普通平面旋转动画
    private void rotateAnimHorizon() {
        rotateOnXCoordinate(180, 0, Rotate3dAnimation.ROTATE_Z_AXIS);

        /*// 下面是使用android自带的旋转动画
         RotateAnimation rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         rotateAnimation.setDuration(1000);
         auto.startAnimation(rotateAnimation);*/
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        start(v);
    }
}
