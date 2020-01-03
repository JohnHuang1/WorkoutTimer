package com.example.workouttimer

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

private const val TAG = "ExerciseRVAdapter"

class ExerciseRVAdapter(private val items: MutableList<WorkoutItem>, private val context: Context, private val wkID: Int): RecyclerView.Adapter<ExerciseViewHolder>(){

    override fun getItemCount() : Int = items.size
    private val db = DataBaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ExerciseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ExerciseViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val item = items[position]
        holder.txtName?.text = item.name
        Log.d(TAG, "Item class = " + item::class)
        if(item::class == Exercise::class){
            Log.d(TAG, "Item identified as an Exercise")
            item as Exercise
            holder.txtDescription?.text = if(item.reps != null && item.reps != 0) "${item.reps} reps" else "${item.time} sec"
            holder.exerciseLL?.setOnClickListener{
                context.startActivity(Intent(context, AddExerciseActivity::class.java).apply{
                    putExtra("edit", 1)
                    putExtra("wkID", wkID)
                    putExtra("crcID", item.circuitID)
                    putExtra("ID", item.id)
                })
            }

        } else {
            item as Circuit
            Log.d(TAG, "Item identified as a Circuit")
            holder.txtDescription?.text = "${item.reps} reps"
            holder.txtDescription?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0)
            holder.exerciseLL?.setOnClickListener{
                context.startActivity(Intent(context, DisplayExercisesActivity::class.java).apply{
                    putExtra("wkID", wkID)
                    putExtra("crcID", item.id)
                })
            }
        }

        holder.exerciseLL?.setOnLongClickListener{
            val msg = if(item::class == Exercise::class){
                deleteItem(item as Exercise, null)
            } else {
                deleteItem(null, item as Circuit)
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun deleteItem(exercise: Exercise?, circuit: Circuit?): String{
        val newList: MutableList<WorkoutItem> = mutableListOf()
        newList.addAll(items)
        newList.remove((exercise ?: circuit) as WorkoutItem)
        val diffUtilCallback = DiffUtilCallback(items as MutableList<Any>, newList as MutableList<Any>)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
        items.remove((exercise ?: circuit) as WorkoutItem)
        Log.d(TAG, "deleteItem() exercise.circuitID = ${exercise?.circuitID}")
        val deleted = when(exercise != null){
            true -> db.deleteExercise(exercise, if(exercise.circuitID == -1) wkID else null, exercise.circuitID)
            false -> if(circuit != null) db.deleteCircuit(circuit, wkID) else 0
        }
        diffResult.dispatchUpdatesTo(this)
        return "$deleted ${if(exercise != null) "exercise" else "circuit"} deleted"
    }

}
class ExerciseViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.exercise_list_item, parent, false)){
    var txtName: TextView? = null
    var txtDescription: TextView? = null
    var exerciseLL: LinearLayout? = null
    init{
        txtName = itemView.findViewById(R.id.txtName)
        txtDescription = itemView.findViewById(R.id.txtDescription)
        exerciseLL = itemView.findViewById(R.id.exerciseLinearLayout)
    }
}