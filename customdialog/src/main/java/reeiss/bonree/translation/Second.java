package reeiss.bonree.translation;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import reeiss.bonree.customdialog.R;


public class Second extends Activity implements OnClickListener {

    String tag = "";

    RotationHelper rotateHelper;

    RelativeLayout layout2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        layout2 = (RelativeLayout) findViewById(R.id.layout2);
        showView();
        setListener();
    }

    public void showView() {
        /* 取得Intent中的Bundle对象 */
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null) {
            /* 取得Bundle对象中的数据 */
            tag = bundle.getString("front");
        }

        System.out.println("bundle =" + tag);

        if (tag.equals("First")) {
            rotateHelper = new RotationHelper(this, Constants.KEY_SECOND_INVERSE);
            rotateHelper.applyLastRotation(layout2, 90, 0);
        }
    }

    public void setListener() {
        ImageView second_btn = (ImageView) findViewById(R.id.second_btn);
        second_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        rotateHelper = new RotationHelper(this,
            Constants.KEY_SECOND_CLOCKWISE);
        rotateHelper.applyFirstRotation(layout2, 0, 90);
    }

    public void jumpToFirst() {
        Intent in = new Intent();
        in.setClass(this, TranslateLayout.class);
        Bundle bundle = new Bundle();
        bundle.putString("second", "Second");
        in.putExtras(bundle);
        startActivity(in);
        finish();
    }

}