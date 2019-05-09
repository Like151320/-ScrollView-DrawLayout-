package com.example.a26050.statusbaradapterdemo

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.utils.WebSetting
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_web_view)

		val webSetting = WebSetting(webView, this)
		webSetting.initWebView()
//		webView.webViewClient = MClient()

		webView.loadUrl("https://www.baidu.com")
//		webView.loadUrl("https://www.csdn.net/")
	}

	override fun onBackPressed() {
		if (webView.canGoBack())
			webView.goBack()
		else
			super.onBackPressed()
	}

	class MClient : WebSetting.MyWebViewClient() {

		var contentH = 0

		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		override fun shouldOverrideUrlLoading(
			view: WebView?, request: WebResourceRequest?
		): Boolean {
			Log.e("Li_ke", "shouldOverrideUrlLoading(${request?.url})")

			return super.shouldOverrideUrlLoading(view, request)
		}

		override fun onPageFinished(view: WebView?, url: String?) {
			super.onPageFinished(view, url)
			view!!

			//得到内容高度时直接使用内容高度
			val setH: Int
//			if (contentH != 0) {
//				setH = ScreenUtils.dp2px(contentH.toFloat())
//			} else {
//			}

				//
				view.layoutParams = view.layoutParams.also {
					it.height = 1
				}
				//未得到内容高度时使用测量高度
				view.measure(0, 0)
				setH = view.measuredHeight
				Log.e("Li_ke", "hf = $setH")

			Log.e("Li_ke", "onPageFinished setH = $setH")
			view.layoutParams = view.layoutParams.also {
				it.height = setH
			}
		}

		override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
			super.onPageStarted(view, url, favicon)
			contentH = view!!.contentHeight
			Log.e("Li_ke", "hs = $contentH")
		}
	}
}
