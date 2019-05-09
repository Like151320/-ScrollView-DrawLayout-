package com.example.a26050.statusbaradapterdemo.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.example.a26050.statusbaradapterdemo.BuildConfig
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


/**
 * 作者: Li_ke
 * 日期: 2018/10/25 0025 17:44
 */
object MLog {
	private val IS_LOG get() = BuildConfig.DEBUG
	private const val IS_SAVE_TO_FILE = false // 不受IS_LOG印象

	/**log默认TAG*/
	private const val DEFAULT_TAG = "Li_ke"
	/**log文件存储根目录*/
	private var saveRootPath: String? = null
	/**log文件标识(默认)*/
	private const val logFileTag: String = "MLog"

	fun init(context: Context) {
		saveRootPath = context.filesDir.path

		if (PackageManager.PERMISSION_GRANTED == context.packageManager.checkPermission(
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				context.packageName
			)
		)
//			saveRootPath = Environment.getExternalStorageDirectory().path
			saveRootPath = context.getExternalFilesDir(null)?.path //外部存储
		else
			w("没有写入权限,无法通过文件助手查看日志")
	}

	//======================外部使用======================

	@JvmStatic
	@JvmOverloads
	fun v(log: Any?, trace: String? = null, tag: String = DEFAULT_TAG) {
		log(Log.VERBOSE, tag, log, trace)
	}

	@JvmStatic
	@JvmOverloads
	fun d(log: Any?, trace: String? = null, tag: String = DEFAULT_TAG) {
		log(Log.DEBUG, tag, log, trace)
	}

	@JvmStatic
	@JvmOverloads
	fun i(log: Any?, trace: String? = null, tag: String = DEFAULT_TAG) {
		log(Log.INFO, tag, log, trace)
	}

	@JvmStatic
	@JvmOverloads
	fun w(log: Any?, trace: String? = null, tag: String = DEFAULT_TAG) {
		log(Log.WARN, tag, log, trace)
	}

	@JvmStatic
	@JvmOverloads
	fun e(log: Any?, trace: String? = null, tag: String = DEFAULT_TAG) {
		log(Log.ERROR, tag, log, trace)
	}

	/**直接存入指定的log文件,不打印*/
	fun saveFileSpecial(logFileTag: String, log: String) {
		if (IS_SAVE_TO_FILE)
			saveToFile(logFileTag, "", "", log)
	}

	/**不可能的BUG*/
	fun wtf(tag: String, msg: String) = Log.wtf(tag, msg)

	//======================功能函数======================

	/**
	 * 真正的 log 实现函数
	 * @param priority Log等级,例如[Log.INFO]
	 * @param log 自动根据数据类型打印内容
	 * @param trace 跟踪记录[StackTraceUtils]，可点击跳转log调用处
	 */
	private fun log(priority: Int, tag: String, log: Any?, trace: String? = null) {
		// log:Any -> log:String
		var msg = log.toString()
		if (trace != null)
			msg = "$trace\n$msg"

		//打印Log
		if (IS_LOG)
			Log.println(priority, tag, msg)

		//保存log
		if (IS_SAVE_TO_FILE) {
			val logLevel = when (priority) {
				Log.VERBOSE -> "v"
				Log.DEBUG -> "d"
				Log.INFO -> "i"
				Log.WARN -> "w"
				Log.ERROR -> "e"
				Log.ASSERT -> "a"
				else -> ""
			}
			try {
				saveToFile(
					logFileTag,
					tag,
					logLevel,
					msg
				)
			} catch (e: RuntimeException) {
				Log.e(tag, "日志保存失败-" + e.message)
			}
		}
	}

	/**保存到文件
	 * @param logFileTag log文件名标志,用于区分特殊log文件
	 * @param logLevel 此次log级别
	 */
	private fun saveToFile(logFileTag: String, logTag: String, logLevel: String, log: String) {
		check(IS_SAVE_TO_FILE)
		saveRootPath = saveRootPath
			?: error("必须先初始化MLog")

		thread {
			//生成log文件位置
			val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(Date())//按天为单位
			val savePath =
				saveRootPath + File.separator + "log" + File.separator + logFileTag + "_" + dateStr + ".txt"

			//创建log上级文件夹
			File(savePath).parentFile.run {
				if (!this.exists())
					this.mkdirs()
			}

			//创建log文件
			File(savePath).run {
				if (!this.exists())
					this.createNewFile()
			}

			//此次log内容
			val logMessage: String = kotlin.run {
				val time = SimpleDateFormat("HH-mm-ss", Locale.CHINA).format(Date())
				"${time}_${logTag}_${logLevel}：$log\n"
			}

			//写入文件
			FileOutputStream(savePath, true).use {
				it.write(logMessage.toByteArray(), 0, logMessage.toByteArray().size)
			}
		}
	}
}