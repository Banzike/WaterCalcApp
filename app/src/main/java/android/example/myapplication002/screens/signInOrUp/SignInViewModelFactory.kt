package android.example.myapplication002.screens.signInOrUp

import android.app.Application
import android.example.myapplication002.screens.database.MeasurementDatabaseDao
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SignInViewModelFactory(
    private val dataSourceReservoir: ReservoirDatabaseDao,
    private val dataSourceUser: UserDatabaseDao,
    private val dataSourceMeasurement: MeasurementDatabaseDao,
    private val application: Application):ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)){
            return SignInViewModel(dataSourceReservoir,dataSourceUser,dataSourceMeasurement,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}