package com.example.workouttimer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

private val TAG:String = DataBaseHandler::class.java.simpleName

class DataBaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        const val DATABASE_NAME = "WorkoutDB"
        const val DATABASE_VERSION = 2

    }

    val TABLE_WORKOUTS = "Workout_tbl"
    val TABLE_WK_CRC_EXC_BRIDGE = "Workout_Circuit_Exercise_Bridge_tbl"
    val TABLE_CRC_EXC_BRIDGE = "Circuit_Exercise_Bridge_tbl"
    val TABLE_EXERCISES = "Exercise_tbl"
    val TABLE_CIRCUITS = "Circuit_tbl"

    val COL_ID = "id"
    val COL_DISPLAY_ORDER = "display_order"
    val COL_NAME = "name"
    val COL_CIRCUIT_ID = "circuit_id"
    val COL_WORKOUT_ID = "workout_id"
    val COL_CRC_EXC_ID = "circuitOrExercise_id"
    val COL_CIRCUIT_OR_EXERCISE = "circuitOrExercise_bool" // 0 = Exercise | 1 = Circuit
    val COL_REPS = "reps"
    val COL_TIME = "time"
    val COL_EXERCISE_ID = "exercise_id"

    val createWorkoutTbl = "CREATE TABLE $TABLE_WORKOUTS"
    val workoutTblParams = "($COL_ID INTEGER PRIMARY KEY, " +
    "$COL_DISPLAY_ORDER INTEGER UNIQUE, " +
    "$COL_NAME VARCHAR(255))"

    val createWkCrcExcBridgeTbl = "CREATE TABLE $TABLE_WK_CRC_EXC_BRIDGE"
    val wkCrcExcBridgeTblParams = "($COL_WORKOUT_ID INTEGER, " +
    "$COL_CIRCUIT_OR_EXERCISE BOOLEAN NOT NULL, " +
    "$COL_CRC_EXC_ID INTEGER, " +
    "$COL_DISPLAY_ORDER INTEGER)"

    val createCrcExcBridgeTbl = "CREATE TABLE $TABLE_CRC_EXC_BRIDGE"
    val crcExcBridgeTblParams = "($COL_CIRCUIT_ID INTEGER, " +
    "$COL_EXERCISE_ID INTEGER, " +
    "$COL_DISPLAY_ORDER INTEGER)"

    val createCircuitTbl = "CREATE TABLE $TABLE_CIRCUITS"
    val circuitTblParams ="($COL_ID INTEGER PRIMARY KEY, " +
    "$COL_NAME VARCHAR(255), " +
    "$COL_REPS INTEGER)"

    val createExerciseTbl = "CREATE TABLE $TABLE_EXERCISES"
    val exerciseTblParams = "($COL_ID INTEGER PRIMARY KEY, " +
    "$COL_NAME VARCHAR(255), " +
    "$COL_REPS INTEGER, " +
    "$COL_TIME INTEGER)"

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(createWorkoutTbl + workoutTblParams)
        db?.execSQL(createWkCrcExcBridgeTbl + wkCrcExcBridgeTblParams)
        db?.execSQL(createCrcExcBridgeTbl + crcExcBridgeTblParams)
        db?.execSQL(createCircuitTbl + circuitTblParams)
        db?.execSQL(createExerciseTbl + exerciseTblParams)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade() called")
        val tblArray = arrayListOf(TABLE_WORKOUTS, TABLE_WK_CRC_EXC_BRIDGE, TABLE_CRC_EXC_BRIDGE, TABLE_CIRCUITS, TABLE_EXERCISES)
        for(tbl in tblArray){
            if(db != null) remakeTable(db, tbl)
        }

//        if(db != null){
//            val result = db?.rawQuery("SELECT name FROM sqlite_master", null)
//            if(result.moveToNext()){
//                do{
//                    val tblName = result.getString(result.getColumnIndex("name"))
//                    if(tblName != "android_metadata" && tblName != "sqlite_sequence"){
//                        db.execSQL("DROP TABLE IF EXISTS $tblName")
//                    }
//                } while(result.moveToNext())
//            }
//        }
//        onCreate(db)
    }

    private fun remakeTable(db: SQLiteDatabase, tblName: String){
        var columns = ""
        var params = ""
        when(tblName){
            TABLE_WORKOUTS ->{
                columns = "$COL_ID, $COL_DISPLAY_ORDER, $COL_NAME"
                params = workoutTblParams
            }
            TABLE_WK_CRC_EXC_BRIDGE -> {
                columns = "$COL_WORKOUT_ID, $COL_CIRCUIT_OR_EXERCISE, $COL_CRC_EXC_ID, $COL_DISPLAY_ORDER"
                params = wkCrcExcBridgeTblParams
            }
            TABLE_CRC_EXC_BRIDGE ->{
                columns = "$COL_CIRCUIT_ID, $COL_EXERCISE_ID, $COL_DISPLAY_ORDER"
                params = crcExcBridgeTblParams
            }
            TABLE_CIRCUITS ->{
                columns = "$COL_ID, $COL_NAME, $COL_REPS"
                params = circuitTblParams
            }
            TABLE_EXERCISES ->{
                columns = "$COL_ID, $COL_NAME, $COL_REPS, $COL_TIME"
                params = exerciseTblParams
            }
        }
        if(checkTableExists(db, tblName)){
            db.execSQL("CREATE TEMPORARY TABLE ${tblName + "_temp"} $params")
            db.execSQL("INSERT INTO ${tblName + "_temp"} SELECT $columns FROM $tblName")
            db.execSQL("DROP TABLE IF EXISTS $tblName")
            db.execSQL("CREATE TABLE $tblName $params")
            db.execSQL("INSERT INTO $tblName SELECT $columns FROM ${tblName + "_temp"}")
            db.execSQL("DROP TABLE IF EXISTS ${tblName + "_temp"}")
        } else {
            db.execSQL("CREATE TABLE $tblName $params")
        }

    }

    private fun checkTableExists(db: SQLiteDatabase, tblName: String): Boolean{
        val result = db.rawQuery("SELECT name FROM sqlite_master WHERE name= '$tblName'", null)
        val exists = result.moveToFirst()
        result.close()
        return exists
    }

    fun addWorkoutToList(wk: Workout){
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_NAME, wk.name)
        content.put(COL_ID, wk.id)
        content.put(COL_DISPLAY_ORDER, wk.displayID)
        db.insert(TABLE_WORKOUTS, null, content)
    }

    fun updateCircuit(crc: Circuit){
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_NAME, crc.name)
        content.put(COL_REPS, crc.reps)
        db.update(TABLE_CIRCUITS, content, "$COL_ID = ?", arrayOf(crc.id.toString()))
    }
    fun updateCircuit(crcID: Int, name: String): Int{
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_NAME, name)
        return db.update(TABLE_CIRCUITS, content, "$COL_ID = ?", arrayOf(crcID.toString()))
    }

    fun updateCircuitItemList(list: MutableList<WorkoutItem>, crcID: Int){
        val db = this.writableDatabase
        db.delete(TABLE_CRC_EXC_BRIDGE, "$COL_CIRCUIT_ID = $crcID", null)
        for(item in list){
            val content = ContentValues()
            content.put(COL_CIRCUIT_ID, crcID)
            content.put(COL_EXERCISE_ID, item.id)
            content.put(COL_DISPLAY_ORDER, item.displayID)
            db.insert(TABLE_CRC_EXC_BRIDGE, null, content)
        }
    }

    fun updateWorkout(wkID: Int, name: String): Int{
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_NAME, name)
        return db.update(TABLE_WORKOUTS, content, "$COL_ID = ?", arrayOf(wkID.toString()))
    }

    fun updateWorkoutList(wkList: MutableList<Workout>){
        val db = this.writableDatabase
        db.delete(TABLE_WORKOUTS, null, null)
        for(wk in wkList){
            val content = ContentValues()
            content.put(COL_NAME, wk.name)
            content.put(COL_ID, wk.id)
            content.put(COL_DISPLAY_ORDER, wk.displayID)
            db.insert(TABLE_WORKOUTS, null, content)
        }
    }

    fun updateWorkoutItemList(list: MutableList<WorkoutItem>, wkID: Int){
        val db = this.writableDatabase
        db.delete(TABLE_WK_CRC_EXC_BRIDGE, "$COL_WORKOUT_ID = $wkID", null)
        for(item in list){
            val content = ContentValues()
            content.put(COL_WORKOUT_ID, wkID)
            content.put(COL_CIRCUIT_OR_EXERCISE, if(item::class == Exercise::class) 0 else 1)
            content.put(COL_CRC_EXC_ID, item.id)
            content.put(COL_DISPLAY_ORDER, item.displayID)
            db.insert(TABLE_WK_CRC_EXC_BRIDGE, null, content)
        }
    }

    fun updateExercise(exc: Exercise){
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_ID, exc.id)
        content.put(COL_NAME, exc.name)
        content.put(COL_TIME, exc.time)
        content.put(COL_REPS, exc.reps)
        db.update(TABLE_EXERCISES, content, "$COL_ID = ${exc.id}", null)
    }

    fun getExercise(excID: Int): Exercise{
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM $TABLE_EXERCISES WHERE $COL_ID = $excID", null)
        val exercise = when(result.moveToFirst()){
            true -> Exercise(
                result.getInt(result.getColumnIndex(COL_ID)),
                result.getString(result.getColumnIndex(COL_NAME)),
                result.getInt(result.getColumnIndex(COL_REPS)),
                result.getLong(result.getColumnIndex(COL_TIME))
            )
            false -> Exercise(-1, "", null, null)
        }
        result.close()
        return exercise
    }

    fun getCircuit(crcID: Int): Circuit{
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM $TABLE_CIRCUITS WHERE $COL_ID = $crcID", null)
        val circuit = when(result.moveToFirst()){
            true -> Circuit(
                result.getInt(result.getColumnIndex(COL_ID)),
                result.getString(result.getColumnIndex(COL_NAME)),
                result.getInt(result.getColumnIndex(COL_REPS))
            )
            false -> Circuit()
        }
        result.close()
        val excList: MutableList<WorkoutItem> = mutableListOf()
        val resultList = db.rawQuery("SELECT * FROM $TABLE_CRC_EXC_BRIDGE WHERE $COL_CIRCUIT_ID = $crcID ORDER BY $COL_DISPLAY_ORDER", null)
        if(resultList.moveToFirst()){
            do{
                val id = resultList.getInt(resultList.getColumnIndex(COL_EXERCISE_ID))
                val exercise = getExercise(id) as Exercise
                exercise.displayID = resultList.getInt(resultList.getColumnIndex(COL_DISPLAY_ORDER))
                exercise.circuitID = circuit.id
                excList.add(exercise as WorkoutItem)
            } while(resultList.moveToNext())
        }
        resultList.close()
        circuit.setExerciseList(excList)
        return circuit
    }

    fun getWorkoutList(): MutableList<Workout>{
        val returnList: MutableList<Workout> = ArrayList()
        val db = this.readableDatabase
        val result = db.query(TABLE_WORKOUTS, null, null, null, null, null, COL_DISPLAY_ORDER, null)
        if(result.moveToFirst()){
            do{
                returnList.add(
                    Workout(
                        result.getString(result.getColumnIndex(COL_NAME)),
                        result.getInt(result.getColumnIndex(COL_ID)),
                        result.getInt(result.getColumnIndex(COL_DISPLAY_ORDER))
                    )
                )
            }while(result.moveToNext())
        }
        result.close()
        return returnList
    }

    fun deleteCircuit(crc: Circuit, wkID: Int): Int{
        val db = this.writableDatabase
        val deleteResult = db.delete(TABLE_CIRCUITS, "$COL_ID = ${crc.id}", null)
        db.delete(TABLE_WK_CRC_EXC_BRIDGE, "$COL_WORKOUT_ID = $wkID AND $COL_CRC_EXC_ID = ${crc.id}", null)
        db.delete(TABLE_CRC_EXC_BRIDGE, "$COL_CIRCUIT_ID = ${crc.id}", null)
        val list = getWorkoutItemList(wkID)
        for(item in list){
            if(item.displayID > crc.displayID){
                val content = ContentValues()
                content.put(COL_DISPLAY_ORDER, item.displayID - 1)
                db.update(TABLE_WK_CRC_EXC_BRIDGE, content, "$COL_WORKOUT_ID = $wkID AND $COL_CRC_EXC_ID = ${item.id}", null)
            }
        }
        return deleteResult
    }

    fun deleteExercise(exc: Exercise, wkId: Int?, crcId: Int = -1): Int{
        val db = this.writableDatabase
        var deleteResult = db.delete(TABLE_EXERCISES, "$COL_ID = ${exc.id}", null)
        if(wkId != null){
            deleteResult += db.delete(TABLE_WK_CRC_EXC_BRIDGE, "$COL_WORKOUT_ID = $wkId AND $COL_CRC_EXC_ID = ${exc.id}", null)
            Log.d(TAG, "Deleted Exercise ${exc.id} from Workout $wkId")
        } else {
            deleteResult += db.delete(TABLE_CRC_EXC_BRIDGE, "$COL_CIRCUIT_ID = $crcId AND $COL_EXERCISE_ID = ${exc.id}", null)
            Log.d(TAG, "Deleted Exercise ${exc.id} from Circuit $crcId")
        }
        val list: MutableList<WorkoutItem> = if(wkId != null) getWorkoutItemList(wkId) else getCircuit(crcId).excList
        for(item in list){
            if(item.displayID > exc.displayID){
                val content = ContentValues()
                content.put(COL_DISPLAY_ORDER, item.displayID - 1)
                if(wkId != null) {
                    db.update(TABLE_WK_CRC_EXC_BRIDGE, content, "$COL_WORKOUT_ID = $wkId AND $COL_CRC_EXC_ID = ${item.id}", null)
                    Log.d(TAG, "updated item ${item.id} from Workout $wkId")
                } else {
                    db.update(TABLE_CRC_EXC_BRIDGE, content, "$COL_CIRCUIT_ID = $crcId AND $COL_EXERCISE_ID = ${item.id}", null)
                    Log.d(TAG, "updated item ${item.id} from Workout $crcId")
                }
            }
        }
        return deleteResult
    }

    fun deleteWorkout(wkID: Int): Int{
        val db = this.writableDatabase
        val deleteResult = db.delete(TABLE_WORKOUTS, "$COL_ID = ${wkID}", null)
        val list: MutableList<Workout> = getWorkoutList()
        var counter = 0
        for(item in list){
            item.displayID = counter++
        }
        updateWorkoutList(list)
        return deleteResult
    }

    fun getAvailableId(tblName: String): Int{
        val wkIDList = getIDArray(COL_ID, tblName)
        var counter: Int = -1
        while(wkIDList.contains(++counter)){}
        return counter
    }

    fun getIDArray(colName: String, tblName: String, whereClause: String = "", colSort: String = ""): MutableList<Int>{
        val temp: ArrayList<Int> = ArrayList()
        val db = this.readableDatabase
        val result: Cursor
        result = when(whereClause != ""){
            true -> db.rawQuery("SELECT $colName FROM $tblName WHERE $whereClause ORDER BY $colSort", null)
            false -> when(colSort != ""){
                true -> db.rawQuery("SELECT $colName FROM $tblName ORDER BY $colSort", null)
                false -> db.rawQuery("SELECT $colName FROM $tblName", null)
            }
        }
        if(result.moveToFirst()){
            do{
                temp.add(result.getInt(result.getColumnIndex(colName)))
            } while(result.moveToNext())
        }
        result.close()
        return temp
    }

    fun getNextWorkoutsDisplayId(): Int{
        val temp = getIDArray(COL_DISPLAY_ORDER, TABLE_WORKOUTS,"",  COL_DISPLAY_ORDER)
        return when(temp.size <= 0){
            true -> 0
            false -> temp[temp.size - 1] + 1
        }
    }

    fun getNextWorkoutItemDisplayID(wkID: Int): Int{
        val temp = getIDArray(COL_DISPLAY_ORDER, TABLE_WK_CRC_EXC_BRIDGE,"$COL_WORKOUT_ID = $wkID",  COL_DISPLAY_ORDER)
        return when(temp.size <= 0){
            true -> 0
            false -> temp[temp.size - 1] + 1
        }
    }

    fun getNextCircuitItemDisplayID(crcID: Int): Int{
        val temp = getIDArray(COL_DISPLAY_ORDER, TABLE_CRC_EXC_BRIDGE,"$COL_CIRCUIT_ID = $crcID",  COL_DISPLAY_ORDER)
        return when(temp.size <= 0){
            true -> 0
            false -> temp[temp.size - 1] + 1
        }
    }

    fun getWorkoutName(id: Int): String{
        val db = this.readableDatabase
        val result = db.query(TABLE_WORKOUTS, Array(1){COL_NAME}, "$COL_ID = $id", null, null, null, null, null)
        val name = when(result.moveToFirst()){
            true -> result.getString(result.getColumnIndex(COL_NAME))
            false ->""
        }
        result.close()
        return name
    }

    fun addExercise(exc: Exercise){
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_ID, exc.id)
        content.put(COL_NAME, exc.name)
        content.put(COL_TIME, exc.time)
        content.put(COL_REPS, exc.reps)
        db.insert(TABLE_EXERCISES, null, content)
    }

    fun addCircuit(crc: Circuit){
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(COL_ID, crc.id)
        content.put(COL_NAME, crc.name)
        content.put(COL_REPS, crc.reps)
        db.insert(TABLE_CIRCUITS, null, content)
    }

    fun connectItem(primary: String, pID: Int, secondary: String, sID: Int){
        val db = this.writableDatabase
        val content = ContentValues()
        when(primary){
            "Workout" ->{
                content.put(COL_CIRCUIT_OR_EXERCISE, if(secondary == "Circuit") 1 else 0)
                content.put(COL_WORKOUT_ID, pID)
                content.put(COL_CRC_EXC_ID, sID)
                content.put(COL_DISPLAY_ORDER, getNextWorkoutItemDisplayID(pID))
                db.insert(TABLE_WK_CRC_EXC_BRIDGE, null, content)
                Log.d(TAG, "connectItem() Workout $pID Connected to $secondary id = $sID")
            }
            "Circuit" ->{
                content.put(COL_CIRCUIT_ID, pID)
                content.put(COL_EXERCISE_ID, sID)
                content.put(COL_DISPLAY_ORDER, getNextCircuitItemDisplayID(pID))
                db.insert(TABLE_CRC_EXC_BRIDGE, null, content)
                Log.d(TAG, "connectItem() Circuit $pID Connected to $secondary id = $sID")
            }
        }
    }

    fun getWorkoutItemList(wkId: Int): MutableList<WorkoutItem>{
        val returnList: MutableList<WorkoutItem> = mutableListOf()
        val db = this.readableDatabase
        val result = db.query(TABLE_WK_CRC_EXC_BRIDGE, null, "$COL_WORKOUT_ID = $wkId", null, null, null, COL_DISPLAY_ORDER)
        if(result.moveToFirst()){
            do{
                val id = result.getInt(result.getColumnIndex(COL_CRC_EXC_ID))
//                val item = when(result.getInt(result.getColumnIndex(COL_CIRCUIT_OR_EXERCISE))){
//                    0 -> getExercise(id)
//                    1 -> getCircuit(id)
//                    else -> throw Exception("BOOLEAN NOT RIGHT")
//                }
                var item: WorkoutItem
                when(result.getInt(result.getColumnIndex(COL_CIRCUIT_OR_EXERCISE))){
                    0 -> {
                        item = getExercise(id)
                        Log.d(TAG, "getWorkoutItemList() Exercise Retrieved id: ${item.id} name: ${item.name} reps: ${item.reps} time: ${item.time}")
                    }
                    1 -> {
                        item = getCircuit(id)
                        Log.d(TAG, "getWorkoutItemList() Circuit Retrieved")
                    }
                    else -> throw Exception("BOOLEAN NOT RIGHT")
                }
                item.displayID = result.getInt(result.getColumnIndex(COL_DISPLAY_ORDER))
                returnList.add(item)
            }while(result.moveToNext())
        }
        result.close()
        Log.d("DataBaseHandler", "getWorkoutItemList() Called")
        return returnList
    }


}