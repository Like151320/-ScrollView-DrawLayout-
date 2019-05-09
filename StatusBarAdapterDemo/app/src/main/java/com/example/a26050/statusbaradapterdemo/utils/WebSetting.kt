package com.example.a26050.statusbaradapterdemo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.util.Log
import android.webkit.*

class WebSetting(val webView: WebView, val context: Context) {

	@SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
	fun initWebView() {
		//设置WebView支持Javascript
		val settings = webView.settings//得到webView的配置
		settings.javaScriptEnabled = true
		//开启Dom存储,若网页端使用此功能(localStorage),WebView却没支持,就会出现无法在Android端捕捉的错误,类似：TypeError:Cannot call method 'getItem' of null
		settings.domStorageEnabled = true
		//四个功能:防止自动跳转,可拦截网页,有页面开始加载与加载结束回调
		webView.webViewClient = MyWebViewClient()
		webView.webChromeClient = MyWebChromeClient()//可以获取网站的title、logo、具体加载进度
		//下载时若app在前台就提示用工具下载
		webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
			if (!ActivityUtils.isBackground(context)) {
				val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
				context.startActivity(intent)
			}
		}
		settings.setSupportZoom(true)//支持缩放
		settings.databaseEnabled = true
		settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
		settings.cacheMode = WebSettings.LOAD_DEFAULT
		settings.useWideViewPort = true//设置此属性，可任意比例缩放
		settings.loadWithOverviewMode = true
		settings.mediaPlaybackRequiresUserGesture = true
		settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
		settings.defaultTextEncodingName = "utf-8"//编码方式为UTF-8
		settings.allowFileAccess = true//设置加载本地资
		//将一个类的对象传给webView的window容器供内部的html/js调用 //参1:传递的对象;参2:别称
//		webView.addJavascriptInterface(WebViewController(), "FrameViewController")
	}

	/**供JS交互*/
	class WebViewController(val activity: Activity) {
		/**关闭页面*/
		@JavascriptInterface
		fun finishFrameView() {
			activity.finish()
		}
	}

	internal class MyWebChromeClient : WebChromeClient() {
		//进度
		override fun onProgressChanged(view: WebView?, newProgress: Int) {
			super.onProgressChanged(view, newProgress)
		}

		//Logo
		override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
			super.onReceivedIcon(view, icon)
		}

		//title
		override fun onReceivedTitle(view: WebView?, title: String?) {
			super.onReceivedTitle(view, title)
		}
	}

	/**
	 * 功能: * 1、可以防止URL通过手机自带的浏览器进行加载(自动跳转) * 2、可以根据逻辑拦截网址 * 3、知道页面开始加载 * 4、知道页面加载完成
	 */
	open class MyWebViewClient : WebViewClient() {
		/**当加载内部跳转URL时，立刻调用次方法
		 * 此时可以拦截要加载的URL
		 * 如果想要拦截,return true、 返回false 则不拦截
		 * 注意: webView调用loadUrl不会触发此方法
		 */
		override fun shouldOverrideUrlLoading(
			view: WebView?, request: WebResourceRequest?
		): Boolean {
			return super.shouldOverrideUrlLoading(view, request)
		}

		/**
		 * 当页面开始加载
		 *
		 * 网页内部跳链接时低版本不走 shouldOverrideUrlLoading 但一定会走 onPageStarted。所以建议统一在
		 * onPageStarted 内监听链接跳转
		 * @param favicon 返回的图标(几乎为Null)
		 */
		override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
			super.onPageStarted(view, url, favicon)
		}

		/**页面加载完成回调*/
		override fun onPageFinished(view: WebView?, url: String?) {
			super.onPageFinished(view, url)
		}

		override fun onReceivedError(
			view: WebView?, request: WebResourceRequest?, error: WebResourceError?
		) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				Log.e("Li_ke", "WebView.onReceivedError: ${error?.description}")
			}
			super.onReceivedError(view, request, error)
		}

		override fun onReceivedHttpError(
			view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?
		) {
			super.onReceivedHttpError(view, request, errorResponse)
		}

		override fun onReceivedSslError(
			view: WebView?, handler: SslErrorHandler?, error: SslError?
		) {
			Log.e("Li_ke", "WebView.onReceivedSslError: ${error?.url}")
			super.onReceivedSslError(view, handler, error)
		}
	}
}