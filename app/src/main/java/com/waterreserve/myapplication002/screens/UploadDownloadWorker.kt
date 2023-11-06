package com.waterreserve.myapplication002.screens

import android.content.Context
import com.waterreserve.myapplication002.screens.database.Measurement
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.Reservoir
import android.net.ConnectivityManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.waterreserve.myapplication002.screens.database.ReservoirDatabase
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.User
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import android.os.Handler.Callback
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber



// Define a Worker class for the periodic task
private lateinit var userDao:UserDatabaseDao
private lateinit var reservoirDao:ReservoirDatabaseDao
private lateinit var measurementDao:MeasurementDatabaseDao
private lateinit var usersList:List<User>
private lateinit var reservesList:List<Reservoir>
private lateinit var measurementsList:List<Measurement>


class UploadDownloadWorker(context: Context, params: WorkerParameters) : Worker(context, params){
    private lateinit var networkReceiver: NetworkChangeReceiver
    val thisJob= Job()
    private val uiScope= CoroutineScope(Dispatchers.Main+ thisJob)
    override fun doWork(): Result {

        val database = ReservoirDatabase.getInstance(applicationContext)
        val isConnected = isNetworkConnected(applicationContext)

        userDao = database.userDatabaseDao
        reservoirDao=database.reservoirDatabaseDao
        measurementDao=database.measurementDatabaseDao

        // Access firestore if there is an internet connection
        if (isConnected) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        val reservesCollection = db.collection("reserves")
        val measurementsCollection = db.collection("measurements")

        // Retrieve data from Room and upload to firestore
        usersList = userDao.getAllUsers()
        reservesList = reservoirDao.getAllReserves()
        measurementsList = measurementDao.getAllMeasurements()
        uploadUsers(usersCollection,reservesCollection,measurementsCollection,download(usersCollection, reservesCollection, measurementsCollection))

        }
        return Result.success()
    }

    private fun uploadUsers(usersCollection:CollectionReference,reservesCollection:CollectionReference, measurementsCollection:CollectionReference,callback:Unit){
        for (user in usersList) {
            val username=user.userName
            usersCollection.document(username)
                .set(user.toFirestoreModel()) // Assuming you have a conversion function
                .addOnSuccessListener {
                    // Data uploaded successfully
                    Timber.i("User ${user.userName} uploaded successfully")
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Timber.i("$e")
                }
        }

        reservesList = reservoirDao.getAllReserves()
        for (reserve in reservesList) {
            if (reserve.depth != 0.0) {
                reservesCollection.document(reserve.userusername + reserve.reservoirId.toString())
                    .set(reserve.toFirestoreModel()) // Assuming you have a conversion function
                    .addOnSuccessListener {
                        Timber.i("Reserve ${reserve.reservoirId} uploaded successfully")
                        // Data uploaded successfully
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                    }
            } else {
                Timber.i("Reserve ${reserve.reservoirId} has invalid depth")
            }
        }

        measurementsList = measurementDao.getAllMeasurements()
        for (measurement in measurementsList) {
            measurementsCollection.document(measurement.ownerIs.toString()+measurement.reserveId.toString()+"."+measurement.measurementId)
                .set(measurement.toFirestoreModel()) // Assuming you have a conversion function
                .addOnSuccessListener {
                    // Data uploaded successfully
                }
                .addOnFailureListener { e ->
                    // Handle failure
                }
        }
        download(usersCollection,reservesCollection,measurementsCollection)
    }
    private fun download(usersCollection:CollectionReference,reservesCollection:CollectionReference, measurementsCollection:CollectionReference){
        uiScope.launch{
            downloadUsers(usersCollection, reservesCollection, measurementsCollection )
        }

    }

    private suspend fun downloadUsers(usersCollection:CollectionReference,reservesCollection: CollectionReference,measurementsCollection: CollectionReference) {
            //πρώτα πρεπει να φτιαξω λιστα με τα collections για το εκαστοτε entity.
            //μετα θα δω αν υπαρχει το καθε entity στην τοπικη βαση δεδομενων κι αν δεν υπαρχει θα το βάλω
            Timber.i("download entered")
            try {
                val result = usersCollection.get().await()
                Timber.i("users collection size is ${result.size()}")
                for (document in result) {
                    val user = document.toObject(User::class.java)
                    if (user in usersList || user.userName == "User 0") { //adding user 0 because of bug where 2 user 0 can be in database... why does that happen? same date joined...
                    } else {
                        user.lastloggedIn = 0L//dis
                        userDao.insertUser(user)
                        Timber.i("User ${user.userName} downloaded successfully")
                    }
                }
            } catch (e: Exception) {
                // Handle failure
//                    Timber.e("We lost user")
            }
            downloadReserves(reservesCollection,measurementsCollection)

    }

        private suspend fun downloadReserves(reservesCollection: CollectionReference,measurementsCollection:CollectionReference) {
            //Reserves
                try {
                    val result = reservesCollection.get().await()
                    Timber.i("result size: ${result.size()}")
                    for (document in result) {

                        val reserve = document.toObject(Reservoir::class.java)
                        Timber.i("Current reserve is: ${reserve.userusername + reserve.reservoirId}")
                        if (reserve in reservesList) {
                            Timber.i("reserve is in reserveList")
                        } else if (reserve.depth == 0.0) {
                            Timber.i("Entered depth 0 if")
                            val reserveName = reserve.userusername + reserve.reservoirId.toString()
                            val documentRef = reservesCollection.document(reserveName)
                            documentRef
                                .delete()
                                .addOnSuccessListener {
                                    // Document successfully deleted
                                    Timber.i("Reserve with depth 0 has been deleted from database")
                                }
                                .addOnFailureListener { e: Exception ->
                                    // Handle any errors
                                    Timber.i("Error deleting invalid document: $e")
                                }
                        } else {
                            Timber.i("Reserve is: $reserve")
                            reservoirDao.insert(reserve)
                            //Timber.i("reserve inserted")
                        }
                    }
                } catch (e: Exception) {
                    Timber.i("Reserve exception entered: $e")
                    // Handle failure
                    //Timber.e("We lost reserve")

                }

                downloadMeasurements(measurementsCollection)

        }

        private suspend fun downloadMeasurements(measurementsCollection:CollectionReference){
            //Measurements

            try{
               val result=measurementsCollection.get().await()
                    for (document in result) {
                        val measurement = document.toObject(Measurement::class.java)
                        if (measurement in measurementsList) {
                        } else {
                            measurementDao.insert(measurement)
                    //Timber.i("Measurement downloaded successfully")
                        }
                    }
                }
               catch(e:Exception){
                    // Handle failure
                    //Timber.e("We lost measurement")
                }
        }



    private fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

//    fun retrieveRoomData(){
//        usersList= userDao.getAllUsers()
//        reservoirList=reservoirDao.getAllReserves()
//        measurementsList=measurementDao.getAllMeasurements()
//
//    }

    private fun User.toFirestoreModel(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "userName" to userName,
            "firebaseUserId" to firebaseUserId,
            "dateJoined" to dateJoined,
            "userSurname" to userSurname,
            "userPhone" to userPhone,
            "placeOfResidence" to placeOfResidence,
            "userEmail" to userEmail,
            "lastloggedIn" to lastloggedIn
        )
    }

    private fun Reservoir.toFirestoreModel(): Map<String, Any?> {
        return mapOf(
            "reservoirId" to reservoirId,
            "reservoirName" to reservoirName,
            "creationDate" to creationDate,
            "type" to type,
            "diameter" to diameter,
            "width" to width,
            "length" to length,
            "depth" to depth,
            "waterlvl" to waterlvl,
            "capacity" to capacity,
            "locationX" to locationX,
            "locationY" to locationY,
            "lastUpdated" to lastUpdated,
            "filledRatio" to filledRatio,
            "imageColour" to imageColour,
            "filledVolume" to filledVolume,
            "user" to user,
            "userusername" to userusername,
            "userDateJoined" to userDateJoined,
            "nextUpdateDue" to nextUpdateDue,
            "updateMeEvery" to updateMeEvery
        )
    }

    private fun Measurement.toFirestoreModel(): Map<String, Any?> {
        return mapOf(
            "measurementId" to measurementId,
            "measurementTypes" to measurementTypes,
            "dateCreated" to dateCreated,
            "reserveId" to reserveId,
            "reservecreatedDate" to reservecreatedDate,
            "ownerIs" to ownerIs
        )
    }
}

