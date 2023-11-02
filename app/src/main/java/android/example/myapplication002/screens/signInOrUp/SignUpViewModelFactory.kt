package android.example.myapplication002.screens.signInOrUp

import android.app.Application
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SignUpViewModelFactory(
    private val dataSourceReservoir: ReservoirDatabaseDao,
    private val dataSourceUser: UserDatabaseDao,
    private val application: Application):ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)){
            return SignUpViewModel(dataSourceReservoir,dataSourceUser,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}