package com.example.workouttimer

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log

private const val TAG = "BackWorkoutService()"

class BackgroundWorkoutService: IntentService(BackgroundWorkoutService::class.simpleName) {
    var serviceStarted = false
    var currentTimer: CountDownTimerWithPause? = null
    var inCircuitBoolean = false
    var workoutIndex = -1
    var circuitIndex = -1
    private var workout: MutableList<WorkoutItem> = mutableListOf()
    private lateinit var handler: Messenger

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            if (!serviceStarted) {
                workout = DataBaseHandler(this).getWorkoutItemList(intent.getIntExtra("wkID", -1))
                handler = intent.getParcelableExtra("handler")
                serviceStarted = true
                Looper.loop()
            }
            when (intent.getStringExtra("action")) {
                "start" -> {
                    Log.d(TAG, "Started")
                    moveToNextExercise(handler)
                }
                "stop" -> {
                    Log.d(TAG, "Stopped")
                    endWorkout(handler)
                }
                "pause" -> {
                    if (currentTimer != null) {
                        currentTimer?.pause()
                        Log.d(TAG, "Paused CurrentTimer == null: ${(currentTimer != null)}")
                    }
                }
                "resume" -> {
                    if (currentTimer != null) {
                        currentTimer?.resume()
                        Log.d(TAG, "Resumed")
                    }
                }
                else -> {
                    Log.d(TAG, "Invalid action passed")
                }
            }
        }
    }

    private fun moveToNextExercise(messenger: Messenger){
        if(inCircuitBoolean){
            val currentCircuit = workout[workoutIndex] as Circuit
            if(currentCircuit.excList.size <= ++circuitIndex){
                workoutIndex++
                circuitIndex = -1
            }
        } else {
            if(workout.size <= ++workoutIndex){
                endWorkout(messenger)
            }
        }

        var currentItem = workout[workoutIndex]
        if (currentItem.javaClass == Circuit::class.java){
            currentItem as Circuit
            currentItem = currentItem.excList[circuitIndex]
        }
        currentItem as Exercise
        Log.d(TAG, "CurrentItem = $currentItem")
        val time = currentItem.time
        if(time != null && time > 0){
            currentTimer = object: CountDownTimerWithPause(time * 1000, 1000, true){
                override fun onTick(millisUntilFinished: Long) {
                    val msg = Message()
                    msg.arg1 = (millisUntilFinished / 1000).toInt()
                    Log.d(TAG, "currentTimer Ticked")
                    msg.obj = currentItem
                    messenger.send(msg)
                    Log.d(TAG, "Tick Message Sent")
                }

                override fun onFinish() {
                    Log.d(TAG, "CountdownTimer onFinish() called")
                    val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(applicationContext, notification)
                    r.play()
                    moveToNextExercise(messenger)
                    Log.d(TAG, "CountDownTimer onFinish() Finished")
                }
            }.create()
            //TODO Tread Keeps Dying
        } else {
            currentTimer = null
            val msg = Message()
            msg.obj = currentItem
            val reps = currentItem.reps
            if(reps != null) msg.arg1 = reps
            messenger.send(msg)
        }
    }

    private fun endWorkout(messenger: Messenger){
        currentTimer?.cancel()
        val msg = Message()
        msg.arg1 = -1
        messenger.send(msg)
        Looper.myLooper()?.quit()
        stopSelf()
    }
}