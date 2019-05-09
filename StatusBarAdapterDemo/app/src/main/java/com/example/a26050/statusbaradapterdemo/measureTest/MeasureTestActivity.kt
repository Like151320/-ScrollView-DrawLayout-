package com.example.a26050.statusbaradapterdemo.measureTest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.R
import com.example.a26050.statusbaradapterdemo.utils.MLog
import com.example.a26050.statusbaradapterdemo.utils.WebSetting

class MeasureTestActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_measure_test6)

		webViewTest()
	}

	fun webViewTest() {
		val tWebView = findViewById<TWebView>(R.id.webView)

//		tWebView.loadUrl("")

		//延时加载网址，让日志清晰
		tWebView.postDelayed({

			MLog.e("开始初始化WebView")
			WebSetting(tWebView, this).initWebView()
			MLog.e("开始指定网址")
			tWebView.loadUrl("https://www.baidu.com")
			MLog.e("指定网址完毕")

		}, 1000L)
	}
}
