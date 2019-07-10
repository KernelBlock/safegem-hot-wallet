package com.bankledger.safegem.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Date：2018/9/10
 * Author: bankledger
 */
public class NoScollGridLayoutManager extends GridLayoutManager {
    private boolean isScrollEnabled = false;
    public NoScollGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NoScollGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public NoScollGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
