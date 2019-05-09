package com.example.a26050.statusbaradapterdemo.utils

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.example.a26050.statusbaradapterdemo.measureTest.onMeasureLog

/**
 * 作者: Li_ke
 * 日期: 2019/5/7 11:15
 * 作用:
 */
class MWebView : WebView {

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
		context, attrs, defStyleAttr
	)

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		//exactly 模式 + size 0 会锁定WebView的高度，尝试跳过这次测量会报错：onMeasure() did not set the measured dimension by calling setMeasuredDimension()
		onMeasureLog(widthMeasureSpec, heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		}
		if (contentHeight != 0) {
			//这次测量已经是用contentHeight测量了
			if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY
				&& MeasureSpec.getSize(heightMeasureSpec) == contentHeight
			) return
			measure(
				widthMeasureSpec,
				MeasureSpec.makeMeasureSpec(contentHeight, MeasureSpec.EXACTLY)
			)
		} else {
			//在内容为0时使用的临时方案
			if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
				//尝试再次赋值为未知，发现值会愈来愈大，不缩小
				measure(
					widthMeasureSpec,
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
				)
			}
		}
	}
}