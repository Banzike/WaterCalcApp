package com.waterreserve.myapplication002.screens.showMap

import android.app.Application
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.waterreserve.myapplication002.screens.database.User
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import timber.log.Timber

class ShowMapViewModel(val database: ReservoirDatabaseDao, val databaseUser:UserDatabaseDao, application: Application):
    AndroidViewModel(application) {

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    lateinit var currentCoordinates: LatLng
    lateinit var latList: List<Double>
    lateinit var lonList: List<Double>
    lateinit var idList: List<Long>
    lateinit var colorList: List<String>
    lateinit var depthList:List<Double>
    lateinit var userList:List<Long>
    private val _start=MutableLiveData<Boolean>(false)
    val start:LiveData<Boolean>
        get()=_start
    private val _display=MutableLiveData<Boolean>(false)
    val display:LiveData<Boolean>
        get()=_display
    private lateinit var currentUser:User

    var markerListFragment= mutableListOf<Marker>()
    var userListFragment = mutableListOf<Boolean>()
    var markerIDlistFragment = mutableListOf<String>()

    var rowNum: Int = 0
    var currentUserId:Long=0L

    private val db = FirebaseFirestore.getInstance()
    val reservesCollection = db.collection("reserves")

    init {
//        start.value=0
        cleanAndGiveData()
//gimmeData()
       // Timber.i("viewModel init")
    }

    private fun cleanAndGiveData(){
        uiScope.launch {
            clearTable()
            getCurrentUser()
            gimmeData()
        }
    }

    private fun getCurrentUser(){
        uiScope.launch {
            currentUser=executegetCurrentUser()
        }
    }

    private suspend fun executegetCurrentUser():User{
        return withContext(Dispatchers.IO){
            val user=databaseUser.getLastLoggedInUser()
            currentUserId=user.userId
            user
        }
    }
    private suspend fun clearTable() {//deletes invalid reserves
        withContext(Dispatchers.IO) {
            val depthData = database.getDepth()
            val idData = database.getIDs()
            var limit = database.getRowCount()
           // Timber.i("clearTable called")
            for (i in 0 until database.getRowCount()) {
                if (depthData[i] == 0.0) {
                    val thisReserve=database.get(idData[i])
                    database.deleteThis(idData[i])

                    Timber.i("reserve deleted")

                    //deleteReservesFromFirebase
                    val reserveName = thisReserve.userusername + thisReserve.reservoirId.toString()
                    val documentRef = db.collection("reserves").document(reserveName)
                    documentRef
                        .delete()
                        .addOnSuccessListener {
                            // Document successfully deleted
                            Timber.i("Reserve has been deleted from database")
                        }
                        .addOnFailureListener { e ->
                            // Handle any errors
                            Timber.i("Error deleting document: $e")
                        }
                }
            }
        }
    }


    private fun gimmeData() {
        uiScope.launch {
            colorList = gimmeColor()
            rowNum=gimmeRowCount()//returns total number of reserves can use length in list as well
            latList=gimmeLat()
            lonList=gimmeLon()
            idList=gimmeIDs()
            depthList=gimmeDepth()
            userList=gimmeUsers()
            //Timber.i("start value changing")
            _display.value=true
            _start.value=!_start.value!!//live data notifying the fragment to display the data
            //returns the the database's needed elements to the fragment
        }
    }

    private suspend fun gimmeUsers():List<Long>{
        return withContext(Dispatchers.IO){
            val list=database.getUsersByCreationDate()
            list
        }
    }
        private suspend fun gimmeLat(): List<Double>{
        return withContext(Dispatchers.IO){
            val list=database.getLat()
            list
        }
    }

    private suspend fun gimmeDepth(): List<Double>{
        return withContext(Dispatchers.IO){
            val list=database.getDepth()
            list
        }
    }

    private suspend fun gimmeLon():List<Double>{
        return withContext(Dispatchers.IO){
            val lonlist=database.getLon()
            lonlist
        }
    }

    private suspend fun gimmeIDs():List<Long>{
        return withContext(Dispatchers.IO){
            val idList=database.getIDs()
            idList
        }
    }

    private suspend fun gimmeRowCount():Int{
        return withContext(Dispatchers.IO){
            val rows=database.getRowCount()
          //  Timber.i("row count is ${rows}")
            rows
        }
    }

    private suspend fun gimmeColor(): List<String> {
        return withContext(Dispatchers.IO) {
          //  Timber.i("color being passed")
            val list = database.getColor()
          //  Timber.i("coloris passed")
            list
        }
    }
}