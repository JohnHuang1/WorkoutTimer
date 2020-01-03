package com.example.workouttimer

import android.content.Context
import android.content.Intent
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

private val TAG: String = WorkoutListRVAdapter::class.java.simpleName

class WorkoutListRVAdapter(private val items : MutableList<Workout>, private val context: Context) : RecyclerView.Adapter<WorkoutViewHolder>() {

    override fun getItemCount() : Int = items.size
    private val db = DataBaseHandler(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WorkoutViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val item = items[position]
        holder.mName?.text = item.name
        holder.mName?.setOnClickListener {openWorkoutActivity(item)}
        holder.mName?.setOnLongClickListener{
            val result = deleteItem(item)
            Toast.makeText(context, "$result Rows Deleted", Toast.LENGTH_SHORT).show()

            true
        }
    }

    private fun openWorkoutActivity(item: Workout){
        Log.d(TAG, "OpenWorkoutActivity() wkID = ${item.id}")
        context.startActivity(Intent(context, DisplayExercisesActivity::class.java).apply {
            putExtra("wkID", item.id)
        })
    }

    fun insertItem(wk: Workout){
        val newList: MutableList<Workout> = ArrayList()
        newList.addAll(items)
        newList.add(wk)
        val diffUtilCallback = DiffUtilCallback(items as MutableList<Any>, newList as MutableList<Any>)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items.add(wk)
        db.addWorkoutToList(wk)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateItem(newList: MutableList<Workout>){
        val diffUtilCallback = DiffUtilCallback(items as MutableList<Any>, newList as MutableList<Any>)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items.clear()
        items.addAll(newList)
        db.updateWorkoutList(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun deleteItem(wk: Workout): Int{
        val newList: MutableList<Workout> = ArrayList()
        newList.addAll(items)
        newList.remove(wk)
        val diffUtilCallback = DiffUtilCallback(items as MutableList<Any>, newList as MutableList<Any>)
        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)

        items.remove(wk)
        val deleted = db.deleteWorkout(wk.id)
        diffResult.dispatchUpdatesTo(this)
        return deleted
    }


}

class WorkoutViewHolder (inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.name_list_item, parent, false)) {
    var mName: TextView? = null
    init{
        mName = itemView.findViewById(R.id.tv_name_item)
    }

}