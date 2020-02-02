package com.example.workouttimer

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_recycler_view.*

private val TAG: String = DisplayWorkoutActivity::class.java.simpleName

class DisplayWorkoutActivity : AppCompatActivity() {

    private var workoutList: MutableList<Workout> = ArrayList()
    private val db = DataBaseHandler(this)
    private var backPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recycler_view)

        workoutList = db.getWorkoutList()

        val adapter = WorkoutListRVAdapter(workoutList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val addBtn: FloatingActionButton = findViewById(R.id.btnAdd)
        addBtn.setOnClickListener{openAddWorkoutActivity(addBtn, this)}
    }

    override fun onBackPressed() {
        if(backPressed){
            finish()
        } else {
            backPressed = true
            Toast.makeText(this, "Press 'Back' button again to exit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openAddWorkoutActivity(btn: FloatingActionButton,context : Context){
        btn.isExpanded = true
        context.startActivity(Intent(context, AddWorkoutActivity::class.java))
    }
}
