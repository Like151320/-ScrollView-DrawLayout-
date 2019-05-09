package com.example.a26050.statusbaradapterdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.utils.StatusBarUtils

class NoTitleScrollEditViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_title_scroll_edit_view)

        StatusBarUtils.from(this).setTransparentStatusbar(true).setLightStatusBar(true).process()
    }
}
