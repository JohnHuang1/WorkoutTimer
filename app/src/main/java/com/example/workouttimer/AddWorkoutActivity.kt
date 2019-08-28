package com.example.workouttimer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_workout.*

class AddWorkoutActivity : AppCompatActivity() {

    val db = DataBaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_workout)

        btnCreate.setOnClickListener{createBtnListener()}
    }

    private fun createBtnListener(){
        val text = txtName.text.toString()
        if(text != ""){
            db.addWorkoutToList(Workout(text, db.getAvailableId(db.TABLE_WORKOUTS), db.getNextDisplayId()))
            this.startActivity(Intent(this, DisplayWorkoutActivity:: class.java))
        }
    }
}
