package com.example.a26050.statusbaradapterdemo.utils

/**
 * 作者: Li_ke
 * 日期: 2019/4/26 10:51
 * 作用: 配合 MLog 可在日志区点击查看日志代码。或者跟踪是谁调用了次行代码。
 * 注意：对性能有影响，不要频繁调用(比如滚动监听、onDraw)
 */
object StackTraceUtils {

	private const val baseLevel = 3
	/** 可在日志区点击的当前调用者方法
	 * @param level : 当要在新的位置使用此方法时,需传入调用层级来确定在log区点击后跳转到哪一层
	 *
	 * 比如：log(){ [showNow]0 } 在log区点击会跳转{ [showNow]0 }代码行
	 *
	 * 比如：log(){ [showNow]1 } 点击会跳转到调用log()代码行
	 *
	 * 警告: 不能使用kotlin默认参数,会导致kotlin生成默认代码导致代码调用层数改变
	 * @param length : 追踪多长的调用者,为0则不追踪,即没数据返回
	 */
	//调用处4
	fun showNow(level: Int, length: Int): String {
		//baseLevel = 此行代码处, +1 = 方法now调用处, +level = 多加几层到想跳的调用处
		val result = StringBuilder()
		//调用处3
		traceInvoke(
			baseLevel + 1 + level,
			baseLevel + 1 + level + length
		).reversedArray().map { it.toSimpleString() }.forEachIndexed { index, s ->
			result.append("\n")
			//前面的空格
			for (i in 0 until index)
				result.append(" ")
			result.append(s)
		}
		result.append(" ")
		return result.toString()
	}

	/** 追踪调用者
	 * @param from : 追踪长度(单位:方法数)，3 -> 2 -> 1 -> 0
	 * @param to   : 追踪长度(单位:方法数)
	 *
	 * 3 = 此方法被调用处；比如调用traceInvoke(3,3)，则在log区点击会跳转 traceInvoke(3,3) 这一行
	 */
	//调用处3
	private fun traceInvoke(from: Int, to: Int): Array<StackTraceElement> {
		//调用处2
		return Thread.currentThread().stackTrace.copyOfRange(from, to)
		// getStackTrace() 内部的 VMStack.getThreadStackTrace  //调用处1
	}

	/** 将复杂的Element 转为长度短的,可在日志区点击的字符串
	 * 类名.方法名(文件名:行)
	 * */
	private fun StackTraceElement.toSimpleString(): String {
		val simpleClassName = className.split(".").last()
		return if (lineNumber >= 0)
			"$simpleClassName.$methodName($fileName:$lineNumber)"
		else
			"$simpleClassName.$methodName($fileName)"
	}
}