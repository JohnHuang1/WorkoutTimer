package com.example.workouttimer

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_recycler_view.*

private const val TAG = "DisplayExerciseActivity"

class DisplayExercisesActivity: AppCompatActivity() {

    private val db = DataBaseHandler(this)
    private var wkId = -1
    private var crcId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        wkId = intent.getIntExtra("wkID", -1)
        crcId = intent.getIntExtra("crcID", -1)

        Log.d(TAG, "wkID = $wkId | crcID = $crcId")

        val circuit = if(crcId != -1) db.getCircuit(crcId) else Circuit()
        title = db.getWorkoutName(wkId) + if(crcId != -1) " > ${circuit.name}" else ""
        val wkList = if(crcId == -1) db.getWorkoutItemList(wkId) else mutableListOf()
        val adapter = ExerciseRVAdapter(if(crcId == -1) wkList else circuit.excList, this, wkId)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val addBtn: FloatingActionButton = findViewById(R.id.btnAdd)
        addBtn.setOnClickListener{
            this.startActivity(Intent(this, AddExerciseActivity::class.java).apply{
                putExtra("wkID", wkId)
                if(crcId != -1) putExtra("crcID", crcId)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(if(crcId == -1) R.menu.workout_menu else R.menu.circuit_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.play_button -> {

            }
            R.id.edit_button -> {
                val inflater = layoutInflater.inflate(R.layout.edit_text_box, null)
                AlertDialog.Builder(this)
                    .setTitle(R.string.change_name_string)
                    .setCancelable(true)
                    .setView(inflater)
                    .setPositiveButton(R.string.save_string){ _, _ -> run{
                        val newName = inflater.findViewById<EditText>(R.id.edit_text_name).text.toString()
                        if(crcId == -1) {
                            updateWorkoutName(wkId, newName)
                            this.title = newName
                        }
                        else {
                            updateCircuitName(crcId, newName)
                            this.title = "${db.getWorkoutName(wkId)} > $newName"
                        }
                    }}
                    .setNegativeButton(R.string.cancel_string){ dialog, _ -> dialog.cancel()}
                    .create()
                    .show()
            }
            R.id.trash_button -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_delete_string)
                    .setCancelable(true)
                    .setPositiveButton(R.string.yes_string){ _, _ -> run{
                        if(crcId == -1){
                            db.deleteWorkout(wkId)
                        } else {
                            db.deleteCircuit(db.getCircuit(crcId), wkId)
                        }
                        onBackPressed()
                    }}
                    .setNegativeButton(R.string.cancel_string){ dialog, _ -> dialog.cancel()}
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if(crcId == -1){
            this.startActivity(Intent(this, DisplayWorkoutActivity::class.java))
        } else {
            this.startActivity(Intent(this, DisplayExercisesActivity::class.java).apply{
                putExtra("wkID", wkId)
            })
        }
    }

    private fun updateWorkoutName(wkID: Int, name: String){
        Toast.makeText(this, "${db.updateWorkout(wkID, name)} workout updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateCircuitName(crcID: Int, name: String){
        Toast.makeText(this, "${db.updateCircuit(crcID, name)} circuit updated", Toast.LENGTH_SHORT).show()
    }
}