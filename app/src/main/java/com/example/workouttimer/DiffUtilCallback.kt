package com.example.workouttimer

import android.support.v7.util.DiffUtil

class DiffUtilCallback(private val oldList: MutableList<Workout>, private val newList: MutableList<Workout>): DiffUtil.Callback(){
    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldPosition == newPosition
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition] == newList[newPosition]
    }

}