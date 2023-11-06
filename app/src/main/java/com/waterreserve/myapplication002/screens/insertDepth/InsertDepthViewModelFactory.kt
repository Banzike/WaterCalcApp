package com.waterreserve.myapplication002.screens.insertDepth

import com.waterreserve.myapplication002.screens.insertDepth.InsertDepthViewModel




import android.app.Application
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider



class InsertDepthViewModelFactory (
    private val dataSource: ReservoirDatabaseDao,
    private val application: Application
):ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsertDepthViewModel::class.java)){
            return InsertDepthViewModel(dataSource,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}