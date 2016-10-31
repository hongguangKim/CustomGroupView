package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

/**
 * Created by hongguang.jin on 2016/10/31.
 */
public class MyGroupView extends ViewGroup implements View.OnClickListener {
    private String TAG = "MyGroupView";
    private LayoutParams layoutParams;
    private int childCount = 0;
    private int MAX_ITEM_NUMBER = 16;

    public class LayoutParams extends ViewGroup.MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
    }

    public MyGroupView(Context context) {
        super(context);
    }

    public MyGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGroupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public MyGroupView.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MyGroupView.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof MyGroupView.LayoutParams;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 记录总高度
        int mTotalHeight = 0;
        int mTotalWidth = 0;
        // 当前行宽度
        int currentWidth = 0;
        // 遍历所有子视图
        childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 获取在onMeasure中计算的视图尺寸
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            MyGroupView.LayoutParams lp = (MyGroupView.LayoutParams) childView.getLayoutParams();
            layoutParams = lp;
            int measureHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            int measuredWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 获取实际宽高
            if (i == 0) {
                mTotalHeight += measureHeight;
            }
            currentWidth += measuredWidth;
            if (currentWidth > widthSize) {
                mTotalWidth = currentWidth - measuredWidth;
                currentWidth = measuredWidth;
                mTotalHeight += measureHeight;
            }
            Log.i(TAG, "currentWidth=" + currentWidth + "mTotalHeight=" + mTotalHeight);
        }
        mTotalWidth = Math.max(currentWidth, mTotalWidth);
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : mTotalWidth, heightMode == MeasureSpec.EXACTLY ? heightSize : mTotalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parenetWidth = getMeasuredWidth();
        // 记录总高度
        int mTotalHeight = 0;
        int mTotalWidth = 0;
        // 遍历所有子视图
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 获取在onMeasure中计算的视图尺寸
            int measureHeight = childView.getMeasuredHeight();
            int measuredWidth = childView.getMeasuredWidth();

            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            if (measuredWidth + mTotalWidth > parenetWidth) {
                mTotalWidth = 0;
                mTotalHeight += measureHeight + +lp.topMargin + lp.bottomMargin;
            }

            childView.layout(mTotalWidth + lp.leftMargin, mTotalHeight + lp.topMargin, measuredWidth + mTotalWidth + lp.rightMargin, mTotalHeight + measureHeight + lp.bottomMargin);
            mTotalWidth += measuredWidth + lp.leftMargin + lp.rightMargin;

            if (childCount < MAX_ITEM_NUMBER)
                childView.setOnClickListener(this);
            else
                childView.setOnClickListener(null);
        }
    }

    private void addItem() {
        if (childCount < MAX_ITEM_NUMBER) {
            TextView newChild = new TextView(getContext());
            newChild.setLayoutParams(layoutParams);
            newChild.setText("Frame" + (childCount + 1));
            newChild.setTextSize(18);
            newChild.setGravity(Gravity.CENTER);
            newChild.setBackgroundResource(R.color.colorAccent);
            ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setInterpolator(new BounceInterpolator());
            animation.setDuration(300);
            newChild.setAnimation(animation);
            animation.start();
            addView(newChild);
            requestLayout();
            invalidate();
        }
    }

    private void removeItem(final View v) {
        ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                removeView(v);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        v.setAnimation(animation);
        animation.start();
        requestLayout();
        invalidate();
    }

    @Override
    public void onClick(View v) {
        addPhotoBtnClick(v);
    }

    public void addPhotoBtnClick(final View v) {
        final CharSequence[] items = {"addItem", "removeItem"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int id) {
                Log.i(TAG, "AlertDialog = " + id);
                if (id == 0)
                    addItem();
                else if (id == 1)
                    removeItem(v);
            }
        });
        builder.show();
    }

}
