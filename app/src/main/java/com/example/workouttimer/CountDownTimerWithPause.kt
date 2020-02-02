package com.example.workouttimer

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log

private const val TAG = "CountDownTimerWithPause"

abstract class CountDownTimerWithPause(var millisInFuture: Long, val countDownInterval: Long, val runAtStart: Boolean){
    private var stopTimeInFuture: Long = 0
    private val totalCountdown: Long = millisInFuture
    private var pauseTimeRemaining: Long = 0
    private val MSG: Int = 1

    fun cancel(){
        handler.removeMessages(MSG)
        Looper.myLooper()?.quit()
    }

    fun create(): CountDownTimerWithPause{
        if(millisInFuture <= 0){
            onFinish()
        } else {
            pauseTimeRemaining = millisInFuture
        }

        if(runAtStart){
            resume()
        }
        return this
    }

    fun pause(){
        if(!isPaused()){
            pauseTimeRemaining = timeLeft()
            cancel()
            Log.d(TAG, "pause() called")
        }
    }

    fun resume(){
        if(isPaused()){
            millisInFuture = pauseTimeRemaining
            stopTimeInFuture = SystemClock.elapsedRealtime() + millisInFuture
            handler.sendMessage(handler.obtainMessage(MSG))
            pauseTimeRemaining = 0
            Log.d(TAG, "resume() called")
        }
    }

    fun isPaused(): Boolean{
        return (pauseTimeRemaining > 0)
    }

    fun timeLeft(): Long{
        return if(isPaused()) pauseTimeRemaining else maxOf(stopTimeInFuture - SystemClock.elapsedRealtime(), 0)
    }

    fun totalCountDown(): Long{
        return totalCountdown
    }

    fun timePassed(): Long{
        return totalCountdown - timeLeft()
    }

    fun hasBeenStarted(): Boolean{
        return pauseTimeRemaining <= millisInFuture
    }

    abstract fun onTick(millisUntilFinished: Long)

    abstract fun onFinish()

    private val handler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            val millisLeft = timeLeft()
            Log.d(TAG, "Tick Message Received")
            when{
                (millisLeft <= 0) ->{
                    onFinish()
                    cancel()
                }
                (millisLeft < countDownInterval)->{
                    sendMessageDelayed(obtainMessage(MSG), millisLeft)
                }
                else->{
                    val lastTickStart = SystemClock.elapsedRealtime()
                    onTick(millisLeft)
                    var delay = countDownInterval - (SystemClock.elapsedRealtime() - lastTickStart)
                    while(delay < 0) delay += countDownInterval
                    Log.d(TAG, "Timer Waited delay = $delay")
                    sendMessageDelayed(obtainMessage(MSG), delay)
                }
            }
            Looper.loop()
        }
    }
}