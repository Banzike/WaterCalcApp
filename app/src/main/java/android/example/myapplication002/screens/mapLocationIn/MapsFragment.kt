package android.example.myapplication002.screens.mapLocationIn

import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentMapsBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions


class MapsFragment : Fragment() {
    private lateinit var binding: FragmentMapsBinding
    private lateinit var viewModel: MapsViewModel
    private var latLng:LatLng?=null

//    private val callback = OnMapReadyCallback {googleMap ->
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//
//
////        val sifnos = LatLng(36.964788, 24.711138)
////       googleMap.addMarker(MarkerOptions().position(sifnos).title("Marker in Sifnos"))
////        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sifnos))
//    }

    private val onMapReady = OnMapReadyCallback { googleMap ->
        val mMap = googleMap
        val sifnos = LatLng(36.964788, 24.711138)

        //get latlong for corners for specified place
        val one = LatLng(37.043199, 24.627862)
        val two = LatLng(36.899960, 24.763261)
        val builder = LatLngBounds.Builder()

        //add them to builder
        builder.include(one)
        builder.include(two)
        val bounds = builder.build()

        //get width and height to current display screen
        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels

        // 20% padding
        val padding = (width * 0.0).toInt()

        //set latlong bounds
        mMap.setLatLngBoundsForCameraTarget(bounds)

        //move camera to fill the bound to screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))

        //set zoom to level to current so that you won't be able to zoom out viz. move outside bounds
        mMap.setMinZoomPreference(mMap.cameraPosition.zoom)

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sifnos))

        googleMap.setOnMapClickListener {
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.black_inv))
                    .flat(false)
                    .position(it)
            )
            latLng = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Χειροκίνητη Υποβολή Θέσης")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)
        //viewModel creation setup
        val application = requireNotNull(this.activity).application
        val dataSource = ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser = ReservoirDatabase.getInstance(application).userDatabaseDao
        val viewModelFactory = MapsViewModelFactory(dataSource, dataSourceUser, application)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(MapsViewModel::class.java)

        binding.mapsViewModel = viewModel
        binding.goToChoiceButton.setOnClickListener {
            if (latLng!=null) {
                viewModel.coordinates = latLng!!
                viewModel.updateCoordinates()
                Navigation.findNavController(it)
                    .navigate(MapsFragmentDirections.actionMapsFragmentToRoundOrRectangularFragment())
            } else {
                Toast.makeText(context, "Επιλέξτε σημείο στον χάρτη", Toast.LENGTH_SHORT).show()
            }
        }
            return binding.root

    }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment?
            mapFragment?.getMapAsync(onMapReady)
        }
}