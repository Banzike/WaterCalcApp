package com.waterreserve.myapplication002.screens.user

import android.app.Application
import com.waterreserve.myapplication002.convertLongToDateString
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.Reservoir
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.User
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class UserViewModel(val databaseR:ReservoirDatabaseDao, val databaseU:UserDatabaseDao,val databaseM: MeasurementDatabaseDao,
                    application: Application):AndroidViewModel(application) {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var userName = MutableLiveData<String>("")
    private lateinit var currentUser: User
    private lateinit var currentReserve:Reservoir
    val numOfReserves = MutableLiveData<String>("")
    val updateOn=MutableLiveData<String>("")
    val email=MutableLiveData<String>("")
    val reserveIs=MutableLiveData<String>("")
    private val db = FirebaseFirestore.getInstance()

    init {

        clearTable()
    }
    private fun clearTable(){
        uiScope.launch {
            executeClearTable()
            getUser()
        }
    }

    private suspend fun executeClearTable() {//deletes invalid reserves
        withContext(Dispatchers.IO) {
            val depthData = databaseR.getDepth()
            val idData = databaseR.getIDs()
            var limit = databaseR.getRowCount()
            // Timber.i("clearTable called")
            for (i in 0 until databaseR.getRowCount()) {
                if (depthData[i] == 0.0) {
                    val thisReserve=databaseR.get(idData[i])
                    databaseR.deleteThis(idData[i])

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

    private fun getUser() {
        uiScope.launch {
            currentUser = executeGetUser()
            userName.value = currentUser.userName
            email.value=currentUser.userEmail
            Timber.i("userName is ${userName.value}")
            Timber.i("email is ${email.value}")
            getNumOfReservesOwned()
        }
    }

    private suspend fun executeGetUser(): User {
        return withContext(Dispatchers.IO) {
            val user = databaseU.getLastLoggedInUser()
            Timber.i("user email is: ${user.userEmail}")
            user
        }
    }

    private fun getNumOfReservesOwned() {
        uiScope.launch {
            numOfReserves.value = executeGetNumOfReservesOwned().toString()
//            if(executeGetNumOfReservesOwned()>0){
//                getNextUpdate()
//            }
        }
    }

    private suspend fun executeGetNumOfReservesOwned(): Int {
        return withContext(Dispatchers.IO) {
            var reservesOwned: Int = 0
            val reserves = databaseR.getAllReserves()
            for (i in 0 until reserves.size) {
                if (reserves[i].userusername == currentUser.userName) {//dateJoined is the most unique attribute
                    reservesOwned += 1
                }
            }
            if(reservesOwned>0){
                getNextUpdate(currentUser.userName)
            }
            reservesOwned
        }
    }

    private fun getNextUpdate(username: String) {
        uiScope.launch {
            updateOn.value=convertLongToDateString(executeGetNextUpdate(username))
            var type:String=""
            if (currentReserve.diameter>0.0){
            type="Πηγάδι "
           }
           else{
            type="Στέρνα "
           }

            reserveIs.value=type+currentReserve.reservoirId.toString()
        }
    }
//version with standard 1 week delay
//    private suspend fun executeGetNextUpdate(): Long {
//        return withContext(Dispatchers.IO) {
//            var reservesOwned: Int = 0
//            val reserves = databaseR.getReservesOfUser(currentUser.userName)
//            val reserveToUpdate: Reservoir =if (reserves.size>1) {
//                var item=0
//                for (i in 1 until reserves.size) {
//                    for (j in 0 until reserves.size) {
//                        if (reserves[i].lastUpdated < reserves[j].lastUpdated)
//                            item=j
//                    }
//                }
//                reserves[item]
//            }
//            else {reserves[0]}
//            val nextUpdateOn:Long = reserveToUpdate.lastUpdated + 1000*60*60*24*7
//            nextUpdateOn
//        }
//    }

    private suspend fun executeGetNextUpdate(username:String): Long {
        return withContext(Dispatchers.IO) {
            currentReserve=databaseR.getNextToUpdate(currentUser.userName)
            currentReserve.nextUpdateDue

        }
    }


    fun deleteUser(){
        uiScope.launch {
            deleteMeasurementsFromFirebase()
        }
    }

    private fun deleteMeasurements(){
        uiScope.launch {
            executeDeleteMeasurements()
        }
    }

    private suspend fun executeDeleteMeasurements(){
        withContext(Dispatchers.IO){
            Timber.i("delete ML")
            databaseM.deleteMeasurementsOfUser(currentUser.userName)
            executeDeleteReservoirs()
        }
    }

    private suspend fun executeDeleteReservoirs(){
        withContext(Dispatchers.IO){
            Timber.i("delete RL")
            databaseR.deleteReservesOfUser(currentUser.userId)
            executeDeleteUser()
        }
    }

    private suspend fun executeDeleteUser(){
        withContext(Dispatchers.IO){
            Timber.i("delete UL")
            databaseU.deleteUser(currentUser.userId)
        }
    }


    private suspend fun deleteMeasurementsFromFirebase(){
        withContext(Dispatchers.IO){
            Timber.i("deleteMF runs")

            for (measurement in databaseM.getMeasurementsOfUser(currentUser.userName)) {
             //   val measurementName = measurement.ownerIs.toString()+measurement.reserveId.toString()+". "+measurement.measurementId
                val documentRef = db.collection("measurements").document(measurement.ownerIs.toString()+measurement.reserveId.toString()+"."+measurement.measurementId)
                documentRef
                    .delete()
                    .addOnSuccessListener {
                        // Document successfully deleted
                        println("DocumentSnapshot successfully deleted!")
                        Timber.i("Measurement has been deleted from firestore")
                    }
                    .addOnFailureListener { e ->
                        // Handle any errors
                        println("Error deleting document: $e")
                    }
            }
            deleteReservesFromFirebase()
        }
    }


        private suspend fun deleteReservesFromFirebase() {
            withContext(Dispatchers.IO) {

            Timber.i("deleteRF runs")
                for (reserve in databaseR.getReservesOfUser(currentUser.userName)) {
                    val reserveName = reserve.userusername + reserve.reservoirId.toString()
                    val documentRef = db.collection("reserves").document(reserveName)
                    documentRef
                        .delete()
                        .addOnSuccessListener {
                            // Document successfully deleted
                            println("DocumentSnapshot successfully deleted!")
                            Timber.i("Reserve has been deleted from firestore")
                        }
                        .addOnFailureListener { e ->
                            // Handle any errors
                            println("Error deleting document: $e")
                        }

                }
                deleteUserFromFirebase()
            }
        }


        private suspend fun deleteUserFromFirebase() {
                Timber.i("deleteUF runs")
                withContext(Dispatchers.IO) {
                    val username=currentUser.userName
                    val documentRef = db.collection("reserves").document(username)
                    documentRef
                        .delete()
                        .addOnSuccessListener {
                            // Document successfully deleted
                            println("DocumentSnapshot successfully deleted!")
                            Timber.i("User has been deleted from firestore")
                            deleteMeasurements()
                        }
                        .addOnFailureListener { e ->
                            // Handle any errors
                            println("Error deleting document: $e")
                        }
                }

            }
}