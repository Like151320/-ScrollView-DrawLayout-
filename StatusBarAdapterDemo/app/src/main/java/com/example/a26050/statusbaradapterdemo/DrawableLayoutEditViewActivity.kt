package com.example.a26050.statusbaradapterdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.utils.StatusBarUtils

class DrawableLayoutEditViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawable_layout_edit_view)

        StatusBarUtils.from(this).setTransparentStatusbar(true).setLightStatusBar(true).process()
    }
}
