package com.waterreserve.myapplication002


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import android.app.Application
import com.waterreserve.myapplication002.screens.database.Measurement
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.Reservoir
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.User
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.navigation.NavDestination
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.NullPointerException


public class MainActivityViewModel(val database: ReservoirDatabaseDao, val databaseUser:UserDatabaseDao, val databaseMeasurement: MeasurementDatabaseDao,
                                   application: Application):AndroidViewModel(application) {
    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private lateinit var currentUser: User
    var username= MutableLiveData<String>("")
    var userId: Long = 0L
    var userlogged = MutableLiveData<String>("")
    var numOfUsers: Int = 0
    private val _gotNumOfUsers = MutableLiveData<Int>(0)
    val gotNumOfUsers: LiveData<Int>
        get() = _gotNumOfUsers
    private lateinit var usersList: List<User>
    private lateinit var reservoirList: List<Reservoir>
    private lateinit var measurementsList: List<Measurement>
    var enableSkip: Boolean = false
    var userIs:String=""


    init {
        getUser()
    }


//    private fun getNumOfUsers(){
//        uiScope.launch {
//            numOfUsers=executegetNumOfUsers()
//        }
//    }
//    private suspend fun executegetNumOfUsers():Int{
//        return withContext(Dispatchers.IO){
//            val usersNum=databaseUser.getUsersCount()
//            _gotNumOfUsers.value?.plus(1)
//            usersNum
//        }
//    }

    fun signOut() {
        uiScope.launch {
            currentUser = letHimIn()
            username.value = currentUser.userName
            Timber.i("signed out locally")
        }
    }

    private suspend fun letHimIn(): User {
        return withContext(Dispatchers.IO) {
            try {
                val user0 = databaseUser.getUserByUsername("User 0")
                user0.lastloggedIn = System.currentTimeMillis()
                databaseUser.updateUser(user0)
                user0
            } catch (e: NullPointerException) {
                println("No user, null pointer exception")
                // if (databaseUser.getUsersCount()<1) {
                val user0 = User()
                user0.userName = "User 0"
                user0.lastloggedIn = System.currentTimeMillis()
                databaseUser.insertUser(user0)
                user0
                //Thread.sleep(100)//This has been added because of bugs where User 0 was created but second User 0 was created with a difference of 3 millies
            }
        }
    }

         fun getUser() {
            uiScope.launch {
                currentUser = executeGetUser()
                userId = currentUser.userId
                username.value=userIs
            }
        }

        private suspend fun executeGetUser(): User {
            return withContext(Dispatchers.IO) {
                try {
                    val user = databaseUser.getLastLoggedInUser()
                    userIs = user.userName
                    user
                } catch (e: NullPointerException) {
                    val user0 = User()
                    user0.userName = "User 0"
                    user0.lastloggedIn = System.currentTimeMillis()
                    userIs = user0.userName
                    databaseUser.insertUser(user0)
                    user0
                }
            }
        }


    }
