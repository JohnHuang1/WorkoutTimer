package com.example.workouttimer

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class ExerciseRVAdapter(private val items: MutableList<WorkoutItem>, private val context: Context): RecyclerView.Adapter<ExerciseViewHolder>(){

    override fun getItemCount() : Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ExerciseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ExerciseViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val item = items[position]
        holder.txtName?.text = item.name
        if(item::class == Exercise::class){
            item as Exercise
            holder.txtDescription?.text = when(item.reps != null){
                true -> "${item.reps} reps"
                false -> "${item.time} sec"
            }
            if(item.circuitID != null){
                holder.txtIndent?.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }
        } else {
            item as Circuit
            holder.txtDescription?.text = "${item.reps} reps"
            holder.txtDescription?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_arrow, 0)
        }
    }

}
class ExerciseViewHolder(inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(inflater.inflate(R.layout.exercise_list_item, parent, false)){
    var txtName: TextView? = null
    var txtDescription: TextView? = null
    var txtIndent: TextView? = null
    init{
        txtName = itemView.findViewById(R.id.txtName)
        txtDescription = itemView.findViewById(R.id.txtDescription)
        txtIndent = itemView.findViewById(R.id.txtIndent)
    }
}