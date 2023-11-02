package android.example.myapplication002.screens.well.insertDiameter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentInsertDiameterBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.example.myapplication002.screens.rect.insertDimens.InsertDimensFragmentDirections
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation


class InsertDiameterFragment : Fragment() {
    private lateinit var binding: FragmentInsertDiameterBinding
    private lateinit var viewModel: InsertDiameterViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Κυλινδρικό σχήμα")

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_insert_diameter,container,false)

        val application= requireNotNull(this.activity).application
        val dataSource= ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val viewModelFactory= InsertDiameterViewModelFactory(dataSource,application)
        val viewModel=ViewModelProvider(this,viewModelFactory).get(InsertDiameterViewModel::class.java)

        binding.insertdiameterViewModel=viewModel
        //for live data binding
        //binding.lifecycleOwner(this)

        binding.helpDiameterButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_insertDiameterFragment_to_insertDiameterHelpFragment))

        binding.updateDiameterButton.setOnClickListener {

            if(binding.insertDiameter.text.isNullOrBlank()||binding.insertDiameter.text.toString().toDouble()==0.0){
                Toast.makeText(activity,"Μη έγκυρη τιμή",Toast.LENGTH_SHORT).show()
            }

            else {
                viewModel.newDiameterString = binding.insertDiameter.text.toString()
                viewModel.updateDiameter()
                Navigation.findNavController(it)
                    .navigate(InsertDiameterFragmentDirections.actionInsertDiameterFragmentToInsertDepthFragment())
            }
        }

//        binding.diameterNextButton.setOnClickListener (Navigation.createNavigateOnClickListener(R.id.action_insertDiameterFragment_to_insertDepthFragment))
//
//            //viewModel.newDiameter = binding.insertDiameter.text.toString().toDouble()


        return binding.root

    }


}