package android.example.myapplication002.screens.well.insertDiameter

import android.app.Application
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
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