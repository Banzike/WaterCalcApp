package com.waterreserve.myapplication002.screens.signInOrUp

import android.app.Application
import com.waterreserve.myapplication002.MainActivity
import com.waterreserve.myapplication002.MainActivityViewModel
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import com.waterreserve.myapplication002.screens.database.User
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import timber.log.Timber

class SignInViewModel(val database: ReservoirDatabaseDao,  val databaseUser: UserDatabaseDao,
                      val databaseMeasurement: MeasurementDatabaseDao, application: Application) :
    AndroidViewModel(application) {

    private lateinit var fbID: String
    var userString:String=""
    var passwordString:String=""
    private var emailExists=false
    private var userNameExists=false
    private var passWordExists=false
    var userIdfromPassword:Long=0L
    var userIdfromEmail:Long=0L
    var userNames= mutableListOf<String>()
    private val _spinnerAction=MutableLiveData<Boolean>(false)
    val spinnerAction
            get()=_spinnerAction
    var letSignInIf=false
    private val _letSignIn=MutableLiveData<Boolean>(false)
    val letSignIn
        get() = _letSignIn
    var letIf:Boolean=false
    var userNameIs="String"
    var email:String=""
    var password:String=""
    val viewModelJob= Job()
    private val uiScope= CoroutineScope(Dispatchers.Main+viewModelJob)

    //private lateinit var viewModelMA:MainActivityViewModel
    init {
        getAllExistingUsers()
    }

    private fun getAllExistingUsers(){
        uiScope.launch {
            executegetAllExistingUsers()
            letIf=true
            _spinnerAction?.value=true
        }
    }

    private suspend fun executegetAllExistingUsers(){
        withContext(Dispatchers.IO){
            userNames=databaseUser.getAllUsernames()
        }
    }

    fun deleteAll(){
        uiScope.launch {
            executeDeleteAll()
        }
    }

    private suspend fun executeDeleteAll(){
        withContext(Dispatchers.IO){
            databaseMeasurement.clear()
            database.clear()
            databaseUser.clear()
        }
    }

    fun signInUser( userId:String,username:String,useremail:String,userphone:String) {
        uiScope.launch {
            executeUserSignIn(userId,username,useremail,userphone)
            _letSignIn.value=!_letSignIn.value!!
        }
    }

    private suspend fun executeUserSignIn(firebaseuserId: String, username: String,useremail: String,userphone:String)
    {
        withContext(Dispatchers.IO){
            fbID = firebaseuserId

            //creates table with firebase IDs
            var userFirebaseIdsTable = databaseUser.getAllFirebaseUserIds()
            //Timber.i("List of firebaseuserIDs is $userFirebaseIdsTable")

            //checks if provided firebase ID is in the table if it exists, lastloggedIn is updated meaning he is the currently logged in user
            if (userFirebaseIdsTable.contains(firebaseuserId)) {

               // Timber.i("User exists and is ${databaseUser.getUserByFirebaseId(firebaseuserId)}}")
                var user= databaseUser.getUserByFirebaseId(firebaseuserId)
                user.lastloggedIn=System.currentTimeMillis()
                databaseUser.updateUser(user)
                Timber.i("User updated")
            }

              else{
                //new user with given firebase ID and username is locally created
                val newUser = User()
                newUser.firebaseUserId = firebaseuserId
                newUser.userName = username
                newUser.userEmail=useremail
                newUser.userPhone=userphone
                databaseUser.insertUser(newUser)
                databaseUser.updateUser(newUser)
                Timber.i("User is NEW:$newUser")
                checkUserDatabase()
             }
        }
    }

    private suspend fun checkUserDatabase(){
        withContext(Dispatchers.IO){
            Timber.i("fbID is: $fbID")

            val currentUser=databaseUser.getLatestUser()

            Timber.i("Total num of users is: ${databaseUser.getUsersCount()}")
            Timber.i("Current user's name and id are: $currentUser")//was null because fbIDs etc didn't pass. That is because user object had to be inserted in database after delcaring values;just before "update".
            var userFirebaseIdsTable2 = databaseUser.getAllFirebaseUserIds()
            Timber.i("FbID table: $userFirebaseIdsTable2")
        }
    }

//    fun signInViaSpinner(username:String){
//        uiScope.launch {
//           executesignInViaSpinner(username)
//            _letSignIn.value=!_letSignIn.value!!
//        }
//    }

//    private suspend fun executesignInViaSpinner(username:String){
//        withContext(Dispatchers.IO){
//            //local sign in db
//            val user=databaseUser.getUserByUsername(username)
//            user.lastloggedIn=System.currentTimeMillis()
//            userNameIs=user.userName
//            email=user.userEmail
//            password=user.passWord
//            Timber.i("Viewmodel user password is $password")
//            databaseUser.updateUser(user)
//            letSignInIf=true
//            //val viewModelMA = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
//            //Aν μπορουσα να ενημερωσω εδω το MainActivity να εκτελεσει το getUser()....
//            //Αλλιως πρεπει να βρω τροπο πρωτα να εκτελειται αυτο και μετα ο κδωδικας απο κατω του
//        }
//    }




    fun loginLocal(){
        uiScope.launch {
            executeloginLocal()
            _letSignIn.value=!_letSignIn.value!!
        }
    }

    private suspend fun executeloginLocal(){
        withContext(Dispatchers.IO){
            //should check for local user be here? there is already a check for the firebase users. Could insert a check for the rest but. What about the phone signup?
            //it could.. check if there are emails etc and password. if email exists but password is wrong, declare it. password retrieval(could sent the password to email or phone or prompt to create a new one)
            //if both are correct user is being updated to last logged in. gotta see what to do with user 0. every time someone logs out and all users are deleted, i could create him
            //what if there is more than one user with the same password or email(since it's not unique)
            //i can solve this with lists. if  the list of the users with the same password
            //and the list of the userrs with the same email share a common id

            if (passwordString.isNotBlank()){
                Timber.i("passwordString is not blank")
                val passwordTable=databaseUser.getAllPasswords()
                passWordExists=passwordTable.contains(passwordString)
            }

            if (userString.isValidEmail()) {
                Timber.i("email is valid")
                val emailTable=databaseUser.getAllEmails()
                emailExists=emailTable.contains(userString)
                Timber.i("emailExists is $emailExists")
            }

            else if (userString.isNotBlank()) {
                Timber.i("userString is neither blank nor email")
                val userNameTable=databaseUser.getAllUsernames()
                userNameExists=userNameTable.contains(userString)
            }

            if(passWordExists&&userNameExists){
                val passofusername=databaseUser.getPasswordOfUsername(userString)
                if(passwordString==passofusername){
                    val user=databaseUser.getUserByUsername(userString)
                    Timber.i("USER FOUND VIA USERNAME and his id is ${user.userId}")
                    user.lastloggedIn=System.currentTimeMillis()
                    databaseUser.updateUser(user)
                }
                else{
                    Timber.i("wrong password")
                }
            }

            else if(passWordExists&&emailExists){
                val passwordOfEmail=databaseUser.getPasswordOfEmail(userString)
                if(passwordString==passwordOfEmail) {
                    val user=databaseUser.getUserByEmail(userString)
                    Timber.i("USER FOUND VIA EMAIL and his id is ${user.userId}")
                    user.lastloggedIn=System.currentTimeMillis()
                    databaseUser.updateUser(user)
                }

                else {
                    Timber.i("wrong password")
                }
            }
        }
    }


    fun signOut(){
        uiScope.launch {
          letHimIn()
        }
    }

    private suspend fun letHimIn(){
        return withContext(Dispatchers.IO){
            val user0=databaseUser.getUserByUsername("User 0")
            user0.lastloggedIn=System.currentTimeMillis()
            databaseUser.updateUser(user0)
        }
    }
    private fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}