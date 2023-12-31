package com.waterreserve.myapplication002.screens.droplet



import android.app.Application
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class DropLetViewModelFactory(
    private val dataSource: ReservoirDatabaseDao,
    private val dataSourceUser: UserDatabaseDao,
    private val dataSourceMeasurement:MeasurementDatabaseDao,
    private val application: Application
): ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DropletViewModel::class.java)){
            return DropletViewModel(dataSource,dataSourceUser,dataSourceMeasurement,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}