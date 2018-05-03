package reeiss.bonree.crawler.testdialog.gridview;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;

import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

import reeiss.bonree.crawler.R;
import reeiss.bonree.crawler.testdialog.RsRadioGroup;
import reeiss.bonree.crawler.testdialog.SmartPopupItem;


/**
 * Created by GodRui on 2018/3/2.
 */

public class GridDialog extends Dialog {
    private ArrayList<GridView> listRadioGroup;
    private View mView;
    private boolean isAnimation;
    private Click onClick;
    private Animation inAnimation;
    private Animation outAnimation;
    private int[] lastSelect;
    private OnDismissListener onDismissListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            onClick.Confirm();
            setOnDismissListener(null);
        }
    };

    public GridDialog(Context context) {
        this(context, R.style.PopupDialogStyle);
    }

    public GridDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initAnimation();
    }

    private void initAnimation() {
        inAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.in_righttoleft);
        inAnimation.setFillAfter(true);

        inAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = false;
                mView.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        outAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.out_lefttoright);
        outAnimation.setFillAfter(true);
        outAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimation = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimation = false;
                cancel();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void setOnClick(Click onClick) {
        this.onClick = onClick;
    }


    public View getContentView() {
        return mView;
    }

    public void setContentView(List<SmartPopupItem> smartPopupItem) {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.popup_base, null);
        LinearLayout root = (LinearLayout) mView.findViewById(R.id.root);
        listRadioGroup = new ArrayList<>();
        for (int i = 0; i < smartPopupItem.size(); i++) {
            View vv = LayoutInflater.from(getContext()).inflate(R.layout.grid, null, false);
            GridView gv = (GridView) vv.findViewById(R.id.gridview);

            ImageView im = (ImageView) vv.findViewById(R.id.im_gri_title);
            im.setImageDrawable(getContext().getResources().getDrawable(smartPopupItem.get(i).getResID()));

            gv.setTag(i);
            String[] btnNames = smartPopupItem.get(i).getBtnName();
            GridAdapter adapter = new GridAdapter(getContext(), btnNames);
            gv.setAdapter(adapter);
            listRadioGroup.add(gv);
            AutoUtils.autoSize(vv);
            root.addView(vv);
        }
        AutoUtils.autoSize(mView);
        super.setContentView(mView);
        init();
    }

    private void init() {
        lastSelect = new int[listRadioGroup.size()];
        mView.findViewById(R.id.base_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        mView.findViewById(R.id.base_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
                setOnDismissListener(onDismissListener);
            }
        });
    }

    public void close() {
        if (isAnimation) return;
        mView.startAnimation(outAnimation);
    }

    public GridDialog setView(int layoutResId) {
        mView = LayoutInflater.from(getContext()).inflate(layoutResId, null, false);
        AutoUtils.autoSize(mView);
        super.setContentView(mView);
        return this;
    }

    @Override
    public void show() {
        super.show();

        mView.startAnimation(inAnimation);

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.RIGHT);
        //dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isOutOfBounds(getContext(), event)) {
            close();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop) || (x > (decorView.getWidth() + slop))
            || (y > (decorView.getHeight() + slop));
    }

    public interface Click {
        void Confirm();
    }
}
