package com.example.a26050.statusbaradapterdemo.utils

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.Window
import com.example.a26050.statusbaradapterdemo.BaseApplication

/**
 * 作者: Li_ke
 * 日期: 2018/11/16 0016 16:09
 * 作用:
 */
object ScreenUtils {
	private val displayMetrics by lazy { BaseApplication.app.resources.displayMetrics }

	/** 使最外层布局撑满全屏(有过度动画) [isFullscreen] 撑满 or 还原 */
	fun smoothFullscreen(view: View, isFullscreen: Boolean) {
		if (isFullscreen) {
			view.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LOW_PROFILE or
						View.SYSTEM_UI_FLAG_FULLSCREEN or
						View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
						View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
						View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		} else {
			view.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
						View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
		}
	}

	/**dp转px*/
	fun dp2px(dp: Float): Int =
		TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, dp,
			displayMetrics
		).toInt()

	/**px转dp*/
	fun px2dp(px: Int): Float = px / displayMetrics.density
	//return TypedValue.complexToDimensionPixelOffset(px, displayMetrics)无用

	fun sp2px(sp: Float): Int =
		TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, sp,
			displayMetrics
		).toInt()

	fun px2sp(px: Int): Float = px / displayMetrics.scaledDensity

	/** 获取窗口宽高
	 *
	 * 受状态栏影响、受虚拟按钮(导航栏)影响、受软键盘影响。 */
	fun getWindowSize(window: Window): Rect {
		val outRect = Rect()
		window.decorView.getWindowVisibleDisplayFrame(outRect)
		return outRect
	}

	/** 屏幕宽高(最大) */
	fun getScreenSize(): Rect {
		return Rect(
			displayMetrics.xdpi.toInt(),
			displayMetrics.ydpi.toInt(),
			displayMetrics.xdpi.toInt() + displayMetrics.widthPixels,
			displayMetrics.ydpi.toInt() + displayMetrics.heightPixels
		)
	}
}

fun View.getScreenRect(): Rect {
	val xyArray = IntArray(2)
	this.getLocationOnScreen(xyArray)
	return Rect(
		xyArray[0],
		xyArray[1],
		xyArray[0] + this.width,
		xyArray[1] + this.height
	)
}