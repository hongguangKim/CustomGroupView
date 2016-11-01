CustomGroupView
================

  为了练习一下自定义个ViewGroup，做了一个小小的例子.
  通过判断的xml中对于自定义ViewGroup的Layout_width/height来绘制出Layout以及childView。
  之后就是当动态添加/删除一个childView时会有scale的动画。
  
Demo
================
![demo](https://raw.githubusercontent.com/hongguangKim/CustomGroupView/master/DEMO/1.PNG)]![demo](https://raw.githubusercontent.com/hongguangKim/CustomGroupView/master/DEMO/2.PNG)]

Source
================
主要的方法是onMeasure，onLayout，一个是测量布局的宽高，当然在这里需要判断childView的width/height，
在此我包括了margin的值到childView。只限于MaginLayoutParams。
```java
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
```
有关add ChildView。我只限制在最多20个childview包含在此ViewGruop中。
```java
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
```
