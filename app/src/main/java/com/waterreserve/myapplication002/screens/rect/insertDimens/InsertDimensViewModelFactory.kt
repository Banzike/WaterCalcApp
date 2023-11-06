package com.waterreserve.myapplication002.screens.rect.insertDimens

import com.waterreserve.myapplication002.screens.rect.insertDimens.InsertDimensViewModel
import android.app.Application
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InsertDimensViewModelFactory(
    private val dataSource: ReservoirDatabaseDao,
    private val application: Application):ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InsertDimensViewModel::class.java)){
            return InsertDimensViewModel(dataSource,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}