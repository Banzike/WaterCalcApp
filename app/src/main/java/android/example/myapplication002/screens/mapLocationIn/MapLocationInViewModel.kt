package android.example.myapplication002.screens.mapLocationIn

import android.app.Application
import android.example.myapplication002.formatReserves
import android.example.myapplication002.screens.database.Reservoir
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.UserDatabaseDao
import android.example.myapplication002.screens.database.User
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
//import androidx.lifecycle.Transformations
import timber.log.Timber


class MapLocationInViewModel(val database: ReservoirDatabaseDao,val databaseUser:UserDatabaseDao,application:Application) :AndroidViewModel(application) {

    private var viewModelJob= Job()
    private val uiScope=CoroutineScope(Dispatchers.Main+viewModelJob)


    var user=MutableLiveData<User?>()
    //Coordinates Input
    var x:String=""
    var y:String=""
    private lateinit var currentUser:User
    private var thisReserve= MutableLiveData<Reservoir?>()

    //Print reserves
    private val reserves=database.getMyReservesDESCdate()
//    val reservesString = reserves.map{ reserves:List<Reservoir> ->
//        formatReserves(reserves, application.resources)
//    }

    init {
       // createUser()
        getUser()
        Timber.i("Bonjour")
        //Empty for testing
    }


    private fun createUser(){
        uiScope.launch {
            val user1=User()
            databaseUser.insertUser(user1)
            databaseUser.updateUser(user1)
            Timber.i("User created!")
        }
    }

    private fun getUser(){
        uiScope.launch {
           //user.value= executeGetUser()
            currentUser=executeGetUser()
          //  Timber.i("User got is ${currentUser.userName}")

        }

    }

    private suspend fun executeGetUser():User{
     return  withContext(Dispatchers.IO){
         val user=databaseUser.getLastLoggedInUser()
         user
        }

    }

//    private fun callgetReserve(){
//        uiScope.launch {
//            getReservefromDatabase()
//        }
//    }
    private suspend fun getReservefromDatabase():Reservoir?{
        return withContext(Dispatchers.IO){
            val reserve=database.getReserve()
            reserve
        }
    }

     fun initializeReserve(){
        uiScope.launch{
            createReserve()
            //getUser()
            thisReserve.value =getReservefromDatabase()
           }
    }
    //Creates the reserve
    private suspend fun createReserve(){
        withContext(Dispatchers.IO) {
            val reserve = Reservoir()
            reserve.user=currentUser.userId
            reserve.userusername=currentUser.userName
           // reserve.user= user.value?.userId ?:0
            database.insert(reserve)
            database.update(reserve)

        }
    }


    //Clears all reserves
    fun clearReserves(){
        uiScope.launch {
            executeClear()
        }
    }
    private suspend fun executeClear(){
        withContext(Dispatchers.IO){
            database.clear()
            Timber.i("Cleared")
        }
    }





//     suspend fun storeData(X:String,Y:String){
//        withContext(Dispatchers.IO) {
//            val reserve = Reservoir()
//            reserve.locationX = X.toLong()
//            reserve.locationY = Y.toLong()
//            database.update(reserve)
//        }
//    }

    //accessible from fragment
    fun update(){
        uiScope.launch {
            if(x!=""&&y!="") {
                executeUpdate()
            }
        }
    }
    private suspend fun executeUpdate(){
        withContext(Dispatchers.IO) {
            val reserve = database.getReserve()
            reserve.locationX = x.toDouble()
            reserve.locationY = y.toDouble()
            database.update(reserve)
            Timber.i("diameter of latest entity is ${reserve.diameter}")
        }
    }

//    private suspend fun getReservefromDatabase() {
//         return withContext(Dispatchers.IO){
//              val reserve=database.getReserve()
//             createReserve()
//             reserve
//            }
//        }
//    }
//    private fun createReserve():Reservoir?{
//        return withContext(Dispatchers.){
//            val newReserve=Reservoir()
//            insert(newReserve)
//            thisReserve.value=getReserve()
//            }
//    }
//    private suspend fun insert(reserve:Reservoir){
//        withContext(Dispatchers.IO){
//            database.insert(reserve)
//        }
//    }
//    private suspend fun getReserve():Reservoir?{
//        return withContext((Dispatchers.IO)){
//            var reserve=database.getReserve()
//            reserve
//        }
//    }
//override fun onCleared() {
//    super.onCleared()
//    viewModelJob.cancel()
//    }

}