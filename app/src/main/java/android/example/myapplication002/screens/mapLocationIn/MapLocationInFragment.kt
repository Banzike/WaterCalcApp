package android.example.myapplication002.screens.mapLocationIn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentMapLocationInBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import timber.log.Timber


class  MapLocationInFragment : Fragment() {
    private lateinit var binding:FragmentMapLocationInBinding
    private lateinit var viewModel:MapLocationInViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_map_location_in,container,false)
        binding.lifecycleOwner = this

        //viewModel creation setup
        val application= requireNotNull(this.activity).application
        val dataSource= ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser= ReservoirDatabase.getInstance(application).userDatabaseDao
        val viewModelFactory=MapLocationInViewModelFactory(dataSource,dataSourceUser,application)
        val viewModel=ViewModelProvider(this,viewModelFactory).get(MapLocationInViewModel::class.java)

        binding.mapLocationInViewModel=viewModel





        binding.storeCoordinatesButton.setOnClickListener {
            Timber.i("storeButton pressed")
            viewModel.x=binding.enterX.text.toString()
            viewModel.y=binding.enterY.text.toString()
            Timber.i("User id is ${viewModel.user.value?.userId}")

        }
        //viewModel=ViewModelProvider(this).get(MapLocationInViewModel::class.java)
        binding.goOnButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mapLocationInFragment_to_roundOrRectangularFragment))

        return binding.root
    }

}