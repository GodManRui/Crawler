package reeiss.bonree.customdialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//3D反转 解决 反转后界面颠倒
public class MainActivity extends AppCompatActivity implements OnClickListener {

    //  private AutoDisplayChildViewContainers auto;
    private RelativeLayout layout1;
    private ImageView first_btn;
    private ImageView second_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

        layout1 = (RelativeLayout) findViewById(R.id.layout1);
        first_btn = (ImageView) findViewById(R.id.first_btn);
        second_btn = (ImageView) findViewById(R.id.second_btn);
        first_btn.setOnClickListener(this);
        second_btn.setOnClickListener(this);
       /* auto = findViewById(R.id.vg);
        for (int i = 0; i < 1; i++) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.net_task_infodata_radiobutton, null);
            radioButton.setTag(i);
            radioButton.setText("Button " + (i + 1));
            auto.addView(radioButton);
        }*/

    }


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        //start(v);
        switch (v.getId()) {
            case R.id.first_btn:
                applyRotation(1, 0, 90);
                break;
            case R.id.second_btn:
                //applyRotation(-1, 180, 90);
                applyRotation(-1, 360, 270);
                break;
        }
    }

    private void applyRotation(final int mPosition, float start, float end) {
        // Find the center of the container
        final float centerX = first_btn.getWidth() / 2.0f;
        final float centerY = first_btn.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final reeiss.bonree.translation.Rotate3dAnimation rotation =
            new reeiss.bonree.translation.Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Log.e("JerryZhu", "onAnimationStart: ");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("JerryZhu", "onAnimationStart: ");
                first_btn.post(new Runnable() {
                    @Override
                    public void run() {
                        final float centerX = first_btn.getWidth() / 2.0f;
                        final float centerY = first_btn.getHeight() / 2.0f;
                        reeiss.bonree.translation.Rotate3dAnimation rotation;

                        if (mPosition > -1) {
                            //显示图片 隐藏列表
                            first_btn.setVisibility(View.GONE);
                            second_btn.setVisibility(View.VISIBLE);
                            second_btn.requestFocus();

                            //rotation = new reeiss.bonree.translation.Rotate3dAnimation(90, 180, centerX, centerY, 310.0f, false);
                            rotation = new reeiss.bonree.translation.Rotate3dAnimation(270, 360, centerX, centerY, 310.0f, false);

                        } else {
                            //隐藏图片 显示列表  ==-1
                            second_btn.setVisibility(View.GONE);
                            first_btn.setVisibility(View.VISIBLE);
                            first_btn.requestFocus();

                            rotation = new reeiss.bonree.translation.Rotate3dAnimation(90, 0, centerX, centerY, 310.0f, false);
                        }

                        rotation.setDuration(500);
                        rotation.setFillAfter(true);
                        rotation.setInterpolator(new DecelerateInterpolator());

                        layout1.startAnimation(rotation);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.e("JerryZhu", "onAnimationRepeat: ");
            }
        });

        layout1.startAnimation(rotation);
    }


}
