package com.waterreserve.myapplication002.screens.database

import android.widget.RemoteViews.RemoteResponse
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy

@Dao
interface ReservoirDatabaseDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reserve:Reservoir)

    @Update
    suspend fun update(reserve: Reservoir)

    @Query("SELECT * from reservoir_table WHERE reservoirId= :key")
    suspend fun get(key:Long):Reservoir

    @Query("DELETE FROM reservoir_table")
    suspend fun clear()

    @Query("DELETE from reservoir_table WHERE reservoirId=:key")
    suspend fun deleteThis(key:Long)

    @Query("SELECT * from reservoir_table ORDER BY reservoirId DESC")
     fun getMyReservesDESCid():LiveData<List<Reservoir>>

    @Query("SELECT * from reservoir_table ORDER BY reservoirId ASC")
     fun getMyReservesASCid(): LiveData<List<Reservoir>>

    @Query("SELECT * from reservoir_table ORDER BY creationDate DESC")
     fun getMyReservesDESCdate(): LiveData<List<Reservoir>>

    @Query("SELECT * from reservoir_table ORDER BY creationDate ASC")
     fun getMyReservesASCdate(): LiveData<List<Reservoir>>

    @Query("SELECT * FROM reservoir_table ORDER BY creationDate DESC LIMIT 1")
    suspend fun getReserve():Reservoir

    @Query("SELECT * FROM reservoir_table ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getReserveUpdated():Reservoir

    @Query("SELECT COUNT(reservoirId) FROM reservoir_table")
    fun getRowCount():Int

    @Query("SELECT imageColour from reservoir_table ORDER BY creationDate DESC")
    fun getColor():MutableList<String>

    @Query("SELECT reservoirId from reservoir_table ORDER BY creationDate DESC")
    fun getIDs():List<Long>

    @Query("SELECT locationX from reservoir_table ORDER BY creationDate DESC")
    fun getLat():List<Double>

    @Query("SELECT locationY from reservoir_table ORDER BY creationDate DESC")
    fun getLon():List<Double>

    @Query("SELECT depth from reservoir_table ORDER BY creationDate DESC")
    fun getDepth():List<Double>

    @Query("SELECT user from reservoir_table ORDER BY creationDate DESC")
    fun getUsersByCreationDate():List<Long>

    @Query("SELECT * from reservoir_table")
    fun getAllReserves():List<Reservoir>

    @Query("SELECT * from reservoir_table where userusername=:key")//maybe user date joined
    fun getReservesOfUser(key: String):List<Reservoir>

    @Query("DELETE FROM reservoir_table where user=:key")
    fun deleteReservesOfUser(key:Long)

    @Query("SELECT * FROM reservoir_table nextUpdateDue WHERE userusername=:key ORDER BY nextUpdateDue ASC LIMIT 1")
    fun getNextToUpdate(key: String): Reservoir
}


