package com.waterreserve.myapplication002.screens.droplet

import android.app.Application
import com.waterreserve.myapplication002.convertLongToDateString
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.Reservoir
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.User
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.math.PI

class DropletViewModel(val database: ReservoirDatabaseDao, val databaseUser: UserDatabaseDao,
                       val databaseMeasurement:MeasurementDatabaseDao, application: Application):AndroidViewModel(application) {

    var currentUsername=MutableLiveData<String>("")
    var id:Long=0L
    var idString=MutableLiveData<String>("...")
    var typeLive=MutableLiveData<String>("")
    var lastUpdated=MutableLiveData<String>("")
    var toUpDate=MutableLiveData<String>("")
    private val _color=MutableLiveData<String>("")
    private val db = FirebaseFirestore.getInstance()
    val reservesCollection = db.collection("reserves")
    val measurementsCollection = db.collection("measurements")
    val color:LiveData<String>
        get()=_color

    private val viewModelJob= Job()
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)
    lateinit var currentUser:User
    var thisReserve=Reservoir()
    val filledPercent= MutableLiveData<String>()
    val volumeTotal=MutableLiveData<String>()
    val filledVolume=MutableLiveData<String>()
    private val imageChosen=MutableLiveData<String>("black")

    init {
        getCurrentReserve()
    }

    private fun getCurrentUser(){
        uiScope.launch {
            currentUser=executeGetCurrentUser()
            currentUsername.value=currentUser.userName
        }
    }

    private suspend fun executeGetCurrentUser():User{
        return withContext(Dispatchers.IO){
            val user=databaseUser.getUserById(thisReserve.user)
            user
        }
    }

    fun setUpdateFrequency(days:Int){
      uiScope.launch {
          executeSetUpdateFrequency(days)
          toUpDate.value=getToUpdate()
      }
    }

    private suspend fun executeSetUpdateFrequency(days:Int){
        withContext(Dispatchers.IO){
            thisReserve.updateMeEvery=days.toLong()*1000*60*60*24
            database.update(thisReserve)
        }
    }

    private fun getCurrentReserve(){//gets current reserve
        uiScope.launch {
            Timber.i("get current reserve entered")
            Timber.i("id is $id")
        thisReserve=database.get(id)//gets reserve by id
        idString.value=id.toString()
        fillPercentage()
        capacityPercentage()
        _color.value=getColor()
        typeLive.value=getType()
        lastUpdated.value=getUpDated()
        toUpDate.value=getToUpdate()
        getCurrentUser()
        }
    }

    private suspend fun getUpDated():String{
        return withContext(Dispatchers.IO){
            val date=thisReserve.lastUpdated
            convertLongToDateString(date)
        }
    }
    private suspend fun getToUpdate():String{
        return withContext(Dispatchers.IO){
            val date=thisReserve.lastUpdated+ thisReserve.updateMeEvery?:1000*60*60*24*7// ms/min/hr/day=1week
            thisReserve.nextUpdateDue=date
            database.update(thisReserve)
            convertLongToDateString(date)
        }
    }

    private fun fillPercentage(){//launches calculation of filled ratio, passes value to filledPercent live data
        uiScope.launch{
            calculateCapacity()
            filledPercent.value= String.format("%.2f",thisReserve.filledRatio)+"%"
        }
    }
    private fun capacityPercentage(){
        uiScope.launch {
            calculateCapacity()
            volumeTotal.value= String.format("%.2f",thisReserve.capacity)+" Κυβικά μέτρα"

            calculateFilledVolume()
           filledVolume.value=String.format("%.2f",thisReserve.filledVolume)+" Κυβικά μέτρα"
        }
    }
    private suspend fun calculateCapacity(){
        withContext(Dispatchers.IO){
            if (thisReserve.type=="Πηγάδι"){
                thisReserve.capacity = PI * thisReserve.diameter * thisReserve.diameter * thisReserve.depth
            }
            else {
                thisReserve.capacity=thisReserve.length*thisReserve.width*thisReserve.depth
            }
            database.update(thisReserve)
        }
    }
    private suspend fun calculateFilledVolume(){
        withContext(Dispatchers.IO){
            thisReserve.filledVolume= (thisReserve.filledRatio * thisReserve.capacity) / 100
            database.update(thisReserve)
        }
    }

    fun deleteThisReserve(){
        uiScope.launch{
            executeDeletMeasurements()
            executeDeletReservoir()
            //Delete from firebase as well
           // deleteMeasurementsFromFirebase()
            deleteReserveFromFirebase()
        }
    }

    private suspend fun getType():String{
        return withContext(Dispatchers.IO){
            val type=thisReserve.type
            type
        }
    }
    private suspend fun getColor():String {
       return  withContext(Dispatchers.IO){
           val colorString= thisReserve.imageColour
           colorString
         }
    }

    private suspend fun executeDeletReservoir(){
        withContext(Dispatchers.IO){
            database.deleteThis(thisReserve.reservoirId)
        }
    }

    private suspend fun executeDeletMeasurements(){
        withContext(Dispatchers.IO){
            databaseMeasurement.deleteMeasurementsOfReserve(thisReserve.reservoirId)
        }
    }

    private suspend fun deleteMeasurementsFromFirebase(){//not yet implemented
        withContext(Dispatchers.IO){
//            for(measurement in databaseMeasurement.getAllMeasurements()){
//                if(thisReserve.reservoirId==measurement.reserveId){
//                    //delete from local
//                    databaseMeasurement.de
//                }
//            }
            databaseMeasurement.deleteMeasurementsOfReserve(thisReserve.reservoirId)
        }
    }

    private suspend fun deleteReserveFromFirebase(){
        withContext(Dispatchers.IO){

            val reserveName=thisReserve.userusername+thisReserve.reservoirId.toString()
            val documentRef = db.collection("reserves").document(reserveName)
            documentRef
                .delete()
                .addOnSuccessListener {
                    // Document successfully deleted
                    println("DocumentSnapshot successfully deleted!")
                    Timber.i("Reserve has been deleted from database")
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    println("Error deleting document: $e")
                }
        }
    }
}