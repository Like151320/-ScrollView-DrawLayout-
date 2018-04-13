package com.example.a26050.statusbaradapterdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.utils.StatusBarUtils

class EditAndScrollViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_and_scroll_view)

        StatusBarUtils.from(this).setLightStatusBar(true).setTransparentStatusbar(true).process()
    }
}
