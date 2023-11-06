package com.waterreserve.myapplication002.screens.well.insertDiameter

import android.app.Application
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider



class InsertDiameterViewModelFactory(
    private val dataSource: ReservoirDatabaseDao,
    private val application: Application
):ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsertDiameterViewModel::class.java)){
            return InsertDiameterViewModel(dataSource,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}