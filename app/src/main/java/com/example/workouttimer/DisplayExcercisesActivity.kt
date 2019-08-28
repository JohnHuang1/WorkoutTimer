package com.example.workouttimer

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Button
import kotlinx.android.synthetic.main.activity_recycler_view.*

class DisplayExercisesActivity: AppCompatActivity() {

    val db = DataBaseHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val wkId = intent.getIntExtra("id", -1)
        title = db.getWorkoutName(wkId)
        var wkList: MutableList<WorkoutItem> = db.getWorkoutItemList(wkId)
        var tempList: MutableList<WorkoutItem> = ArrayList()
        for(item in wkList){
            tempList.add(item)
            if(item::class == Circuit::class){
                tempList.addAll((item as Circuit).excList)
            }
        }
        val adapter = ExerciseRVAdapter(tempList, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val addBtn: FloatingActionButton = findViewById(R.id.btnAdd)
        addBtn.setOnClickListener{
            this.startActivity(Intent(this, AddExerciseActivity::class.java))
        }
    }
}