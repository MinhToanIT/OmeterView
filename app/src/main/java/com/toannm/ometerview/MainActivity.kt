package com.toannm.ometerview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speed_view.speedTo(70f,1000)
        speed_view.withTremble = false
        speed_view.isTickRotation = false

        setColor()

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                speed_view.speedTo(p1.toFloat(),1000)
                setColor()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
    }

    private fun setColor(){
        if(speed_view.speed < 40){
            speed_view.setProgressColor(Color.parseColor("#e53935"))
        }else if(speed_view.speed >= 40 && speed_view.speed < 80){
            speed_view.setProgressColor(Color.parseColor("#ffc107"))
        }else{
            speed_view.setProgressColor(Color.parseColor("#2196f3"))
        }
    }
}