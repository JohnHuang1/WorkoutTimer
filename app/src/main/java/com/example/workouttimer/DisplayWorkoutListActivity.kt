package com.example.workouttimer

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_recycler_view.*

private val TAG: String = DisplayWorkoutActivity::class.java.simpleName

class DisplayWorkoutActivity : AppCompatActivity() {

    private var workoutList: MutableList<Workout> = ArrayList()
    private val db = DataBaseHandler(this)

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

    private fun openAddWorkoutActivity(btn: FloatingActionButton,context : Context){
        btn.setExpanded(true)
        context.startActivity(Intent(context, AddWorkoutActivity::class.java))
    }
}
