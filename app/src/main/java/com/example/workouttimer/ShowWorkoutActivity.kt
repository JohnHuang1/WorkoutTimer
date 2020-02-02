package com.example.workouttimer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.os.Messenger
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_show_workout.*

private const val TAG = "ShowWorkoutActivity"

class ShowWorkoutActivity : AppCompatActivity(), WorkoutHandler.AppReceiver {
    private var handler: WorkoutHandler? = null
    private var backPressed = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_workout)

        val wkID = this.intent.getIntExtra("wkID", -1)
        registerService(wkID)

        btnStart.setOnClickListener{
            val intent = Intent(applicationContext, BackgroundWorkoutService::class.java)
            when(btnStart.text){
                "Pause"->{
                    intent.putExtra("action", "pause")
                    btnStart.text = "Resume"
                }
                "Resume"->{
                    intent.putExtra("action", "resume")
                    btnStart.text = "Pause"
                }
                "Stop"->{
                    intent.putExtra("action", "stop")
                    btnStart.text = "Return"
                }
                "Return"->{
                    backPressed = false
                    this.onBackPressed()
                }
                "Start"->{
                    intent.putExtra("action", "start")
                    btnStart.text = "Pause"
                }
            }
            intent.apply{
                putExtra("handler", Messenger(handler))
                putExtra("wkID", wkID)
            }
            startService(intent)

        }
    }

    override fun onBackPressed() {
        if(backPressed){
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Press Again to Exit Workout", Toast.LENGTH_SHORT).show()
            backPressed = true
        }
    }

    override fun onReceiveResult(msg: Message) {
        Log.d(TAG, "onReceiveResult()")
        val count = msg.arg1
        val exc = msg.obj as Exercise?
        if(count != -1){
            Log.d(TAG, "onReceiveResult() count = $count")
            if(count >= 0) txtCount.text = count.toString()
            if(exc != null){
                txtItemName.text = exc.name
                txtCountType.text = when(exc.time){
                    null-> getString(R.string.reps_string)
                    0.toLong()-> getString(R.string.reps_string)
                    else-> getString(R.string.seconds_string)
                }
                Log.d(TAG, "onReceiveResult() Obj not null")
            }
        } else {
            txtItemName.text = getString(R.string.Congratulations)
            txtCountType.text = getString(R.string.you_are_finished)
            btnStart.text = getString(R.string.Return)
            Log.d(TAG, "onReceiveResult() Finish Reached")
        }
    }

    private fun registerService(wkID: Int){
        val intent = Intent(applicationContext, BackgroundWorkoutService::class.java)
        handler = WorkoutHandler(this)
        intent.apply{
            putExtra("handler", Messenger(handler))
            putExtra("wkID", wkID)
        }
        startService(intent)
    }

}
