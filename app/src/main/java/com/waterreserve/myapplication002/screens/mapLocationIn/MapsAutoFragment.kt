package com.waterreserve.myapplication002.screens.mapLocationIn

import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import com.waterreserve.myapplication002.R
import com.waterreserve.myapplication002.databinding.FragmentMapsAutoBinding
import com.waterreserve.myapplication002.screens.database.ReservoirDatabase
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.room.Database
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.lang.ref.Reference
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.net.NetworkInfo

class MapsAutoFragment : Fragment() {
    private var long:Double=0.0
    private var lat:Double=0.0
    lateinit var client: FusedLocationProviderClient
    lateinit var binding: FragmentMapsAutoBinding
    lateinit var viewModel: MapsAutoViewModel
    private lateinit var map: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var cm:ConnectivityManager
    var networkInfo:NetworkInfo? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sifnos, Greece.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sifnos = LatLng(36.976840, 24.702769)
        map = googleMap //initialize map
        val update = CameraUpdateFactory.newLatLngZoom(sifnos, 10.0f)
        Timber.i("update = $update")
        map.moveCamera(update)
        setupLocClient()
        fetchLocation()
//        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Αυτόματη Εύρεση Θέσης")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps_auto,container,false)
        binding.lifecycleOwner=this
        //viewModel creation setup
        val application= requireNotNull(this.activity).application
        val dataSource= ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser=ReservoirDatabase.getInstance(application).userDatabaseDao
        val viewModelFactory=MapsAutoViewModelFactory(dataSource, dataSourceUser, application)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(MapsAutoViewModel::class.java)

        cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo =cm.activeNetworkInfo


        locationManager=requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        binding.goNextButton.setOnClickListener{
         viewModel.coordinateLat=lat
         viewModel.coordinateLong=long
         viewModel.storeCoordinates()
         Timber.i("Fragment coordinates  are: $lat and $long")
         Navigation.findNavController(it).navigate(R.id.action_mapsAutoFragment_to_roundOrRectangularFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapauto) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setupLocClient() {
        client= LocationServices.getFusedLocationProviderClient(requireActivity())
        Timber.i("client has been set up!")
    }

    private fun requestLocalPermissions(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),101)
        Timber.i("requestLocalPermission called")
        fetchLocation()
    }

    private fun fetchLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval=1000
        }

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
           // Toast.makeText(requireContext(),"Δεν έχουν παραχωρηθεί οι απαραίτητες άδειες",Toast.LENGTH_SHORT).show()
            Timber.i("No permission for fine and coarse location")
            requestLocalPermissions()
            return
        }

        if(networkInfo!=null&&networkInfo?.isConnectedOrConnecting!!) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(requireContext(), "Λήψη θέσης...", Toast.LENGTH_SHORT).show()
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation

                        if (location != null) {
                            Timber.i("Location received: $location")
                            val latLng = LatLng(location.latitude, location.longitude)
                            map.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_inv))
                                    .title("Θέση")
                            )
                            lat = location.latitude
                            long = location.longitude
                            val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
                            // Toast.makeText(requireContext(), "Η θέση βρέθηκε", Toast.LENGTH_SHORT).show()
                            map.moveCamera(update)
                            client.removeLocationUpdates(this)//stops the callback from being called anymore
                            Toast.makeText(context,"Ολοκληρώθηκε",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                client.requestLocationUpdates(locationRequest, locationCallback, null)

            } else {
                Toast.makeText(
                    requireContext(),
                    "Ενεργοποιήστε την τοποθεσία και προσπαθήστε ξανά",
                    Toast.LENGTH_SHORT
                ).show()
                // Location services are not enabled, prompt user to enable them
                // Open location settings
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                findNavController().navigate(R.id.action_mapsAutoFragment_to_mapMethodFragment)
            }
        }else{
            Toast.makeText(
                requireContext(),
                "Ενεργοποιήστε τα δεδομένα και προσπαθήστε ξανά",
                Toast.LENGTH_SHORT
            ).show()
            // Location services are not enabled, prompt user to enable them
            // Open location settings
            startActivity(Intent(Settings.ACTION_DATA_USAGE_SETTINGS))
            findNavController().navigate(R.id.action_mapsAutoFragment_to_mapMethodFragment)

        }
    }

//    private fun fetchLocation() {
//        Timber.i("fetchLocation called")
//        val task=client.lastLocation              // αυτο προκαλουσε το θεμα!!!!!
//
//        //test
//        val locationRequest = LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval = 1000
//        }
//
//
//        if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)
//            !=PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.ACCESS_COARSE_LOCATION)
//            !=PackageManager.PERMISSION_GRANTED){
//            Timber.i("No permission for fine and coarse location")
//            requestLocalPermissions()
//            return
//        }
//        else{
//            Timber.i("Permissions have been granted")
//        }


//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            // Location services are enabled
//            Toast.makeText(requireContext(), "Location services are enabled", Toast.LENGTH_SHORT).show()
//
//
//            task.addOnSuccessListener {
//                if (it != null) {
//                    Timber.i("it is: $it")
//                    // Toast.makeText(requireActivity(),"Latitude: ${it.latitude} Longitude:${it.longitude}",Toast.LENGTH_SHORT).show()
//                    val location = task.result
//                    val latLng = LatLng(location.latitude, location.longitude)
//                    //create a marker at the exact location
//                    map.addMarker(
//                        MarkerOptions()
//                            .position(latLng)
//                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_inv))
//                            .title("Βρίσκεστε εδώ!")
//                    )
//                    lat = location.latitude
//                    long = location.longitude
//                    // create an object that will specify how the camera will be updated
//                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
//                    Toast.makeText(requireContext(), "Η θέση βρέθηκε", Toast.LENGTH_SHORT).show()
//                    map.moveCamera(update)
//                    //Save the location data to the database
//                    //Move camera to new location
//                    //ref.setValue(location)
//                }else { //if(it==null) {
//                    Toast.makeText(
//                        requireActivity(),"Ανοίξτε τοποθεσία και δεδομένα",Toast.LENGTH_LONG).show()
//                    // findNavController().navigate(R.id.action_mapsAutoFragment_to_mapMethodFragment)
//                    }
//                 }
//
//            }else {
//                // Location services are not enabled, prompt user to enable them
//                Toast.makeText(requireContext(), "Location services are not enabled", Toast.LENGTH_SHORT).show()
//
//                // Open location settings
//             //   startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            }
//
//
//    }

//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray) {
//        //check if the request code matches the REQUEST_LOCATION
//        if (requestCode == REQUEST_LOCATION)
//        {
//            //check if grantResults contains PERMISSION_GRANTED.If it does, call getCurrentLocation()
//            if (grantResults.size == 1 && grantResults[0] ==
//                PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation()
//            } else {
//                //if it doesn`t log an error message
//                Log.e(TAG, "Location permission has been denied")
//            }
//        }
//    }
}