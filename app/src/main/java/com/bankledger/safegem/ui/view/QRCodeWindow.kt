package com.bankledger.safegem.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.bankledger.safegem.R
import com.bankledger.safegem.adapter.QRCodeAdapter
import com.bankledger.safegem.qrcode.QRCodeUtil
import com.bankledger.safegem.ui.activity.BaseActivity
import com.bankledger.safegem.ui.view.CommonPopupWindow.Builder
import com.bankledger.safegem.utils.Constants
import java.util.*

class QRCodeWindow {

    private val upView: View
    private val popupWindow: CommonPopupWindow?
    private val mContext: Context
    private val mVpView: ViewPager
    private val mTvPage: TextView
    private val adapter: QRCodeAdapter
    private val mList: List<String>

    constructor(context: Context, content: String) {
        mContext = context
        upView = LayoutInflater.from(mContext).inflate(R.layout.code_popup_layout, null, false)
        popupWindow = Builder(mContext)
                .setView(upView)
                .setWidthAndHeight(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                .setOutsideTouchable(false)
                .setBackGroundLevel(0.5f)
                .create()
        upView.findViewById<Button>(R.id.close_btn).setOnClickListener { popupWindow.dismiss() }
        upView.findViewById<Button>(R.id.close).setOnClickListener { popupWindow.dismiss() }
        mVpView = upView.findViewById(R.id.viewPager)
        mVpView.offscreenPageLimit = 5
        mTvPage = upView.findViewById(R.id.tv_page)
        mList = QRCodeUtil.encodePage(content)
        adapter = QRCodeAdapter(mList, mContext)

        mVpView.adapter = adapter
        setTvPage(mVpView.currentItem)
        mVpView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                setTvPage(position)
            }

        })
    }

    fun show(activity: BaseActivity) {
        if (popupWindow != null && popupWindow.isShowing) return
        popupWindow?.showAtLocation(activity.findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
    }

    fun setTvPage(position: Int) {
        var position = position
        mTvPage.text = (++position).toString() + "/" + mList.size
    }

    fun getPopupWindow(): CommonPopupWindow? {
        return popupWindow;
    }

}