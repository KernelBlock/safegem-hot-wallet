package com.bankledger.safegem.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Date：2018/10/17
 * Author: bankledger
 */
@SuppressLint("AppCompatCustomView")
public class RecyclerImageView extends ImageView {

    public RecyclerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (Exception e) {
            System.out.println("RectclerImageView  -> onDraw() Canvas: trying to use a recycled bitmap");
        }
    }
}
