package android.example.myapplication002.screens.mapLocationIn

import android.app.Application
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MapsViewModelFactory(
    private val dataSource: ReservoirDatabaseDao,
    private val dataSourceUser: UserDatabaseDao,
    private val application: Application):ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)){
            return MapsViewModel(dataSource,dataSourceUser,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}