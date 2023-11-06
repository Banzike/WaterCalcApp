package com.waterreserve.myapplication002.screens.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Reservoir::class,User::class,Measurement::class], version = 58, exportSchema = false) //,User::class,Measurement::class
abstract class ReservoirDatabase:RoomDatabase() {

    abstract val reservoirDatabaseDao: ReservoirDatabaseDao
    abstract val userDatabaseDao: UserDatabaseDao
    abstract val measurementDatabaseDao: MeasurementDatabaseDao

    companion object{

        @Volatile
        private var INSTANCE: ReservoirDatabase?=null

        fun getInstance(context: Context): ReservoirDatabase{
            synchronized(this){
                var instance= INSTANCE

                if(instance==null){
                    instance= Room.databaseBuilder(
                         context.applicationContext,
                         ReservoirDatabase::class.java,
                         "reservoir_database")
                         .fallbackToDestructiveMigration()
                        .build()
                }
                return instance
            }

        }
    }
}