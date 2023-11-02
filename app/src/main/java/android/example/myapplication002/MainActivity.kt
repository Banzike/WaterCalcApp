package android.example.myapplication002

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.example.myapplication002.databinding.ActivityMainBinding
import android.example.myapplication002.screens.NetworkChangeReceiver
import android.example.myapplication002.screens.UploadDownloadWorker
import android.example.myapplication002.screens.database.MeasurementDatabaseDao
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.example.myapplication002.screens.insertwaterlevel.WaterLevelFragment
import android.example.myapplication002.screens.insertwaterlevel.WaterLevelFragmentDirections
import android.example.myapplication002.screens.showMap.ShowMapFragment
import android.example.myapplication002.screens.showMap.ShowMapViewModel
import android.example.myapplication002.screens.showMap.ShowMapViewModelFactory
import android.example.myapplication002.screens.signInOrUp.SignInOrUpFragment
import android.example.myapplication002.screens.title.TitleFragment
import android.icu.util.TimeUnit
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.TextureView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.*
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.adapters.AdapterViewBindingAdapter.OnItemSelected
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.navigation.NavigationView
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import timber.log.Timber
import androidx.work.Worker
import com.firebase.ui.auth.AuthUI


class MainActivity : AppCompatActivity(){

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var networkReceiver: NetworkChangeReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        @Suppress("UNUSED_VARIABLE")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Notification values to go to waterlevelfragment
        //val fragmentKey=intent.getStringExtra("FRAGMENT_KEY")

        //  viewModel creation setup
        val application = requireNotNull(this).application
        val dataSource = ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser = ReservoirDatabase.getInstance(application).userDatabaseDao
        val dataSourceMeasurement =
            ReservoirDatabase.getInstance(application).measurementDatabaseDao
        val viewModelFactory = MainActivityViewModelFactory(
            dataSource,
            dataSourceUser,
            dataSourceMeasurement,
            application
        )
        viewModel =ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        binding.lifecycleOwner = this


        //checking network state
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        networkReceiver = NetworkChangeReceiver()
        registerReceiver(networkReceiver, filter)

        //Enables use of notifications
        createNotificationChannel()

        //Initializing firebase
        FirebaseApp.initializeApp(this)

        //Connecting to user authorization client and setting the desired behavior
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )

            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    //server's client ID
                    .setServerClientId(getString(R.string.myClientId))
                    //only show accounts that have signed in previously
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            //Automatically sign in when exactly one credential is retrieved
            .setAutoSelectEnabled(false)
            .build()

        // drawer layout instance to toggle the menu icon to open
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.myNavDrawer)

        navHostFragment =
        supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        //navController.popBackStack(R.id.titleFragment, false)


        //Standard navigation drawer setup, navigation drawer behavior is overriden later
        navView.setupWithNavController(navController)

        val menu = navView.menu
        val userItem = menu.findItem(R.id.userFragment)

        //Using drawer toggle to show logged in user on drawer
        val drawerListener = object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                // Update the username in the navigation drawer here
                viewModel.getUser()
                Timber.i("onDrawerOpened triggered")
            }

            override fun onDrawerClosed(drawerView: View) {
                //Unchecks item when drawer is closed
                navView.checkedItem?.isChecked = false
            }
            override fun onDrawerStateChanged(newState: Int) {}
        }

        drawerLayout.addDrawerListener(drawerListener)
        val headerLayout: View = navView.getHeaderView(0)//Gets uppermost View of header
        val view = headerLayout.findViewById<TextView>(R.id.headerusernametextview)
        //Observer to update displayed name whenever viewModel.getUser() is accessed (upon viewModel initiation and call)
        //also if the user signed in is other than user 0) they are sent to title screen



        //Auto navigate away or to User 0 legal destinations and change drawer name
        viewModel.username.observe(this, Observer<String> {
            userItem.title = viewModel.username.value
            val allowedDestinationsUser0 =
                setOf(R.id.signInOrUpFragment, R.id.signInFragment, R.id.signUpFragment)
            if(viewModel.username.value!="User 0" && navController.currentDestination?.id in allowedDestinationsUser0){
                 Timber.i("username is: ${viewModel.username.value}")
                 Timber.i("going to title fragment")
                 navController.navigate(R.id.titleFragment)
            }
            else if(viewModel.username.value=="User 0"&&navController.currentDestination?.id !in allowedDestinationsUser0){
                navController.navigate(R.id.signInOrUpFragment)
            }
        })



        //Check if logged in user is "User 0" before navigating to fragments other than sign up &| sign in.
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (viewModel.userIs=="User 0"){
                Timber.i("Nav 0 entered")
                val allowedDestinations =
                    setOf(R.id.signInOrUpFragment, R.id.signInFragment, R.id.signUpFragment)
                if (destination.id !in allowedDestinations) {
                    // If User 0 tries to navigate elsewhere, he is sent to the signInOrUpFragment
                    navController.navigate(R.id.signInOrUpFragment)
                    Timber.i("navigating to sign in or up")
                    Toast.makeText(this,
                        "Πρέπει να κάνετε σύνδεση ή εγγραφή",
                        Toast.LENGTH_SHORT).show()
                 } else if(destination.id in allowedDestinations) {
                    Timber.i("Navigation allowed")
                }
                else{
                    Timber.i("Uknown issue")
                }
            }
        }

            //Manual setup of the Navigation Drawer Items
            navView.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {

                    R.id.signInOrUpFragment -> {
                        navController.navigate(R.id.signInOrUpFragment)
                        viewModel.signOut()
                        signOutFirebase()
                        Toast.makeText(this,"Αποσυνδεθήκατε",Toast.LENGTH_SHORT).show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }

                    R.id.titleFragment -> {
                        navController.navigate(R.id.titleFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }

                    R.id.showMapFragment -> {
                        navController.navigate(R.id.showMapFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }

                    R.id.userFragment -> {
                        navController.navigate(R.id.userFragment)
                        drawerLayout.closeDrawer(GravityCompat.START)
                        true
                    }
                    else -> false // indicate that the item has not been handled
                }
            }

        //setupWorker()
        //Setup for uploading/downloading data to Firestore
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            UploadDownloadWorker::class.java,
            15, // Repeat every 15 minutes, it's the minimum
            java.util.concurrent.TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "upload_work",
                ExistingPeriodicWorkPolicy.UPDATE, // Replace existing task if it exists
                periodicWorkRequest
            )
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelID = getString(R.string.notification_channel_ID)
            val name = getString(R.string.notifcation_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Timber.i("Notification channel has been created")
        }
    }


    //Intent created to navigate instantly to WaterLevelFragment upon clicking specific notification
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            val targetFragmentTag = it.getStringExtra("WaterLevelFragment")
            if (targetFragmentTag != null) {
                // Navigate to the desired fragment
                val fragment = supportFragmentManager.findFragmentByTag(targetFragmentTag)
                if (fragment != null) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.myNavHostFragment, fragment)
                        .commit()
                }
            }
        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        if (navController.currentDestination?.id == R.id.dropletFragment) {
//            navController.popBackStack(
//                R.id.dropletFragment,
//                false
//            ) // Pop the specific fragment as it creates issues with lateinit map
//        }
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    private fun signOutFirebase()
    {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Timber.i("signed out from firebase")
                // ...
                // Toast.makeText(this,"MA: User has signed out from firebase",Toast.LENGTH_SHORT).show()
            }
    }
}