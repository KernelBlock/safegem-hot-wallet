package com.bankledger.safegem.ui.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Date：2018/10/8
 * Author: bankledger
 */
public class ChatDetailItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public ChatDetailItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
    }

}
