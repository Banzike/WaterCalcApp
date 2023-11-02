package android.example.myapplication002.screens.title



import android.app.Application
import android.example.myapplication002.MainActivityViewModel
import android.example.myapplication002.screens.database.Reservoir
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.UserDatabaseDao
import android.example.myapplication002.screens.database.User
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import timber.log.Timber

class TitleViewModel(val databaseReserve: ReservoirDatabaseDao,val databaUser: UserDatabaseDao,application: Application) :AndroidViewModel(application)
{
    val viewModelJob=Job()
    val uiScope= CoroutineScope(Dispatchers.Main+ Job())
    lateinit var currentUser: User

    init {
       // createUser()
        getcurrentUser()
        Timber.i("Initializing...")
    }

    private fun getcurrentUser() {
        uiScope.launch{
           var user= executegetCurrentUser()
           currentUser=user
        }
    }

    private suspend fun executegetCurrentUser():User {
       return withContext(Dispatchers.IO) {
            val user = databaUser.getLastLoggedInUser()
            Timber.i("executegetuser")
            user
        }
    }
}

