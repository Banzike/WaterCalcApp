package android.example.myapplication002.screens.user

import android.app.Application
import android.example.myapplication002.screens.database.MeasurementDatabaseDao
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserViewModelFactory(
    private val dataSourceReservoir: ReservoirDatabaseDao,
    private val dataSourceUser: UserDatabaseDao,
    private val dataSourceMeasurement:MeasurementDatabaseDao,
    private val application: Application):ViewModelProvider.Factory {
        @Suppress("unchecked cast")
        override fun <T: ViewModel> create(modelClass:Class<T>):T{
            if(modelClass.isAssignableFrom(UserViewModel::class.java)){
                return UserViewModel(dataSourceReservoir,dataSourceUser,dataSourceMeasurement,application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}