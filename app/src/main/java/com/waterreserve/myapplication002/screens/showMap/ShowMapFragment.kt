package com.waterreserve.myapplication002.screens.showMap

import com.waterreserve.myapplication002.R
import com.waterreserve.myapplication002.screens.database.ReservoirDatabase
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.waterreserve.myapplication002.databinding.FragmentShowMapBinding
import android.widget.ArrayAdapter
import com.waterreserve.myapplication002.screens.database.Reservoir
import com.waterreserve.myapplication002.screens.database.ReservoirDatabaseDao
import android.widget.AdapterView
import android.widget.AdapterView.*
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import timber.log.Timber
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


class ShowMapFragment : Fragment(), OnMarkerClickListener, OnItemSelectedListener {
    private lateinit var binding: FragmentShowMapBinding
    private lateinit var viewModel: ShowMapViewModel
    private lateinit var map: GoogleMap
    private lateinit var color: String
    private var userId: Long = 0L
    private var idlist = mutableListOf<Long>()
    private var colorlist = mutableListOf<String>()
    private var markerIDlist = mutableListOf<String>()
    private val addMarkers = MutableLiveData<Int>(0)
    private var userList = mutableListOf<Boolean>()
    private var markerList= mutableListOf<Marker>()
    private var initialized = false
    private val onMapReady = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap //initialize map
        val sifnos = LatLng(36.976840, 24.702769)
        val update = CameraUpdateFactory.newLatLngZoom(sifnos, 11.7f)
        map.moveCamera(update)
        addMarkers.value = 1
//        Timber.i("before the observer")

        viewModel.start.observe(viewLifecycleOwner, Observer<Boolean> {
           // Timber.i("observer accessed")
            if (viewModel.display.value!!) {
                displayMarkers(false)
                initialized = true
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().setTitle("Χάρτης")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_map, container, false)
        //  viewModel creation setup
        val application = requireNotNull(this.activity).application
        val dataSource = ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val datasourceUser = ReservoirDatabase.getInstance(application).userDatabaseDao
        val viewModelFactory = ShowMapViewModelFactory(dataSource, datasourceUser, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ShowMapViewModel::class.java)
        binding.lifecycleOwner = this
        binding.showMapViewModel = viewModel


        //Adding spinners for filters
        val spinner: Spinner = binding.filterSpinner
        //  val items = arrayOf("Προβολή όλων", "Προβολή μόνο του χρήστη")
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.show_from_users_array,
            android.R.layout.simple_spinner_item
        )
            .also { arrayAdapter ->
                // Specify the layout to use when the list of choices appears
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                spinner.adapter = arrayAdapter
            }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val selection = "Τα δικά σας"
        val spinnerPosition: Int = adapter.getPosition(selection)
        val selected = spinner.setSelection(spinnerPosition)
       // Timber.i("selected is $selected")
        spinner.onItemSelectedListener = this

        return binding.root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
       // Timber.i("onItemSelected is called!")
        if(initialized) {
            when (position) {
                0 -> setMarkerVisibility(true)
                1 -> setMarkerVisibility(false)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
      //  Timber.i("Nothing has been selected")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.show_map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(onMapReady)
    }


    override fun onMarkerClick(p0: Marker): Boolean {//goes to droplet or refreshes if reservoir has been deleted
        activity?.let {
            val index = markerIDlist.indexOf(p0.id)
         //   Timber.i("Marker index is $index")

            if(index<0)
        {
            Navigation.findNavController(it, R.id.myNavHostFragment).navigate(
                ShowMapFragmentDirections
                    .actionShowMapFragmentSelf())
        }
             else if (userList[index]) {
                Navigation.findNavController(it, R.id.myNavHostFragment).navigate(
                    ShowMapFragmentDirections
                        .actionShowMapFragmentToDropletFragment(
                            idIs = idlist[index],
                            imageIs = colorlist[index]//we pass it here so it can be instantly displayed in the droplet fragment
                        )
                    
                )
                Timber.i("idIs: $idlist[index]")
                markerIDlist.clear()
            }
            else if(!userList[index]) {
                Toast.makeText(context, "Δεν είναι δικός σας ο ταμιευτήρας", Toast.LENGTH_SHORT).show()
            }
            //Safety net for event of marker being clicked when it has been deleted. Case can happen using the back button when in the droplet fragment for example

        }
        return true
    }

    private fun displayMarkers(showAll: Boolean) {
        if(markerList.size==0) {
            Timber.i("display markers is accessed")
            //searches the list based on color, returns the images resources and the id of the reservoir, creates a marker for each entity in the reserve
            userId = viewModel.currentUserId
            Timber.i("Current user's id is $userId")
            for (i in 0 until viewModel.rowNum) {
                color = viewModel.colorList[i]
                colorlist.add(color)
                val resource =
                    when (color) { //resource is to be used as the current marker's image on the map
                        "blue" -> R.drawable.blue_inv
                        "teal" -> R.drawable.teal_inv
                        "green" -> R.drawable.green_inv
                        "yellow" -> R.drawable.yellow_inv
                        else -> R.drawable.red_inv
                    }
                val compareID = viewModel.userList[i] == userId

                //a list of 1s and 0s containing 1 for each reserve owned by the current user
                userList.add(compareID)

                //coordinates of the marker
                val coords = LatLng(viewModel.latList[i], viewModel.lonList[i])
                idlist.add(viewModel.idList[i])
                val compare = userList[i] || showAll
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(coords)
                        .icon(BitmapDescriptorFactory.fromResource(resource))
                        .visible(compare)
                )
                markerList.add(marker!!)
                markerIDlist.add(marker.id)
                map.setOnMarkerClickListener(this)
                Timber.i("marker $i is visible: $compare")
              //  Timber.i("id of i is ${idlist[i]}")
            }
           // Timber.i("idList size is ${idlist.size}")
        }
        centerMap()
    }

    private fun setMarkerVisibility(showAll: Boolean) {
        for(i in 0 until markerList.size){
            val compare = userList[i] || showAll
            markerList[i].isVisible=compare
        }
        centerMap()
    }



    //Centers the camera depending on the visible markers at the time
    private fun centerMap(){

     var latmin: Double = 90.0
     var lonmin: Double = 180.0
     var latmax: Double = -180.0
     var lonmax: Double = -90.0

     var markerListVisibleSize: Int = 0

     for (i in 0 until markerList.size) {
         if (markerList[i].isVisible) {
             markerListVisibleSize += 1
         }
      }

      if (markerListVisibleSize > 1) {
        for (i in 0 until markerList.size) {
            if (markerList[i].isVisible) {
                if (lonmin > viewModel.lonList[i]) {
                    lonmin = viewModel.lonList[i]
                }
                if (lonmax < viewModel.lonList[i]) {
                    lonmax = viewModel.lonList[i]
                }
                if (latmin > viewModel.latList[i]) {
                    latmin = viewModel.latList[i]
                }
                if (latmax < viewModel.latList[i]) {
                    latmax = viewModel.latList[i]
                }
            }
        }
            Timber.i("latmin: $latmin")
            Timber.i("latmax: $latmax")
            //gotta catch:  java.lang.IllegalArgumentException: southern latitude exceeds northern latitude (90.0 > -90.0)

            val southWestBound = LatLng(latmin, lonmin)
            val northEastBound = LatLng(latmax, lonmax)
            val bounds = LatLngBounds(southWestBound, northEastBound)
            val update = CameraUpdateFactory.newLatLngBounds(bounds, 180)
            map.animateCamera(update)


         }
        else if (markerListVisibleSize == 1) {//if there is only one visible reserve the map is centered around that point
            var position:Int=0
            for(i in 0 until markerList.size){
                if(markerList[i].isVisible){
                    position=i
                    break
                }
            }
            val markerCoords = LatLng(viewModel.latList[position], viewModel.lonList[position])
            val update = CameraUpdateFactory.newLatLngZoom(markerCoords, 11.7f)
            // map.moveCamera(update)
            map.animateCamera(update)
            }
        else if (markerListVisibleSize == 0) {//if there are no reserves, map is centered on sifnos
            val sifnos = LatLng(36.976840, 24.702769)
            val update = CameraUpdateFactory.newLatLngZoom(sifnos, 11.7f)
       //   map.moveCamera(update)
            map.animateCamera(update)
            }
    }
}