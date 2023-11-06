package com.waterreserve.myapplication002

import android.app.Application
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import com.waterreserve.myapplication002.MainActivityViewModel
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivityViewModelFactory(
    private val dataSource: ReservoirDatabaseDao,
    private val dataSourceUser: UserDatabaseDao,
    private val dataSourceMeasurement: MeasurementDatabaseDao,
    private val application: Application):ViewModelProvider.Factory{
        @Suppress("unchecked cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
                return MainActivityViewModel(dataSource,dataSourceUser,dataSourceMeasurement,application)  as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}