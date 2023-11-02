package android.example.myapplication002.screens.showMap




import android.app.Application
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ShowMapViewModelFactory(
    private val dataSource: ReservoirDatabaseDao,
    private val databaseSource: UserDatabaseDao,
    private val application: Application):ViewModelProvider.Factory {
    @Suppress("unchecked cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowMapViewModel::class.java)){
            return ShowMapViewModel(dataSource,databaseSource,application)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}