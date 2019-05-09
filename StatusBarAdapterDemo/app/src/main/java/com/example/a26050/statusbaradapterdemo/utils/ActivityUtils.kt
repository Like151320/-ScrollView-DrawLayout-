package com.example.a26050.statusbaradapterdemo.utils

import android.app.ActivityManager
import android.content.Context

/**
 * 作者: Li_ke
 * 日期: 2019/4/10 10:26
 * 作用:
 */

/**context是否在后台*/
object ActivityUtils {
	fun isBackground(context: Context): Boolean {
		val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val appProcesses = activityManager.runningAppProcesses
		if (appProcesses != null) {
			for (appProcess in appProcesses) {
				if (appProcess.processName == context.packageName) {
					return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND
				}
			}
		}
		return false
	}
}