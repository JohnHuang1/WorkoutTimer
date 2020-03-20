package com.example.workouttimer


class Workout{

    var id: Int
    var name: String
    var displayID: Int
    constructor(name: String, id: Int = -1, display_id: Int = -1){
        this.id = id
        this.name = name
        this.displayID = display_id
    }
}

open class WorkoutItem(var name: String, var id: Int = -1, var displayID: Int = -1)

class Exercise: WorkoutItem{
    var reps: Int? = null
    var time: Long? = null
    var circuitID: Int = -1

    constructor(id: Int = -1, name: String = "", reps: Int? = null, time: Long? = null, displayID: Int = -1, circuitID: Int = -1): super(name, id, displayID){
        this.reps = reps
        this.time = time
        this.circuitID = circuitID
    }

    override fun toString(): String {
        return "ID: $id | name: $name | reps: $reps | time: $time | displayID: $displayID | circuitID: $circuitID"
    }
}

class Circuit: WorkoutItem{

    var reps: Int = 0
    var excList: MutableList<WorkoutItem> = ArrayList()
    var exerciseCount: Int = excList.size
    constructor(id: Int = -1, name: String = "", reps: Int = -1, displayID: Int = -1): super(name, id, displayID){
        this.id = id
        this.name = name
        this.reps = reps
    }

    fun setExerciseList(newList: MutableList<WorkoutItem>){
        excList.clear()
        excList.addAll(newList)
        exerciseCount = excList.size
    }
}