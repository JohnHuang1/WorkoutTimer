package com.example.workouttimer

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

const val WORK_TICK = "com.workouttimer.workoutservice.TICK"
const val WORK_UPDATE = "com.workouttimer.workoutservice.UPDATE"

private const val TAG = "WorkoutService"

class WorkoutService : Service() {
    private lateinit var workout: MutableList<WorkoutItem>
    private var binder = LocalBinder()
    private var currentIndex: Int = -1
    private lateinit var currentItem: WorkoutItem
    private var currentCircuit: Circuit = Circuit()
    private var circuitRep: Int = 0
    private var circuitIndex: Int = -1
    private var currentTimer: CountDownTimerWithPause? = null
    private lateinit var broadcastManager: LocalBroadcastManager
    private val circuitNames = emptyMap<Int, String>()


    override fun onCreate() {
        super.onCreate()
        broadcastManager = LocalBroadcastManager.getInstance(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null) {
            val wkID = intent.getIntExtra("wkID", -1)
            workout = DataBaseHandler(this).getWorkoutItemList(wkID)
            Log.d(TAG, "wkID = $wkID")
        }
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
    inner class LocalBinder : Binder(){
        fun getService(): WorkoutService{ return this@WorkoutService }
    }

    fun next(){
        currentTimer?.stop()
        Log.d(TAG, "next() called: currentIndex = $currentIndex")
        if(workout.size <= currentIndex + 1 && (currentCircuit.id == -1 || (currentCircuit.reps == circuitRep && currentCircuit.excList.size <= circuitIndex + 1))){
            Intent(WORK_UPDATE).putExtra("workout_finished", 1).also{
                intent -> broadcastManager.sendBroadcast(intent)
            }
            stopSelf()
            return
        }
        if(currentCircuit.id == -1){
            currentItem = getNextItem()
        } else{
            if(currentCircuit.excList.size > ++circuitIndex){
                currentItem = currentCircuit.excList[circuitIndex]
            } else {
                if(currentCircuit.reps <= circuitRep){
                    circuitRep = 0
                    circuitIndex = -1
                    currentCircuit = Circuit()
                    Intent(WORK_UPDATE).putExtra("circuit_rep", 0).putExtra("circuit_name", "").also{
                            intent -> broadcastManager.sendBroadcast(intent)
                    }
                    currentItem = getNextItem()
                } else {
                    circuitIndex = 0
                    currentItem = currentCircuit.excList[circuitIndex]
                    circuitRep++
                    Intent(WORK_UPDATE).putExtra("circuit_rep", circuitRep).putExtra("circuit_name", currentCircuit.name).also{
                            intent -> broadcastManager.sendBroadcast(intent)
                    }
                }
            }
        }
        updateItem(currentItem as Exercise)
        val time = (currentItem as Exercise).time
        if(time != null && time != 0.toLong()){
            currentTimer = object : CountDownTimerWithPause(time, true){
                override fun tick(millisLeft: Long) {
                    val intent = Intent(WORK_UPDATE)
                    intent.putExtra(WORK_TICK, millisLeft.toDouble().div(1000.toDouble()).roundToInt())
                    broadcastManager.sendBroadcast(intent)
                }
                override fun finish() {
                    next()
                }
            }
            currentTimer?.create()
        } else {
            currentTimer = null
        }
        if(workout.size <= currentIndex + 1 && (currentCircuit.id == -1 || (currentCircuit.reps == circuitRep && currentCircuit.excList.size <= circuitIndex + 1))){
            val lastIntent = Intent(WORK_UPDATE)
            broadcastManager.sendBroadcast(lastIntent.putExtra("last_exercise", 1))
        }
    }

    fun start(){
        currentIndex = -1
        circuitIndex = -1
        currentItem = getNextItem()
        updateItem(currentItem as Exercise)
        val time = (currentItem as Exercise).time
        if(time != null){
            currentTimer = object : CountDownTimerWithPause(time, true){
                override fun tick(millisLeft: Long) {
                    val intent = Intent(WORK_UPDATE)
                    intent.putExtra(WORK_TICK, millisLeft.toDouble().div(1000.toDouble()).roundToInt())
                    broadcastManager.sendBroadcast(intent)
                }
                override fun finish() {
                    next()
                }
            }
            currentTimer?.create()
        }
    }

    fun pause(){
        currentTimer?.pause()
    }

    fun resume(){
        currentTimer?.resume()
    }

    fun stop(){
        currentTimer?.stop()
        stopSelf()
    }

    private fun updateItem(exc: Exercise){
        val intent = Intent(WORK_UPDATE)
        intent.putExtra("item_name", exc.name)
        if(exc.reps != null && exc.reps != 0){
            intent.putExtra("reps", exc.reps as Int)
        } else {
            intent.putExtra("time", exc.time)
        }
        broadcastManager.sendBroadcast(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "Service Destroyed")
        super.onDestroy()
    }

    private fun getNextItem(): WorkoutItem{
        return if(workout[++currentIndex] is Circuit){
            currentCircuit = workout[currentIndex] as Circuit
            circuitRep++
            Intent(WORK_UPDATE).putExtra("circuit_rep", circuitRep).putExtra("circuit_name", currentCircuit.name).also{
                    intent -> broadcastManager.sendBroadcast(intent)
            }
            currentCircuit.excList[++circuitIndex]
        } else {
            workout[currentIndex]
        }
    }

}