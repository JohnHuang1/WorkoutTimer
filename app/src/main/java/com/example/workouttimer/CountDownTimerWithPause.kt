package com.example.workouttimer

import android.os.CountDownTimer
import android.util.Log

private const val TAG = "CountDownTimerWithPause"

abstract class CountDownTimerWithPause(var seconds: Long, val runAtStart: Boolean){
    private var timeLeft = seconds * 1000
    private var runningBool = false
    private lateinit var timer: CountDownTimer
    private var existBool = false

    abstract fun finish()
    abstract fun tick(millisLeft: Long)

    fun create(){
        if(!existBool){
            timer = object : CountDownTimer(seconds * 1000 + 500, 1000){
                override fun onTick(millisLeft: Long) {
                    timeLeft = millisLeft
                    tick(millisLeft)
                }
                override fun onFinish() {
                    finish()
                }
            }
            existBool = true
            if(runAtStart) start()
        }
    }
    fun resume(){
        if(!runningBool){
            timer = object : CountDownTimer(timeLeft, 1000){
                override fun onTick(millisLeft: Long) {
                    timeLeft = millisLeft
                    tick(millisLeft)
                }
                override fun onFinish() {
                    finish()
                }
            }
            timer.start()
            runningBool = true
        }
    }
    fun pause(){
        if(runningBool){
            Log.d(TAG, "timeLeft = $timeLeft")
            timer.cancel()
            runningBool = false
        }
    }
    fun start(){
        if(!runningBool){
            timer.start()
            runningBool = true
        }
    }
    fun stop(){
        timer.cancel()
        runningBool = false
    }

}