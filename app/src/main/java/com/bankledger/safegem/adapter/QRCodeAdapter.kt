package com.bankledger.safegem.adapter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bankledger.safegem.R
import com.bankledger.safegem.ui.view.RecyclerImageView
import com.bankledger.safegem.utils.QRCodeEncoderUtils

import java.util.*

class QRCodeAdapter(var mList:List<String>,var mContext:Context): PagerAdapter() {

    override fun isViewFromObject(view: View?, `object`: Any?)= view == `object`

    override fun getCount()=mList.size

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        var view=LayoutInflater.from(mContext).inflate(R.layout.qrcode_img_layout,null)
        var imageView=view.findViewById<RecyclerImageView>(R.id.code_img)
        imageView.setImageBitmap(QRCodeEncoderUtils.encodeAsBitmap(mContext, mList.get(position)))
        container?.addView(view)
        var map = TreeMap<String,String>()
        return view
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
        var view=`object` as View
        container?.removeView(view)
        var imageView=view.findViewById<RecyclerImageView>(R.id.code_img)
        releaseImageViewResouce(imageView)
        imageView = null
    }

    /**
     * 释放图片资源的方法
     */
    fun releaseImageViewResouce(imageView:ImageView){
        if (imageView == null) return
        val drawable=imageView.drawable
        var bitmapDrawable:BitmapDrawable?=drawable as? BitmapDrawable
        var bitmap=bitmapDrawable?.bitmap
        if (!bitmap?.isRecycled!!){
            bitmap.recycle()
            bitmap=null
        }
    }

}