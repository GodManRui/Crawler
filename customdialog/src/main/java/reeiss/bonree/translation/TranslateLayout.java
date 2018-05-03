package reeiss.bonree.translation;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.View;
import android.view.View.OnClickListener;

import reeiss.bonree.customdialog.R;


public class TranslateLayout extends Activity implements OnClickListener {

    RelativeLayout layout1;

    String tag;
    RotationHelper rotateHelper;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
        ImageView first_btn = (ImageView) findViewById(R.id.first_btn);
        first_btn.setOnClickListener(this);

        layout1 = (RelativeLayout) findViewById(R.id.layout1);
        showView();

    }

    public void showView() {
        /* 取得Intent中的Bundle对象 */
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            /* 取得Bundle对象中的数据 */
            tag = bundle.getString("second");
            System.out.println("tag =" + tag);
            if (tag != null && tag.equals("Second")) {
                rotateHelper = new RotationHelper(this,
                    Constants.KEY_FIRST_CLOCKWISE);
                rotateHelper.applyLastRotation(layout1, -90, 0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        rotateHelper = new RotationHelper(this, Constants.KEY_FIRST_INVERSE);
        rotateHelper.applyFirstRotation(layout1, 0, -90);
    }

    public void jumpToSecond() {
        Intent in = new Intent();
        in.setClass(this, Second.class);
        // new一个Bundle对象，并将要传递的数据传入
        Bundle bundle = new Bundle();
        bundle.putString("front", "First");
        /* 将Bundle对象assign给Intent */
        in.putExtras(bundle);
        // 如果已经打开过的实例，将不会重新打开新的Activity
        // in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(in);
        finish();
    }

}
