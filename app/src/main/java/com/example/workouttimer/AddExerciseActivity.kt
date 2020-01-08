package com.example.workouttimer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_add_exercise.*

private const val TAG: String = "AddExerciseActivity:"

class AddExerciseActivity : AppCompatActivity() {
    enum class ItemType {NONE, EXERCISE, CIRCUIT}
    enum class CountType {NONE, REPS, SECS}
    private val db = DataBaseHandler(this)
    private var wkId = -1
    private var edit = -1
    private var crcId = -1
    private var id = -1
    private var exercise: Exercise = Exercise(-1, "", null, null)
    private var circuit: Circuit = Circuit()
    private var startName:String? = ""
    private var startNumber: String = ""
    private var startItemType: ItemType = ItemType.NONE
    private var startCountType: CountType = CountType.NONE
    private var currentItemType: ItemType = ItemType.NONE
    private var currentCountType: CountType = CountType.NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        wkId = intent.getIntExtra("wkID", -1)
        edit = intent.getIntExtra("edit", -1)
        crcId = intent.getIntExtra("crcID", -1)
        id = intent.getIntExtra("ID", -1)
        exercise = if(edit == 1) db.getExercise(id) else Exercise(-1, "", null, null)
        circuit = if(edit == 0) db.getCircuit(id) else Circuit()
        startName = if(exercise.id == -1) circuit.name else exercise.name

        val exerciseSpinner: Spinner = findViewById(R.id.spinnerExercise)

        val choicesArr = when(edit){
            -1 -> {
                if(crcId == -1) R.array.exercise_dropdown_choices
                else R.array.exercise_dropdown_choices_exercise
            }
            0 -> R.array.exercise_dropdown_choices_circuit
            1 -> if(crcId == -1) R.array.exercise_dropdown_choices
                else R.array.exercise_dropdown_choices_exercise
            else -> -1
        }
        ArrayAdapter.createFromResource(this, choicesArr, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            exerciseSpinner.adapter = adapter
        }
        spinnerExercise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent?.getItemAtPosition(position)
                when(item.toString()){
                    "Exercise" -> {
                        currentItemType = ItemType.EXERCISE
                        btnReps.isClickable = true
                        btnReps.alpha = 1f
                        btnTime.isClickable = true
                        btnTime.alpha = 1f
                    }
                    "Circuit" -> {
                        currentItemType = ItemType.CIRCUIT
                        btnReps.callOnClick()
                        btnReps.isClickable = false
                        btnReps.alpha = 1f
                        btnTime.isClickable = false
                        btnTime.alpha = .3f
                        txtPrompt.text = getString(R.string.repetitions_string)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        btnReps.setOnClickListener{
            currentCountType = CountType.REPS
            txtPrompt.text = getString(R.string.repetitions_string)
            btnReps.isSelected = true
            btnTime.isSelected = false
        }
        btnTime.setOnClickListener{
            currentCountType = CountType.SECS
            txtPrompt.text = getString(R.string.seconds_string)
            btnTime.isSelected = true
            btnReps.isSelected = false
        }

        btnSave.setOnClickListener{
            when(currentItemType){
                ItemType.EXERCISE -> {
                    Log.d(TAG, "btnSave | crcId = $crcId")
                    if(edit == -1) {
                        Log.d(
                            TAG,
                            "btnSave.setOnClickListener() ItemType.EXERCISE called | wkID = $wkId"
                        )
                        val exc = Exercise(
                            db.getAvailableId(db.TABLE_EXERCISES),
                            editName.text.toString(),
                            if (currentCountType == CountType.REPS) editNumber.text.toString().toInt() else null,
                            if (currentCountType == CountType.SECS) editNumber.text.toString().toLong() else null
                        )
                        Log.d(
                            TAG,
                            "Item: " + exc.id + " | " + exc.name + " | " + exc.reps.toString() + " | " + exc.time.toString()
                        )
                        db.addExercise(exc)
                        if(crcId == -1){
                            db.connectItem("Workout", wkId, "Exercise", exc.id)
                        } else {
                            db.connectItem("Circuit", crcId, "Exercise", exc.id)
                        }
                    } else if (edit == 1){
                        exercise.name = editName.text.toString()
                        if(currentCountType == CountType.REPS){
                            exercise.reps = editNumber.text.toString().toInt()
                            exercise.time = null
                        } else {
                            exercise.time = editNumber.text.toString().toLong()
                            exercise.reps = null
                        }
                        db.updateExercise(exercise)
                    }
                }
                ItemType.CIRCUIT ->{
                    if(edit == -1){
                        val circuit = Circuit( db.getAvailableId(db.TABLE_CIRCUITS), editName.text.toString(), editNumber.text.toString().toInt())
                        db.addCircuit(circuit)
                        db.connectItem("Workout", wkId, "Circuit", circuit.id)
                    } else if(edit == 0){
                        circuit.name = editName.text.toString()
                        exercise.reps = editNumber.text.toString().toInt()
                        db.updateCircuit(circuit)
                    }
                }
                ItemType.NONE ->{
                    Toast.makeText(this, "Item not created: ItemType NONE", Toast.LENGTH_SHORT).show()
                }
            }
            startBackActivity()
        }

        if(edit == 1){
            startItemType = ItemType.EXERCISE
            editName.setText(exercise.name)
            if(exercise.reps != null && exercise.reps != 0){
                startCountType = CountType.REPS
                editNumber.setText(exercise.reps.toString())
                btnReps.callOnClick()
            } else {
                startCountType = CountType.SECS
                editNumber.setText(exercise.time.toString())
                btnTime.callOnClick()
            }
        } else if(edit == 0){
            startItemType = ItemType.CIRCUIT
            startCountType = CountType.REPS
            editName.setText(circuit.name)
            editNumber.setText(circuit.reps.toString())
        } else {
            btnReps.callOnClick()
        }
        startNumber = editNumber.text.toString()
        spinnerExercise.setSelection(0)
    }

    override fun onBackPressed() {
        Log.d(TAG, "onBackPressed()")
        if(startItemType != currentItemType || startCountType != currentCountType || startName != editName.text.toString() || startNumber != editNumber.text.toString()){
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.save_changes))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes_string)){ dialog, _ -> dialog.dismiss();btnSave.callOnClick()}
                .setNegativeButton(getString(R.string.no_string)){ dialog, _ -> dialog.dismiss();startBackActivity()}
                .create()
                .show()
            Log.d(TAG, "onBackPressed() Dialog.Show()")
        } else {
            startBackActivity()
        }
    }

    private fun startBackActivity(){
        Log.d(TAG, "startBackActivity()")
        this.startActivity(Intent(this, DisplayExercisesActivity::class.java).apply{
            putExtra("wkID", wkId)
            putExtra("crcID", crcId)
        })
    }
}
