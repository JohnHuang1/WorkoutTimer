package com.example.workouttimer


class Workout{

    var id: Int = 0
    var name: String
    var display_id: Int = 0
    constructor(name: String, id: Int = 0, display_id: Int = 0){
        this.id = id
        this.name = name
        this.display_id = display_id
    }
}

open class WorkoutItem(var name: String, var id: Int = 0, var displayID: Int? = null)

class Exercise: WorkoutItem{
    var reps: Int? = null
    var time: Long? = null
    var circuitID: Int? = null

    constructor(id: Int, name: String, reps: Int?, time: Long?, displayID: Int = 0, circuitID: Int? = null): super(name, id, displayID){
        this.reps = reps
        this.time = time
        this.circuitID = circuitID
    }
}

class Circuit: WorkoutItem{

    var reps: Int = 0
    var excList: MutableList<Exercise> = ArrayList()
    var exerciseCount: Int = excList.size
    constructor(id: Int, name: String = "", reps: Int, displayID: Int? = null): super(name, id, displayID){
        this.id = id
        this.name = name
        this.reps = reps
    }

    fun setExerciseList(newList: MutableList<Exercise>){
        excList.clear()
        excList.addAll(newList)
        exerciseCount = excList.size
    }
}