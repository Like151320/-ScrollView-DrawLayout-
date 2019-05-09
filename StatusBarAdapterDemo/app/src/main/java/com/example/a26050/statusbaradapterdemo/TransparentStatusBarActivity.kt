package com.example.a26050.statusbaradapterdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.utils.StatusBarUtils

class TransparentStatusBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transparent_status_bar)

        StatusBarUtils.from(this).setTransparentStatusbar(true).setLightStatusBar(true).process()
    }
}
