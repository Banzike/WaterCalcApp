package android.example.myapplication002.screens.droplet

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.example.myapplication002.NotificationReceiver
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentDropletBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


//DataStore creation
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
object PreferenceKeys{
    val ID_VALUE= longPreferencesKey("id_value")
}

class DropletFragment : Fragment() {
    private lateinit var viewModel: DropletViewModel
    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var Id:Long=0L

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Δεξαμενή")
        val binding: FragmentDropletBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_droplet, container, false)
        val application = requireNotNull(this.activity).application
        val dataSource = ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser = ReservoirDatabase.getInstance(application).userDatabaseDao
        val dataSourceMeasurement =
            ReservoirDatabase.getInstance(application).measurementDatabaseDao
        val viewModelFactory =
            DropLetViewModelFactory(dataSource, dataSourceUser, dataSourceMeasurement, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DropletViewModel::class.java)

        binding.dropletViewModel = viewModel
        binding.lifecycleOwner = this

        val args = DropletFragmentArgs.fromBundle(requireArguments())
        Id=args.idIs

        Timber.i("Args image is: ${args.imageIs}")
        //Timber.i("Image in viemodel is ${viewModel.color.value}")

        Timber.i("Fragment is created")
        viewModel.id = Id //crash if id is null
        Timber.i("id is $Id")


        val resource = when (args.imageIs) {
            "blue" -> R.drawable.blue
            "teal" -> R.drawable.teal
            "green" -> R.drawable.green
            "yellow" -> R.drawable.yellow
            else -> R.drawable.red
        }
        // set the image resource based on the imageIs value decided by the waterlevel viewModel
        binding.dropImage.setImageResource(resource)
        binding.updateReservoirButton.setOnClickListener {view: View ->
            Navigation.findNavController(view).navigate(
                DropletFragmentDirections.actionDropletFragmentToWaterLevelFragment(
                    idIs = args.idIs,
                    isOld = true
                )
            )
        }

        binding.deletThisButton.setOnClickListener {
            showAlert(it)
        }

        binding.changeNotificationPeriod.setOnClickListener{
            //Insert code*********
            val types = arrayOf("1 μέρα", "2 μέρες", "3 μέρες", "4 μέρες"
                , "5 μέρες", "6 μέρες", "7 μέρες")
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("")
                .setItems(types) { dialog, which ->
                    when (which) {
                        0 -> Timber.i("0 selected")
                        1 -> Timber.i("1 selected")
                        3 -> Timber.i("3 selected")
                        4 -> Timber.i("4 selected")
                        5 -> Timber.i("5 selected")
                        6 -> Timber.i("6 selected")
                    }
                    viewModel.setUpdateFrequency(which+1)
                }
                .show()
        }

        binding.createNotificationButton.setOnClickListener {
            createNotification()
        }

        binding.backToMapButton.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_dropletFragment_to_showMapFragment)
        }

        binding.goToTitleButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_dropletFragment_to_titleFragment)
        }
        return binding.root
    }


    private fun showAlert(view: View){

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Προσοχή!")
            .setMessage("Η δεξαμενή θα διαγραφεί οριστικά. Είστε σίγουροι πως θέλετε να συνεχίσετε;")
            .setNegativeButton("Όχι"){ dialog, which->
            }
            .setPositiveButton("Ναι"){ dialog, which->
                viewModel.deleteThisReserve()
                Navigation.findNavController(view)
                .navigate(R.id.action_dropletFragment_to_showMapFragment)
                Toast.makeText(requireContext(),"Η δεξαμενή διαγράφηκε",Toast.LENGTH_SHORT).show()
            }
            .show()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotification(){
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocalPermissions()
            return
        }
        else {
            val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            //Datastore
            CoroutineScope(Dispatchers.Default).launch {
                insertIDValue(Id)
            }
            val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)
            val alarmManager:AlarmManager= requireContext().getSystemService(AlarmManager::class.java)
            val delay = System.currentTimeMillis() + (1  * 1000) // How long from now the notification will be displayed
            alarmManager.set(AlarmManager.RTC_WAKEUP, delay, pendingIntent)
            Toast.makeText(requireContext(),"Δημιουργήθηκε ειδοποίηση",Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun insertIDValue(idIs:Long) {
        context?.dataStore?.edit { settings ->
            settings[PreferenceKeys.ID_VALUE]=idIs
            Timber.i("Inserting value: $idIs")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestLocalPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            101)
        Timber.i("requestLocalPermission called")
        createNotification()
    }
}