package com.waterreserve.myapplication002.screens.signInOrUp

import android.app.Application
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.User
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.Timer
import com.waterreserve.myapplication002.MainActivityViewModel
import androidx.lifecycle.LiveData

class SignUpViewModel(val database: ReservoirDatabaseDao,  val databaseUser: UserDatabaseDao, application: Application) :
    AndroidViewModel(application) {
        val viewModelJob=Job()
        private val uiScope= CoroutineScope(Dispatchers.Main+ viewModelJob)

    var username:String=""
    var email:String=""
    var password:String=""
    var firebaseID:String=""
    lateinit var listofUsernames:MutableList<String>
    private val _updateUI=MutableLiveData<Boolean>(false)
    val updateUI:LiveData<Boolean>
        get()=_updateUI

    init {
        checkUsernameUniqueness()
    }

    fun checkUsernameUniqueness() {
        uiScope.launch {
            executecheckUsernameUniqueness()
        }
    }

    private suspend fun executecheckUsernameUniqueness(){
       return withContext(Dispatchers.IO){
           listofUsernames=databaseUser.getAllUsernames()
        }
    }

    fun storeUserInfo(){
        uiScope.launch {
            executestoreInfo()
            _updateUI!!.value=!_updateUI!!.value!!
        }
    }

    private suspend fun executestoreInfo(){
        withContext(Dispatchers.IO){
            val newUser=User()
            newUser.userName=username
            newUser.passWord=password
            newUser.firebaseUserId=firebaseID
            newUser.userEmail=email
            newUser.lastloggedIn=System.currentTimeMillis()
            databaseUser.insertUser(newUser)
            databaseUser.updateUser(newUser)
            listofUsernames.add(listofUsernames.size,username)
            Timber.i("User has been created")
        }
    }
}