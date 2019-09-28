package com.example.workouttimer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_add_exercise.*

private const val TAG: String = "AddExerciseActivity:"

class AddExerciseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        val exerciseSpinner: Spinner = findViewById(R.id.spinnerExercise)

        ArrayAdapter.createFromResource(this, R.array.exercise_dropdown_choices, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            exerciseSpinner.adapter = adapter
        }
        spinnerExercise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val item = parent?.getItemAtPosition(position)
                when(item.toString()){
                    "Exercise" -> {
                        btnReps.isClickable = true
                        btnReps.alpha = 1f
                        btnTime.isClickable = true
                        btnTime.alpha = 1f
                    }
                    "Circuit" -> {
                        btnReps.callOnClick()
                        btnReps.isClickable = false
                        btnReps.alpha = 1f
                        btnTime.isClickable = false
                        btnTime.alpha = .3f
                        txtPrompt.text = getString(R.string.repetitions_string)
                    }
                    "Rest" -> {
                        btnTime.callOnClick()
                        btnTime.isClickable = false
                        btnTime.alpha = 1f
                        btnReps.isClickable = false
                        btnReps.alpha = .3f
                        txtPrompt.text = getString(R.string.seconds_string)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        btnReps.setOnClickListener{
            txtPrompt.text = getString(R.string.repetitions_string)
            btnReps.isSelected = true
            btnTime.isSelected = false
        }
        btnTime.setOnClickListener{
            txtPrompt.text = getString(R.string.seconds_string)
            btnTime.isSelected = true
            btnReps.isSelected = false
        }

        btnSave.setOnClickListener{

        }

        btnReps.callOnClick()
    }
}
