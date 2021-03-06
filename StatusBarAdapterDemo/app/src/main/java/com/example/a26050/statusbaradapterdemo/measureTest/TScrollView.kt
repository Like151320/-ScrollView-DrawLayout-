package com.example.a26050.statusbaradapterdemo.measureTest

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

/**
 * 作者: Li_ke
 * 日期: 2019/5/7 16:11
 * 作用:
 */
class TScrollView : NestedScrollView {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
		context,
		attrs,
		defStyleAttr
	)

	init {
		initTag()
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		onMeasureLog(widthMeasureSpec, heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec)
		}
	}
}