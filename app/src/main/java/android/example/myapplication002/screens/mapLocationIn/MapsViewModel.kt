package android.example.myapplication002.screens.mapLocationIn

import android.app.Application
import android.example.myapplication002.screens.database.Reservoir
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.User
import android.example.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*

class MapsViewModel(val database: ReservoirDatabaseDao,val databaseUser: UserDatabaseDao, application: Application):AndroidViewModel(application) {
    val viewModelJob= Job()
    val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)

    //var coordinates=LatLng(0.0,0.0)
    lateinit var currentUser:User
    lateinit var coordinates:LatLng

init {
    getcurrentUser()


}
    //getcurrentUser() boilerplate
    private fun getcurrentUser() {
        uiScope.launch{
            var user= executegetCurrentUser()
            currentUser=user
            initializeReserve()
        }
    }

    private suspend fun executegetCurrentUser():User {
        return withContext(Dispatchers.IO) {
            val user = databaseUser.getLastLoggedInUser()
            user
        }
    }
    //end of getcurrentUser()


//    private suspend fun getUser(){
//        withContext(Dispatchers.IO) {
//            thisUser=database.getLatestUser()
//
//        }
//    }
    private suspend fun getReservefromDatabase(): Reservoir?{
        return withContext(Dispatchers.IO){
            val reserve=database.getReserve()
            reserve
        }
    }

    private fun initializeReserve(){
        uiScope.launch{
         //   getUser()
            createReserve()

      //     getReservefromDatabase()
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
          //  database.update(reserve)
        }
    }


    fun updateCoordinates(){
        uiScope.launch {
            executeUpdateCoordinates()
        }
    }
    private suspend fun executeUpdateCoordinates(){
        withContext(Dispatchers.IO){
            val reserve=database.getReserve()
            reserve.locationX=coordinates.latitude
            reserve.locationY=coordinates.longitude
            database.update(reserve)
        }
    }

}