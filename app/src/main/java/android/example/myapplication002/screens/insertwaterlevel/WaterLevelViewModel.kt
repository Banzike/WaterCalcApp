package android.example.myapplication002.screens.insertwaterlevel

import android.app.Application
import android.example.myapplication002.screens.database.Measurement
import android.example.myapplication002.screens.database.MeasurementDatabaseDao
import android.example.myapplication002.screens.database.Reservoir
import android.example.myapplication002.screens.database.ReservoirDatabaseDao
import android.example.myapplication002.screens.database.User
import android.example.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import timber.log.Timber


class WaterLevelViewModel (val database: ReservoirDatabaseDao,val databaseUser: UserDatabaseDao, val databaseMeasurement: MeasurementDatabaseDao,
                           application: Application):AndroidViewModel(application) {
//updates database with the user provided value of the water level
//calculates the filled ratio value which is used t determine the image that will be chosen in the next fragment
//should i store the value of the image in the database? should i run code to determine it? storing it makes it eaier in the fragment
    var idOfReserve:Long=0L
    val imageChosen=MutableLiveData<String>("black")
    private lateinit var thisReserve:Reservoir
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var idArg:Long=0L
    var isOld=true

    var newWaterLevelString = "0"
    var waterDepth:Double=0.0

    lateinit var thisUser:User


    private suspend fun getReservefromDatabase(): Reservoir {
        return withContext(Dispatchers.IO) {
            database.getReserveUpdated()
        }
    }

    init {
        getReserve()
     //   getAllReserveAndPrint()
    }

    fun getAllReserveAndPrint(){
        uiScope.launch {
            executegetAllReserveAndPrint()
        }
    }

   private suspend fun executegetAllReserveAndPrint(){
        withContext(Dispatchers.IO){
            val listOfReserves=database.getAllReserves()
            for(i in 0 until  listOfReserves.size)
            {
                val idOfReserve=listOfReserves[i].reservoirId
                Timber.i("Id of user at index $i is ${idOfReserve}")
            }
        }
    }

    fun getUser(){
        uiScope.launch {
            thisUser = executeGetUser()
        }
    }

   private suspend fun executeGetUser():User{
        return withContext(Dispatchers.IO){
            val userId=thisReserve.user
            val user=databaseUser.getUserById(userId)
            user
        }
    }

   fun getReserve(){
        uiScope.launch {
          //  Timber.i("getReserve launced")
            thisReserve=executeGetReserve()
            getUser()
        }
    }

   private suspend fun executeGetReserve():Reservoir{
        return withContext(Dispatchers.IO){

            val reserve: Reservoir = if(!isOld){ //if the reserve has just been created we get the reserve from here
                database.getReserve()
            } else{
                database.get(idArg) //if the reserve is older we get it from here
            }
       //     Timber.i("getReserve launched")
            idOfReserve=reserve.reservoirId
            waterDepth=reserve.depth
            Timber.i("idOfReserve is: $idOfReserve ")
         //   Timber.i("Water depth is $waterDepth")
            //getUser()
            reserve
        }
    }

   fun insertMeasurement(){
        uiScope.launch {
            executeInsertMeasurement()
        }
    }

   private suspend fun executeInsertMeasurement(){
        withContext(Dispatchers.IO){
            val measurement=Measurement()
            measurement.reserveId=thisReserve.reservoirId
            thisReserve.lastUpdated=System.currentTimeMillis()
            measurement.reservecreatedDate=thisReserve.creationDate
            measurement.ownerIs=thisReserve.userusername
            databaseMeasurement.insert(measurement)
        }
    }

   fun updateWaterLevel() {
        uiScope.launch {
            executeUpdateWaterLevel()
        }
    }

   private suspend fun executeUpdateWaterLevel() {
        withContext(Dispatchers.IO) {
            val newWaterLevel = newWaterLevelString.toDouble()//stores value inputed from the user
            thisReserve.waterlvl = thisReserve.depth-newWaterLevel //passes values to the fields
            thisReserve.filledRatio = ((thisReserve.depth-newWaterLevel)/thisReserve.depth) * 100
            thisReserve.lastUpdated=System.currentTimeMillis()
            database.update(thisReserve)//updates the database
            chooseImage()
            // insertMeasurement()
            Timber.i("Reserve filled ratio is ${thisReserve.filledRatio}")

        }
    }

//    private suspend fun passfillPercentage(){//launches calculation of filled ratio, passes value to filledPercent live data
//         withContext(Dispatchers.IO){
//           var filledPercent=""
//           filledPercent= String.format("%.2f",thisReserve.filledRatio)
//             chooseImage()
//
//
//        }
//    }

    private  fun chooseImage(){//maybe it needs to return some shit
      uiScope.launch{
//            calculateFillPercentage()
           // val reserve=database.getReserve()
            when(thisReserve.filledRatio.toInt()){
                in 0..19-> imageChosen.value="red"
                in 20..39-> imageChosen.value="yellow"
                in 40..59-> imageChosen.value="green"
                in 60..79-> imageChosen.value="teal"
                else -> imageChosen.value="blue"
            }
          upDateImageChosen()
          //makeVisible.value=true
        }
    }

    private suspend fun upDateImageChosen(){
        withContext(Dispatchers.IO){
            //val reserve=database.getReserve()
            thisReserve.imageColour=imageChosen.value.toString()
            database.update(thisReserve)
            Timber.i("chooseImage runs: Image is ${imageChosen.value}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        Timber.i("on clear called")
    }

}