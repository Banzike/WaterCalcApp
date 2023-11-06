package com.waterreserve.myapplication002.screens.mapLocationIn



import android.app.Application
import com.waterreserve.myapplication002.screens.database.Reservoir
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.User
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import timber.log.Timber

class
MapsAutoViewModel(val database: ReservoirDatabaseDao, val databaseUser:UserDatabaseDao, application: Application):AndroidViewModel(application) {

    val viewModelJob=Job()
    val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)
    var coordinateLat:Double=0.0
    var coordinateLong:Double=0.0
    var updatedCoordsFlag=MutableLiveData<Boolean>(false)
    lateinit var currentUser: User

    init {
        getCurrentUser()
    }

    private fun initializeReserve(){
        uiScope.launch{
            getCurrentUser()

        }
    }
    private  fun getCurrentUser(){
        uiScope.launch {
            currentUser=executegetCurrentUser()
            createReserve()
        }
    }

    private suspend fun executegetCurrentUser():User{
      return withContext(Dispatchers.IO) {
           val user= databaseUser.getLastLoggedInUser()
          user
        }

    }

    //Creates the reserve
    private suspend fun createReserve(){
        withContext(Dispatchers.IO) {
            val reserve = Reservoir()
            reserve.user=currentUser.userId
            reserve.userusername=currentUser.userName
            reserve.userDateJoined=currentUser.dateJoined
            database.insert(reserve)
            Timber.i("Reserve created")
        }
    }

    //Stores the coordinates found in the fragment
    fun storeCoordinates(){
        uiScope.launch {
            executeStoreCoordinates()
            updatedCoordsFlag.value=true
            val isit=updatedCoordsFlag.value
            Timber.i("flag is $isit")
        }
    }

    private suspend fun executeStoreCoordinates(){
        withContext(Dispatchers.IO){
            val reserve=database.getReserve()
            reserve.locationX= coordinateLat
            reserve.locationY= coordinateLong
            reserve.lastUpdated=System.currentTimeMillis()
            database.update(reserve)
        }
    }
}