package reeiss.bonree.customdialog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * Created by GodRui on 2018/4/23.
 */

public class MyViewGroup extends RadioGroup {

    private int width;
    private int height;
    private DisplayMetrics displayMetrics;
    private int metricsWidth;
    private int metricsHeight;
    private int lineCount;

    public MyViewGroup(Context context) {
        this(context, null);
    }


    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        for (int i = 0; i < 11; i++) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.net_task_infodata_radiobutton, null);
            radioButton.setTag(i);
            addView(radioButton);
        }
        displayMetrics = getResources().getDisplayMetrics();
        metricsWidth = displayMetrics.widthPixels;
        metricsHeight = displayMetrics.heightPixels;

        int childCount = getChildCount();
        boolean odd = childCount % 2 == 0;
        lineCount = odd ? childCount / 2 : childCount / 2 + 1;
    }

    //UN  0<<30  EX 1<< 30  AT 2<<30
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //setMeasuredDimension(width / 5, width / 15);
        int defaultWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int defaultHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int measureWidth;
        int measureHeight;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode != MeasureSpec.EXACTLY) {
            //wrap 根据背景大小 判断默认宽度
            int suggestedMinimumHeight = getSuggestedMinimumHeight();
            if (suggestedMinimumHeight == 0) {
                //this
                measureHeight = metricsHeight;//没有背景，设置为屏幕宽高的较小值
            } else {
                //有背景，就取屏幕宽高和背景宽高的较小值比较
                measureHeight = Math.min(suggestedMinimumHeight, metricsHeight);
            }
        } else {
            //exactly 精确
            measureHeight = Math.min(size, metricsHeight);
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY);
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(defaultWidth,
            dp2px(lineCount * 50));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        width = getWidth();
        height = getHeight();
        int space = width / 13;
        setPadding(space, 0, space, 0);
        setGravity(Gravity.CENTER);

        int childIndex = 0;
        int top = 0;

        for (int i = 0; i < lineCount; i++) {
            for (int j = 0; j < 2; j++) {
                if (childIndex > getChildCount() - 1) return;
                View childAt = getChildAt(childIndex++);
                // childAt.layout(j * dpToPx(90), top + 30, (j + 1) * dpToPx(90), top + dpToPx(30) + 30);
                childAt.layout(j == 0 ? 0 : width / 2, top + dpToPx(10), j == 0 ? width / 2 : width, top + dpToPx(50));
            }
            top += dpToPx(48);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            getContext().getResources().getDisplayMetrics());
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

}
