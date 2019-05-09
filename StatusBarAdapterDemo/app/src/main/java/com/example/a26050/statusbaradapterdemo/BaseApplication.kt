package com.example.a26050.statusbaradapterdemo

import android.app.Application

/**
 * 作者: Li_ke
 * 日期: 2019/5/7 11:54
 * 作用:
 */
class BaseApplication : Application() {

	override fun onCreate() {
		app = this
		super.onCreate()
	}

	companion object {
		lateinit var app: BaseApplication
	}
}