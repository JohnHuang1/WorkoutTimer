package com.example.workouttimer

import android.os.Handler
import android.os.Message
import android.os.Parcelable

class WorkoutHandler(private val receiver: AppReceiver) : Handler() {
    override fun handleMessage(msg: Message?) {
        super.handleMessage(msg)
        if(msg != null) receiver.onReceiveResult(msg)
    }

    interface AppReceiver{
        fun onReceiveResult(msg: Message){}
    }
}