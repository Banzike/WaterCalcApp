package com.waterreserve.myapplication002.screens.insertwaterlevel

import android.app.Application
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import com.waterreserve.myapplication002.screens.well.insertDiameter.InsertDiameterViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider




class WaterLevelViewModelFactory(
    private val dataSource: ReservoirDatabaseDao,
    private val dataSourceUser: UserDatabaseDao,
    private val dataSourceMeasurement: MeasurementDatabaseDao,
    private val application: Application
): ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterLevelViewModel::class.java)){
            return WaterLevelViewModel(dataSource,dataSourceUser,dataSourceMeasurement,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}