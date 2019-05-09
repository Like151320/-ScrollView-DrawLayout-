package com.example.a26050.statusbaradapterdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.utils.StatusBarUtils

class ScrollEditViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_edit_view)

        StatusBarUtils.from(this).setTransparentStatusbar(true).setLightStatusBar(true).process()
//        AndroidBug5497Workaround.assistActivity(this)//使用它依然会出现很多小BUG，干脆不用了
    }
}
/*
软键盘全面解析：

1、软键盘的本质
    软键盘实际是一个Dialog，由InputMethodService为输入法创建的，并对某些参数进行配置，使其可以在底部或全屏显示。当我们点击输入框时，
        系统会对当前主窗口进行调整，以留出空间显示该Dialog到底部。
    Service ——
        AbstractInputMethodService —— 定义了输入法的行为，若要自定义输入法，则必须继承此类
            InputMethodService —— Android原生实现管理键盘的Service
            点入查看其源码可以看到在onCreate()中使用了Theme进行属性初始化。
                可以看到源码中的创建语句 Gravity.BOTTOM 证实了此Window就是底部的软键盘window。
                mWindow = new SoftInputWindow(this, "InputMethod", mTheme, null, null, mDispatcherState,
                WindowManager.LayoutParams.TYPE_INPUT_METHOD, Gravity.BOTTOM, false);
                而SoftInputWindow继承自Dialog证实了这是一个Dialog



 */