package com.example.workouttimer

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_show_workout.*

private const val TAG = "ShowWorkoutActivity"

class ShowWorkoutActivity : AppCompatActivity(), WorkoutHandler.AppReceiver {
    private var backPressed = false
    var wkID = -1
    private var bound = false
    private lateinit var workoutService : WorkoutService
    private lateinit var broadcastReceiver: BroadcastReceiver

    val connection = object: ServiceConnection {
        override fun onServiceConnected( className: ComponentName?, iBinder: IBinder?) {
            workoutService = (iBinder as WorkoutService.LocalBinder).getService()
            bound = true
        }
        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_workout)

        wkID = this.intent.getIntExtra("wkID", -1)
        supportActionBar?.hide()
        actionBar?.hide()


        Intent(this, WorkoutService::class.java).also {
            intent -> startService(intent.putExtra("wkID", wkID))
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val name = intent?.getStringExtra("item_name")
                val reps = intent?.getIntExtra("reps", -1)
                val time = intent?.getLongExtra("time", -1)
                val timeLeft = intent?.getIntExtra(WORK_TICK, -1)
                val last = intent?.getIntExtra("last_exercise", -1)
                val end = intent?.getIntExtra("workout_finished", -1)
                val circuitName = intent?.getStringExtra("circuit_name")
                val circuitRep = intent?.getIntExtra("circuit_rep", -1)
                Log.d(TAG, "onReceive Intent: name = $name | reps = $reps | time = $time")
                if(name != null){
                    txtItemName.text = name
                }
                if(reps != -1){
                    txtCount.text = reps.toString()
                    txtCountType.text = getString(R.string.reps_string)
                    btnStart.text = "Next"
                }
                if(time != (-1).toLong()){
                    txtCount.text = time.toString()
                    txtCountType.text = getString(R.string.seconds_string)
                    btnStart.text = "Pause"
                }
                if(timeLeft != -1){
                    txtCount.text = timeLeft.toString()
                }
                if(end == 1){
                    endWorkout()
                }
                if(last == 1 && txtCountType.text == getString(R.string.reps_string)){
                    btnStart.text = "Finish"
                }
                if(circuitRep != -1) {
                    txtCircuitRep.text = if(circuitRep != 0) getString(R.string.round, circuitRep) else ""
                }
                if(circuitName != null){
                    txtCircuitName.text = if(circuitName != "") getString(R.string.circuit_colon, circuitName) else ""
                }
            }
        }

        btnStart.setOnClickListener{
            when(btnStart.text){
                "Pause"->{
                    workoutService.pause()
                    btnStart.text = "Resume"
                }
                "Resume"->{
                    workoutService.resume()
                    btnStart.text = "Pause"
                }
                "Stop"->{
                    workoutService.stop()
                    btnStart.text = "Return"
                }
                "Return"->{
                    this.onBackPressed()
                }
                "Start"->{
                    workoutService.start()
                    btnStart.text = "Pause"
                }
                "Next"->{
                    workoutService.next()
                }
                "Finish"->{
                    endWorkout()
                }
            }
        }
    }

    override fun onBackPressed() {
        if(backPressed){
            workoutService.stop()
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press Again to Exit Workout", Toast.LENGTH_SHORT).show()
            backPressed = true
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, WorkoutService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter(WORK_UPDATE))
    }

    override fun onStop(){
        unbindService(connection)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        bound = false
        super.onStop()
    }

    fun endWorkout(){
        txtItemName.text = getString(R.string.you_are_finished)
        txtCountType.text = getString(R.string.Congratulations)
        txtCount.text = ""
        backPressed = true
        btnStart.text = "Return"
    }

}
