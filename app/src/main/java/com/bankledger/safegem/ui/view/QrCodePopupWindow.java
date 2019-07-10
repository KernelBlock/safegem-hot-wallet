package com.bankledger.safegem.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankledger.safegem.R;
import com.bankledger.safegem.qrcode.QRCodeUtil;
import com.bankledger.safegem.ui.activity.BaseActivity;
import com.bankledger.safegem.utils.QRCodeEncoderUtils;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;

/**
 * Date：2018/8/17
 * Author: bankledger
 */
public class QrCodePopupWindow {
    private View upView;
    private CommonPopupWindow popupWindow;
    private Context mContext;
    private ViewPager mVpView;
    private TextView mTvPage;
    private MyViewPageAdapter adapter;
    private List<String> mList;
    private Bitmap bm;
    private List<RecyclerImageView> imgList = new ArrayList<>();
    int i = 0;

    public QrCodePopupWindow(View parent, Context context, String content) {
        mContext = context;
        upView = LayoutInflater.from(context).inflate(R.layout.code_popup_layout, null, false);
        popupWindow = new CommonPopupWindow.Builder(context)
                .setView(upView)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setOutsideTouchable(false)
                .setBackGroundLevel(0.5f)
                .create();

        upView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

            }
        });
        upView.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
//                mVpView.setCurrentItem(mVpView.getCurrentItem() + 1);
            }
        });
        mVpView = upView.findViewById(R.id.viewPager);
        mTvPage = upView.findViewById(R.id.tv_page);
        adapter = new MyViewPageAdapter();
        mList = QRCodeUtil.encodePage(content);
        mVpView.setAdapter(adapter);
        setTvPage(mVpView.getCurrentItem());
        mVpView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTvPage(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setTvPage(int position) {
        mTvPage.setText(++position + "/" + mList.size());
    }

    public void show(BaseActivity activity) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    public void show(BaseActivity activity, String content) {
        if (popupWindow != null && popupWindow.isShowing()) return;
        popupWindow.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    List<Bitmap> bitmapList = new ArrayList<>();

    class MyViewPageAdapter extends PagerAdapter {
        private int gcNumber = 0;

        @Override
        public int getCount() {
            return mList.size();
        }

        //判断是否是否为同一张图片，这里返回方法中的两个参数做比较就可以
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //设置viewpage内部东西的方法，如果viewpage内没有子空间滑动产生不了动画效果
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.qrcode_img_layout, null);
            RecyclerImageView imageView = view.findViewById(R.id.code_img);
//            imgList.add(imageView);
            try {
                bm = QRCodeEncoderUtils.encodeAsBitmap(mContext, mList.get(position));
//                bitmapList.add(bm);
                imageView.setImageBitmap(bm);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            (container).removeView(view);
            RecyclerImageView imageView = view.findViewById(R.id.code_img);
            releaseImageViewResouce(imageView);
            imageView = null;
        }

    }

    /**
     * 释放图片资源的方法
     *
     * @param imageView
     */
    public void releaseImageViewResouce(ImageView imageView) {
        if (imageView == null) return;
        Drawable drawable = imageView.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
//        System.gc();
    }


}
