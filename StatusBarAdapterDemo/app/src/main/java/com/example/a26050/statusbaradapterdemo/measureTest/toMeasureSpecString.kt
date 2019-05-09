package com.example.a26050.statusbaradapterdemo.measureTest

import android.util.SparseIntArray
import android.view.View
import com.example.a26050.statusbaradapterdemo.utils.MLog

var logTag = SparseIntArray()
fun View.initTag() {
	val key = this.javaClass.simpleName.hashCode()
	val value = logTag[key]

	this.tag = value
	logTag.put(key, value + 1)
}

var space = 0
fun <T : View> T.onMeasureLog(
	widthMeasureSpec: Int, heightMeasureSpec: Int, onMeasureBlock: () -> Unit
) {
	var spaceStr = ""
	for (i in 0 until space) {
		spaceStr += "    "
	}

	MLog.e(
		spaceStr
				+ "${this::class.simpleName}$tag 之前 "
				+ widthMeasureSpec.toMeasureSpecString()
				+ " , "
				+ heightMeasureSpec.toMeasureSpecString()
				+ " | 之前 "
				+ "$measuredWidth , $measuredHeight"
//		, StackTraceUtils.showNow(1, 2)
	)
	++space
	onMeasureBlock()
	--space
	MLog.e(
		spaceStr
				+ "${this::class.simpleName}$tag 之后 "
				+ widthMeasureSpec.toMeasureSpecString()
				+ " , "
				+ heightMeasureSpec.toMeasureSpecString()
				+ " | 之后 "
				+ "$measuredWidth , $measuredHeight"
//		, StackTraceUtils.showNow(1, 2)
	)
}

fun Int.toMeasureSpecString(): String {

	val mode = View.MeasureSpec.getMode(this)
	val modeStr = when (mode) {
		View.MeasureSpec.AT_MOST -> "AT_MOST"
		View.MeasureSpec.UNSPECIFIED -> "UNSPECIFIED"
		View.MeasureSpec.EXACTLY -> "EXACTLY"
		else -> "mode=$mode"
	}

	val size = View.MeasureSpec.getSize(this)

	return "$modeStr $size"
}
/*

—————————— 基础
布局模式：
LinearLayout0(match,match,垂直){
	FrameLayout0(wrap,wrap){
		TextView0(wrap,wrap)
	}
	TextView1(match,wrap)
	FrameLayout1(100dp,100dp)
	FrameLayout2(){
		TextView2(800dp,wrap)
	}
}

测量日志：
E: TestLinearLayout0 之前 EXACTLY 1080 , EXACTLY 1851 | 之前 0 , 0
E:     TestFrameLayout0 之前 AT_MOST 1080 , AT_MOST 1851 | 之前 0 , 0
E:         TestTextView0 之前 AT_MOST 1080 , AT_MOST 1851 | 之前 0 , 0
E:         TestTextView0 之后 AT_MOST 1080 , AT_MOST 1851 | 之后 168 , 57
E:     TestFrameLayout0 之后 AT_MOST 1080 , AT_MOST 1851 | 之后 168 , 57
E:     TestTextView1 之前 EXACTLY 1080 , AT_MOST 1794 | 之前 0 , 0
E:     TestTextView1 之后 EXACTLY 1080 , AT_MOST 1794 | 之后 1080 , 57
E:     TestFrameLayout1 之前 EXACTLY 300 , EXACTLY 300 | 之前 0 , 0
E:     TestFrameLayout1 之后 EXACTLY 300 , EXACTLY 300 | 之后 300 , 300
E:     TestFrameLayout2 之前 AT_MOST 1080 , AT_MOST 1437 | 之前 0 , 0
E:         TestTextView2 之前 EXACTLY 2400 , AT_MOST 1437 | 之前 0 , 0
E:         TestTextView2 之后 EXACTLY 2400 , AT_MOST 1437 | 之后 2400 , 57
E:     TestFrameLayout2 之后 AT_MOST 1080 , AT_MOST 1437 | 之后 1080 , 57
E: TestLinearLayout0 之后 EXACTLY 1080 , EXACTLY 1851 | 之后 1080 , 1851

E: TestLinearLayout0 之前 EXACTLY 1080 , EXACTLY 1851 | 之前 1080 , 1851
E:     TestFrameLayout0 之前 AT_MOST 1080 , AT_MOST 1851 | 之前 168 , 57
E:         TestTextView0 之前 AT_MOST 1080 , AT_MOST 1851 | 之前 168 , 57
E:         TestTextView0 之后 AT_MOST 1080 , AT_MOST 1851 | 之后 168 , 57
E:     TestFrameLayout0 之后 AT_MOST 1080 , AT_MOST 1851 | 之后 168 , 57
E:     TestTextView1 之前 EXACTLY 1080 , AT_MOST 1794 | 之前 1080 , 57
E:     TestTextView1 之后 EXACTLY 1080 , AT_MOST 1794 | 之后 1080 , 57
E:     TestFrameLayout1 之前 EXACTLY 300 , EXACTLY 300 | 之前 300 , 300
E:     TestFrameLayout1 之后 EXACTLY 300 , EXACTLY 300 | 之后 300 , 300
E:     TestFrameLayout2 之前 AT_MOST 1080 , AT_MOST 1437 | 之前 1080 , 57
E:         TestTextView2 之前 EXACTLY 2400 , AT_MOST 1437 | 之前 2400 , 57
E:         TestTextView2 之后 EXACTLY 2400 , AT_MOST 1437 | 之后 2400 , 57
E:     TestFrameLayout2 之后 AT_MOST 1080 , AT_MOST 1437 | 之后 1080 , 57
E: TestLinearLayout0 之后 EXACTLY 1080 , EXACTLY 1851 | 之后 1080 , 1851

分析日志：
1、onMeasure的调用处有 onLayout 中与 measure 中。所有的 onMeasure 都是被 measure 调用的。
2、测量和排版、绘制一样，是从外到内遍历调用的，要知道ViewGroup的大小就要先知道它子View的大小。
3、onMeasure被调用了两遍，全部内容被测量了两便。
4、onMeasure会赋值 measureWidth、measureHeight。
5、xml中的宽高与测量参数的关系：
	match_parent 对应 EXACTLY + 父大小
	wrap_content 对应 AT_MOST + 父大小
	固定值 		 对应 EXACTLY + 给定的值转px
6、计算出的测量大小与测量参数的关系：(size值就是测量传入的mode+size中的size值)
exactly(准确)模式计算出的 — 测量大小 = size值。 日志中TextView2的宽度甚至超出了屏幕
at most(最多)模式计算出的 — 如果 内容大小 <= size值 则 内容大小 <= 测量大小 <= size值。
	测量大小不会超过 size 值。 日志中 FrameLayout2的宽度就和屏幕同宽

—————————— 2 View(wrap)的大小由直接子View控制

布局模式：
LinearLayout0(wrap,match,垂直){
	FrameLayout0(match,wrap){
		TextView0(wrap,wrap,文字多)
	}
	TextView1(wrap,wrap,文字少)
}

测量日志：(简略为只看第一遍测量)
E: TestLinearLayout0 之前 AT_MOST 1080 , EXACTLY 1851 | 之前 0 , 0
E:     TestFrameLayout0 之前 AT_MOST 1080 , AT_MOST 1851 | 之前 0 , 0
E:         TestTextView0 之前 AT_MOST 1080 , AT_MOST 1851 | 之前 0 , 0
E:         TestTextView0 之后 AT_MOST 1080 , AT_MOST 1851 | 之后 168 , 57
E:     TestFrameLayout0 之后 AT_MOST 1080 , AT_MOST 1851 | 之后 168 , 57
E:     TestTextView1 之前 AT_MOST 1080 , AT_MOST 1794 | 之前 0 , 0
E:     TestTextView1 之后 AT_MOST 1080 , AT_MOST 1794 | 之后 84 , 57
E:     TestFrameLayout0 之前 EXACTLY 84 , EXACTLY 57 | 之前 168 , 57
E:         TestTextView0 之前 AT_MOST 84 , AT_MOST 57 | 之前 168 , 57
E:         TestTextView0 之后 AT_MOST 84 , AT_MOST 57 | 之后 84 , 57
E:     TestFrameLayout0 之后 EXACTLY 84 , EXACTLY 57 | 之后 84 , 57
E: TestLinearLayout0 之后 AT_MOST 1080 , EXACTLY 1851 | 之后 84 , 1851

分析日志：
上文中 FrameLayout0 在 TextView1 测量后被立即重新测量了一遍！这次额外的测量仍然是由 measure 调用。
第一次传入的测量参数 最多1080 在第二次改成了 最多84。猜测额外的调用是由于被依赖的外层 LinearLayout0 的预期大小(还未赋值)改变引起的。
由此推断，当A依赖B的大小时，一旦B的大小改变，A会立即重新测量。

—————————— 3 ScrollView + WebView(GONE) 的测量

布局：
ScrollView0(match,match){
	FrameLayout0(match,match){
		TextView0(10dp,1000dp)
		WebView0(match,match,隐藏gone)
	}
}


测量日志：
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1776 | 之前 0 , 0
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TTextView0 之前 EXACTLY 30 , EXACTLY 3000 | 之前 0 , 0
E:         TTextView0 之后 EXACTLY 30 , EXACTLY 3000 | 之后 30 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1776 | 之后 1080 , 1776
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:         TTextView0 之前 EXACTLY 30 , EXACTLY 3000 | 之前 30 , 3000
E:         TTextView0 之后 EXACTLY 30 , EXACTLY 3000 | 之后 30 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: 开始初始化WebView
E: 开始指定网址
E: 指定网址完毕
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3000
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3000
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848

分析日志：
1、ScrollView 内的唯一子View高度无论我写 wrap_content 还是 match_parent 都会被强制改为 unspecified(不明)
2、unspecified 模式类似于 wrap 测量结果是由子View决定的，不同的是 wrap 计算出的测量结果不会比size值大。
	unspecified 计算出的测量值没有 size值 限制(上述例子传入了0)。
3、隐藏的View不参与测量，但其内容改变时会引起测量，只是依旧不参与测量。

—————————— 4 ScrollView + WebView(match) + 软键盘 的测量

布局：
ScrollView0(match,match){
	FrameLayout0(match,match){
		TextView0(10dp,1000dp,gone)
		WebView0(match,match)
	}
}

测量日志：
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1776 | 之前 0 , 0
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1776 | 之后 1080 , 1776
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: 开始初始化WebView
E: 开始指定网址
E: 指定网址完毕
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 168
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 168
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 168
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 168
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3660
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3660
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3660
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3660
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3372
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3372
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3372
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3372
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3870
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3870
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3870
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3870
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4239
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4239
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
弹出软键盘
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 4239
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 4239
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4239
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4239
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1045 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 4239
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 4239
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4239
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4239
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1045 | 之后 1080 , 1045

分析日志：
ScrollView 的唯一子View高度是 unspecified(不明)，依赖此值的内部View(match)高度也会被改为 unspecified(不明)
unspecified 模式下高度随内容变化，webView初始无内容，所以高度为0
指定网址后 webView 的高度开始变化，最后变成高出屏幕的值(因为unspecified不受size值限制)
弹出软键盘后 ScrollView 的高度减少了803px 但它子View的高度不变

—————————— 5 ScrollView + WebView(wrap) + 软键盘 的测量

布局：
ScrollView0(match,match){
	FrameLayout0(match,match){
		TextView0(10dp,1000dp,gone)
		WebView0(match,wrap)
	}
}

测量日志：
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1776 | 之前 0 , 0
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1776 | 之后 1080 , 1776
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: 开始初始化WebView
E: 开始指定网址
E: 指定网址完毕
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 168
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 168
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 168
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 168
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 954
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 954
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 954
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 954
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3156
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3156
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3156
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3156
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3654
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 3654
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3654
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 3654
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4023
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 4023
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
弹出软键盘
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 4023
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 4023
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1045 | 之前 1080 , 1848
E:     TFrameLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:     TFrameLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1045 | 之后 1080 , 1045

分析日志：
弹出软键盘前的日志信息和第4次分析的日志信息完全相同。
弹出软键盘后，前者：测量两遍高不变；后者：测量三遍高变0。
测量传入的参数是完全相同的，那么必然是wrap所在的其它地方导致了此问题。
我复习了下软键盘相关资料，在有滚动View时自动使用adjustResize策略：缩小整个布局的大小，再把软键盘摆下面。

查看View的测量源码，onMeasure 中仅调用 setMeasuredDimension 来赋值测量结果，而 setMeasuredDimension 接受的是一个大小值，不含mode
这个大小值由 getDefaultSize(minimumSize,modeSize)得来，仅当 unspecified 模式时使用 minimumSize(最小大小) 作为测量大小。
minimumSize 由 getSuggestedMinimumWidth() 得到，我重写此方法得知返回了0，那么WebView高度也应为0，它必然重写了onMeasure方法
但是我看不到 WebView.onMeasure是如何重写的
无法解答为何 scrollView 嵌套 webView(wrap) 时弹出软键盘导致WebView高度变0

WebView是一个滚动View，当它被嵌套在另一个滚动View时必须让自身高度与内容高度一致才能正常显示，并且不会处理滚动事件。其它的滚动View嵌套也是这么做的。
所以下拉、上拉、浮动标题 之类依赖滚动监听的功能都将失效。

—————————— 6 约束布局的测量不一般

布局：
ScrollView(match,match){
	ConstraintLayout(match,match){
		TextView(wrap,match)
		WebView(wrap,match)
	}
}

测量日志：

	替换最外层为 LinearLayout 时(约束布局match)
E: TLinearLayout0 之前 EXACTLY 1080 , EXACTLY 1776 | 之前 0 , 0
E:     TConstraintLayout0 之前 EXACTLY 1080 , EXACTLY 1776 | 之前 0 , 0
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 1776 | 之前 0 , 0
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 1776 | 之后 72 , 1776
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 1776 | 之前 0 , 0
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 1776 | 之后 1080 , 1776
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 1776 | 之前 72 , 1776
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 1776 | 之后 72 , 1776
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 1776 | 之前 1080 , 1776
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 1776 | 之后 1080 , 1776
E:     TConstraintLayout0 之后 EXACTLY 1080 , EXACTLY 1776 | 之后 1080 , 1776
E: TLinearLayout0 之后 EXACTLY 1080 , EXACTLY 1776 | 之后 1080 , 1776
E: TLinearLayout0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:     TConstraintLayout0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 1848 | 之前 72 , 1776
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 1848 | 之后 72 , 1848
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 1848 | 之后 1080 , 1848
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 1848 | 之前 72 , 1848
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 1848 | 之后 72 , 1848
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 1848 | 之前 1080 , 1848
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 1848 | 之后 1080 , 1848
E:     TConstraintLayout0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: TLinearLayout0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: 开始初始化WebView
E: 开始指定网址
E: 指定网址完毕

	替换最外层为 LinearLayout 时(约束布局wrap)
E: TLinearLayout0 之前 EXACTLY 1080 , EXACTLY 1776 | 之前 0 , 0
E:     TConstraintLayout0 之前 EXACTLY 1080 , AT_MOST 1776 | 之前 0 , 0
E:         TTextView0 之前 AT_MOST 1080 , AT_MOST 1776 | 之前 0 , 0
E:         TTextView0 之后 AT_MOST 1080 , AT_MOST 1776 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , AT_MOST 1776 | 之前 0 , 0
E:         TWebView0 之后 AT_MOST 1080 , AT_MOST 1776 | 之后 1080 , 0
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 59 | 之前 72 , 59
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 59 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 0 | 之前 1080 , 0
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 0 | 之后 1080 , 0
E:     TConstraintLayout0 之后 EXACTLY 1080 , AT_MOST 1776 | 之后 1080 , 59
E: TLinearLayout0 之后 EXACTLY 1080 , EXACTLY 1776 | 之后 1080 , 1776
E: TLinearLayout0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:     TConstraintLayout0 之前 EXACTLY 1080 , AT_MOST 1848 | 之前 1080 , 59
E:         TTextView0 之前 AT_MOST 1080 , AT_MOST 1848 | 之前 72 , 59
E:         TTextView0 之后 AT_MOST 1080 , AT_MOST 1848 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , AT_MOST 1848 | 之前 1080 , 0
E:         TWebView0 之后 AT_MOST 1080 , AT_MOST 1848 | 之后 1080 , 0
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 59 | 之前 72 , 59
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 59 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 0 | 之前 1080 , 0
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 0 | 之后 1080 , 0
E:     TConstraintLayout0 之后 EXACTLY 1080 , AT_MOST 1848 | 之后 1080 , 59
E: TLinearLayout0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: 开始初始化WebView
E: 开始指定网址
E: 指定网址完毕

	最外层为ScrollView 时(约束布局match)
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1776 | 之前 0 , 0
E:     TConstraintLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TTextView0 之前 AT_MOST 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TTextView0 之后 AT_MOST 1080 , UNSPECIFIED 0 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , UNSPECIFIED 0 | 之前 0 , 0
E:         TWebView0 之后 AT_MOST 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 59 | 之前 72 , 59
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 59 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 0 | 之前 1080 , 0
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 0 | 之后 1080 , 0
E:     TConstraintLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 59
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1776 | 之后 1080 , 1776
E: TScrollView0 之前 EXACTLY 1080 , EXACTLY 1848 | 之前 1080 , 1776
E:     TConstraintLayout0 之前 EXACTLY 1080 , UNSPECIFIED 0 | 之前 1080 , 59
E:         TTextView0 之前 AT_MOST 1080 , UNSPECIFIED 0 | 之前 72 , 59
E:         TTextView0 之后 AT_MOST 1080 , UNSPECIFIED 0 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , UNSPECIFIED 0 | 之前 1080 , 0
E:         TWebView0 之后 AT_MOST 1080 , UNSPECIFIED 0 | 之后 1080 , 0
E:         TTextView0 之前 AT_MOST 1080 , EXACTLY 59 | 之前 72 , 59
E:         TTextView0 之后 AT_MOST 1080 , EXACTLY 59 | 之后 72 , 59
E:         TWebView0 之前 AT_MOST 1080 , EXACTLY 0 | 之前 1080 , 0
E:         TWebView0 之后 AT_MOST 1080 , EXACTLY 0 | 之后 1080 , 0
E:     TConstraintLayout0 之后 EXACTLY 1080 , UNSPECIFIED 0 | 之后 1080 , 59
E: TScrollView0 之后 EXACTLY 1080 , EXACTLY 1848 | 之后 1080 , 1848
E: 开始初始化WebView
E: 开始指定网址
E: 指定网址完毕

分析日志：
上述日志是替换约束布局测量模式的对比。
值得注意的是，ConstraintLayout 布局下的子View 被额外测量了一遍！当高度是准确值时，子View的测量模式不变，额外测量没影响。
但是当 ConstraintLayout 的测量模式不为 exactly 时 (most 或 unspecified)，它会在自己的额外测量中锁定子View的大小。
在滚动View中布局ViewGroup会不明高度，一般布局会动态的依赖子View的高度，这个约束布局反而一开始就固定了子View的高度。这就是为什么 ScrollView 套 ConstraintLayout 套 WebView(match) 的高度为0。



QA总结：
Q：重写分析onMeasure有啥用？
A：	1、证实 onMeasure 也是像 onLayout 那样从外到内遍历，先子后父的。
	2、所有 onMeasure 都是被 measure 而非 onLayout 调用的。
	3、发现 xml 中写的宽高、测量参数、测量结果 的关系：
		match_parent 对应 EXACTLY + 父大小
		wrap_content 对应 AT_MOST + 父大小
		固定值 		 对应 EXACTLY + 给定的值转px
		滚动View中的直接子View或其它match此子View的 对应 UNSPECIFIED + 0 (除非给固定值)

		exactly：测量结果 = size值
		at most：测量结果 = 内容大小 但不能超过 size值
		unspecified：测量结果 = 内容大小
	4、发现gone的View不参与测量，但是其改变仍会导致重新测量。
Q：为啥wrap布局的大小依赖直接子View而非更大的间接子View？
A：wrap布局遍历测量子View，一旦发现大小可靠的子View就会用它的大小来赋值自己的预期大小，并用预期大小重新测量其它match自己的子View。
	所以直接子View的大小决定了 wrap 自己的父布局大小。
Q：为何 ScrollView 套 WebView 时，WebView设wrap 导致软键盘弹出后高度变0，WebView设match 就没事？
A：未解决，只知道 WebView的高度由于整个布局缩小而归0的。
Q：ConstraintLayout 和普通布局的测量有何不同？
A：约束布局会额外测量子View一遍，当自己大小已知时(exactly)这次测量无差别，但当自己大小未知时(at most或unspecified)，
	额外测量会使用exactly模式，导致子View的宽高被锁定。这就是为什么非match的约束布局会出现奇怪的问题。
 */