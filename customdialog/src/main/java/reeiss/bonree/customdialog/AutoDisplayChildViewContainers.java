package reeiss.bonree.customdialog;

/**
 * Created by GodRui on 2018/4/26.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

public class AutoDisplayChildViewContainers extends RadioGroup {
    private int parentWidth;
    private int lineSpace;
    private int aLineViewCount;

    public AutoDisplayChildViewContainers(Context context) {
        super(context);
    }

    public AutoDisplayChildViewContainers(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AutoDisplayChildViewContainers);
        aLineViewCount = ta.getInteger(R.styleable.AutoDisplayChildViewContainers_numColumns, 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /* 测量父布局 */
        int reallyWidth = MeasureSpec.getSize(widthMeasureSpec);
        int paddingLeft = reallyWidth / 12;
        int paddingTop = reallyWidth / 17;
        lineSpace = reallyWidth / 15;

        setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);

        parentWidth = reallyWidth - paddingLeft * 2;

        int count = getChildCount();
        if (count == 0) return;

        double lineNum = count == 1 ? 1 : Math.ceil((float) count / aLineViewCount);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxHeight = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int measuredHeight = child.getMeasuredHeight();

            if (count == 1) {
                int width = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY);
                int height = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
                child.measure(width, height);
            } else {
                int width = (parentWidth - (aLineViewCount - 1) * lineSpace) / aLineViewCount;
                int widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                int heightSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);
                child.measure(widthSpec, heightSpec);
            }
            maxHeight = Math.max(maxHeight, measuredHeight);
        }
        int spaceNum = (int) lineNum;
        setMeasuredDimension(reallyWidth, maxHeight * spaceNum + spaceNum * lineSpace + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int top = getPaddingTop();
        int isNextLine = 1;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int measuredWidth = child.getMeasuredWidth();
                int measuredHeight = child.getMeasuredHeight();
                if (isNextLine < aLineViewCount) {
                    int afterViewCount = i % aLineViewCount;
                    int left = getPaddingLeft() + afterViewCount * measuredWidth + afterViewCount * lineSpace;
                    int right = left + measuredWidth;
                    child.layout(left, top, right, top + measuredHeight);
                    isNextLine++;
                } else {
                    child.layout(getPaddingLeft() + (parentWidth - measuredWidth), top, getPaddingLeft() + parentWidth, top + measuredHeight);
                    top += lineSpace + measuredHeight;
                    isNextLine = 1;
                }
            }
        }
    }

    private int measureSize(int measureSpec, int defaultSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int result = defaultSize;

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else if (mode == MeasureSpec.AT_MOST) {
            result = Math.max(size, result);
        }
        return result;
    }
}