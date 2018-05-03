package reeiss.bonree.customdialog;

/**
 * Created by GodRui on 2018/4/26.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

public class AutoDisplayChildViewContainer extends RadioGroup {

    /*    public AutoDisplayChildViewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }*/
    int measure;
    int layout;
    private int parentWidth;
    private int totalLeft = 0;
    private int totalTop = 0;
    private int lineSpace;//= DisplayUtil.dip2px(getContext(), 10)
    private int maxChildHeight = 0;
    private int totalRight = 0;

    public AutoDisplayChildViewContainer(Context context) {
        super(context);
    }

    public AutoDisplayChildViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /* 测量父布局 */
        int reallySize = measureSize(widthMeasureSpec, DisplayUtil.dip2px(getContext(), 240));
        int spaceLeft = reallySize / 12;
        int spaceTop = reallySize / 17;
        setPadding(spaceLeft, spaceTop, spaceLeft, spaceTop);
        parentWidth = reallySize - spaceLeft * 2;
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int count = getChildCount();
        int tempTotalRight = 0;

        //总高度
        int tempMaxChildHeight = 0;
        int tempTotalHeight = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {

                // measure 子布局  确定宽高
                int measureChildHeight = child.getMeasuredHeight();
                int measuredChildWidth = child.getMeasuredWidth();

                lineSpace = (parentWidth - measuredChildWidth * 2);

                if (measuredChildWidth > parentWidth) {
                    child.measure(parentWidth, measureChildHeight);
                } else
                    child.measure(measuredChildWidth, measureChildHeight);

                tempMaxChildHeight = Math.max(tempMaxChildHeight, measureChildHeight);
                tempTotalRight += measuredChildWidth;
                if (tempTotalRight > parentWidth) {
                    /*tempTotalHeight += tempMaxChildHeight;
                    tempMaxChildHeight = child.getMeasuredHeight();*/
                    tempTotalHeight += measureChildHeight + lineSpace;
                    tempTotalRight = measuredChildWidth;
                }
            }
        }

        /* 获取适配子布局后的高度 */
        int parentHeight = tempTotalHeight + tempMaxChildHeight;

        setMeasuredDimension(parentWidth + getPaddingLeft() * 2,
            parentHeight + getPaddingBottom() * 2
        );
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



    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int lineViewCount = 0;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {

                if (i != 0) {
                    /* child 的 left 是上个子 view 的宽加上 lineSpace */
                    totalLeft += getChildAt(i - 1).getMeasuredWidth() + lineSpace;
                } else {
                    totalLeft = 0;
                    totalTop = 0;
                    maxChildHeight = child.getMeasuredHeight();
                }
                /* child 的 right */
                totalRight = totalLeft + child.getMeasuredWidth();
                /* 如果 right 大于 父布局的宽， 则换行 */
                if (totalRight > parentWidth) {
                    adjustLine(lineViewCount, i); // 调整这一行的子布局的位置
                    lineViewCount = 0;  // 这一行的子 child 的数量充值
                    totalTop += maxChildHeight + lineSpace;
                    totalLeft = 0;
                    maxChildHeight = child.getMeasuredHeight();
                    totalRight = child.getMeasuredWidth();
                } else {
                    maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
                }

              /*  child.layout(totalLeft, totalTop, totalRight, totalTop + child.getMeasuredHeight()
                );*/

                /* 统计这一行的子view的数量 */
                lineViewCount++;
            }
        }

        /* 调整最后一行子布局的位置 */
        //  totalLeft = totalRight + lineSpace;
        adjustLine(lineViewCount, count);
    }

    /* 调整一行，让这一行的子布局水平居中 */
    private void adjustLine(int lineChildCount, int childIndex) {
        if (lineChildCount != 1)
            totalLeft = (parentWidth - totalLeft) / 2;
        // int marginTop;
        for (int lvn = lineChildCount; lvn > 0; lvn--) {

            View lineViewChild = getChildAt(childIndex - lvn);
            totalRight = totalLeft + lineViewChild.getMeasuredWidth();
           /* if (lineViewChild.getMeasuredHeight() != maxChildHeight) {
                marginTop = (maxChildHeight - lineViewChild.getMeasuredHeight()) / 2;
            } else {
                marginTop = 0;
            }*/

            lineViewChild.layout(totalLeft + getPaddingLeft(), totalTop + getPaddingTop(),
                totalRight + getPaddingLeft(), totalTop + getPaddingTop() + lineViewChild.getMeasuredHeight());
            totalLeft += lineViewChild.getMeasuredWidth() + lineSpace;
        }
    }

}