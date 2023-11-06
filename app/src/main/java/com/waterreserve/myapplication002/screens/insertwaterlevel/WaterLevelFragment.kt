package com.waterreserve.myapplication002.screens.insertwaterlevel

import android.content.Context
import com.waterreserve.myapplication002.MainActivityViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waterreserve.myapplication002.R
import com.waterreserve.myapplication002.databinding.FragmentWaterLevelBinding
import com.waterreserve.myapplication002.screens.database.ReservoirDatabase
import com.waterreserve.myapplication002.screens.database.MeasurementDatabaseDao
import com.waterreserve.myapplication002.screens.database.UserDatabaseDao
import com.waterreserve.myapplication002.screens.droplet.DropletFragmentArgs
import com.waterreserve.myapplication002.screens.well.insertDiameter.InsertDiameterViewModel
import com.waterreserve.myapplication002.screens.well.insertDiameter.InsertDiameterViewModelFactory
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import timber.log.Timber


class WaterLevelFragment : Fragment() {

    private lateinit var binding: FragmentWaterLevelBinding
    private lateinit var viewModel: WaterLevelViewModel
    private lateinit var viewModelMA: MainActivityViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.i("Waterlevel entered")
        requireActivity().setTitle("Επίπεδο νερού")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_water_level,
            container,
            false
        )
        viewModelMA = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        val application = requireNotNull(this.activity).application
        val dataSource = ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceUser = ReservoirDatabase.getInstance(application).userDatabaseDao
        val dataSourceMeasurement=ReservoirDatabase.getInstance(application).measurementDatabaseDao
        val viewModelFactory = WaterLevelViewModelFactory(dataSource,dataSourceUser,dataSourceMeasurement, application)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(WaterLevelViewModel::class.java)

        binding.lifecycleOwner = this
        binding.waterlevelViewModel = viewModel

        val args = WaterLevelFragmentArgs.fromBundle(requireArguments())
        viewModel.idArg=args.idIs
        viewModel.isOld=args.isOld
        Timber.i("Id is ${args.idIs}")
        Timber.i("isOld is ${args.isOld}")

        viewModel.getReserve()

        binding.insertLvlButton.setOnClickListener {view:View->
            checkUser(view)
            checkIfValidAndSet()
//            if (binding.insertlvl.text.isNullOrBlank()||binding.insertlvl.text.toString().toDouble()>viewModel.waterDepth) {
//                Toast.makeText(activity, "Μη έγκυρη τιμή", Toast.LENGTH_SHORT).show()
//            } else {
//                viewModel.newWaterLevelString = binding.insertlvl.text.toString()
//                viewModel.updateWaterLevel()
//                binding.goNextButton.isEnabled=true
//                binding.goNextButton.alpha= 1.0f
//            }
        }

        binding.helpWaterLevelButton.setOnClickListener((Navigation.createNavigateOnClickListener(R.id.action_waterLevelFragment_to_waterLevelHelpFragment)))
        binding.goNextButton.setOnClickListener { view: View ->
            viewModel.insertMeasurement()
            Navigation.findNavController(view).navigate(

                WaterLevelFragmentDirections.actionWaterLevelFragmentToDropletFragment(
                    imageIs = viewModel.imageChosen.value,
                    idIs = viewModel.idOfReserve
                )
            )
        }
        
        
        binding.insertlvl.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    checkUser(v)
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    //Does same thing as pushing the first button
                    checkIfValidAndSet()
                    true
                }

                else -> false
            }
        }
        return binding.root
    }

    private fun checkIfValidAndSet(){
        if (binding.insertlvl.text.isNullOrBlank()||binding.insertlvl.text.toString().toDouble()>viewModel.waterDepth) {
            Toast.makeText(activity, "Μη έγκυρη τιμή", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.newWaterLevelString = binding.insertlvl.text.toString()
            viewModel.updateWaterLevel()
            binding.goNextButton.isEnabled=true
            binding.goNextButton.alpha= 1.0f
        }
    }

    private fun checkUser(view:View){
        if(viewModel.thisUser.userName!=viewModelMA.username.value)
        {
            Timber.i("Cant change this value DAWG")
            Toast.makeText(requireContext(),"Δεν μπορείτε να προσθέσετε μέτρηση",Toast.LENGTH_SHORT).show()
            Navigation.findNavController(view).navigate(R.id.action_waterLevelFragment_to_titleFragment)
        }
        else{
            Timber.i("ολα καλα")
        }
    }
}
//    private    fun hideButton(view: View){
//        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.hideSoftInputFromWindow(view.windowToken,0)
//        }


//binding.goNextButton.setOnClickListener { view: View ->
//    Navigation.findNavController(view).navigate(
//        WaterLevelFragmentDirections.actionWaterLevelFragmentToDropletFragment(
//            imageIs = viewModel.imageChosen.value
//        )
//    )
//}

//binding.insertLvlButton.setOnClickListener {
//    if (binding.insertlvl.text.isNullOrBlank()) {
//        Toast.makeText(activity, "Μη έγκυρη τιμή", Toast.LENGTH_SHORT).show()
//    } else {
//        viewModel.newWaterLevelString = binding.insertlvl.text.toString()
//        viewModel.updateWaterLevel()
//    }
//}
