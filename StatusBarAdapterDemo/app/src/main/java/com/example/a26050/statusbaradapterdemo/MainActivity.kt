package com.example.a26050.statusbaradapterdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.a26050.statusbaradapterdemo.measureTest.MeasureTestActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)


		button.setOnClickListener {
			startActivity(
				Intent(this, TransparentStatusBarActivity::class.java)
			)
		}
		button2.setOnClickListener {
			startActivity(
				Intent(this, NoTitleScrollEditViewActivity::class.java)
			)
		}
		button3.setOnClickListener {
			startActivity(
				Intent(this, DrawableLayoutEditViewActivity::class.java)
			)
		}
		button4.setOnClickListener {
			startActivity(
				Intent(this, EditAndScrollViewActivity::class.java)
			)
		}
		button5.setOnClickListener {
			startActivity(
				Intent(this, ScrollEditViewActivity::class.java)
			)
		}
		button6.setOnClickListener {
			startActivity(
				Intent(this, WebViewActivity::class.java)
			)
		}
		button7.setOnClickListener {
			startActivity(
				Intent(this, MeasureTestActivity::class.java)
			)
		}
	}
}
