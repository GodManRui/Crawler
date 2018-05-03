package reeiss.bonree.crawler.tester;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import reeiss.bonree.crawler.R;
import reeiss.bonree.crawler.utils.ZoomImageView;

/**
 * Created by GodRui on 2018/3/20.
 */

public class BigImageActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setFlags(flag, flag);

        setContentView(R.layout.bigimage);
        String image = getIntent().getStringExtra("image");
        Picasso.get().load(image).into((ZoomImageView) findViewById(R.id.im));

    }
}
