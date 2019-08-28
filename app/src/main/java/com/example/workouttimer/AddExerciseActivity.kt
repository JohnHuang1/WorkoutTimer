package com.example.workouttimer

import android.app.Activity
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_add_exercise.*

class AddExerciseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        val exerciseSpinner: Spinner = findViewById(R.id.spinnerExercise)
        val repsButton: Button = findViewById(R.id.btnReps)
        val timeButton: Button = findViewById(R.id.btnTime)
        val txtPrompt: TextView = findViewById(R.id.txtPrompt)

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
                        btnReps.isPressed = true
                        btnReps.alpha = 1f
                        btnTime.isClickable = true
                        btnTime.alpha = 1f
                        txtPrompt.text = resources.getString(R.string.repetitions_string)
                    }
                    "Circuit" -> {
                        btnReps.isClickable = false
                        btnReps.isPressed = true
                        btnReps.alpha = 1f
                        btnTime.isClickable = false
                        btnTime.isPressed = false
                        btnTime.alpha = .3f
                        txtPrompt.text = resources.getString(R.string.repetitions_string)
                    }
                    "Rest" -> {
                        btnTime.isClickable = false
                        btnTime.isPressed = true
                        btnTime.alpha = 1f
                        btnReps.isClickable = false
                        btnReps.isPressed = false
                        btnReps.alpha = .3f
                        txtPrompt.text = resources.getString(R.string.seconds_string)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        repsButton.setOnClickListener{
            btnReps.isClickable = false
            btnReps.isPressed = true
            btnTime.isClickable = true
            btnTime.isPressed = false
            txtPrompt.text = getString(R.string.repetitions_string)
        }
        timeButton.setOnClickListener{
            btnTime.isClickable = false
            btnTime.isPressed = true
            btnReps.isClickable = true
            btnReps.isPressed = false
            txtPrompt.text = getString(R.string.seconds_string)
        }
    }
}