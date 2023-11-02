package android.example.myapplication002.screens.insertDepth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentInsertDepthBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.example.myapplication002.screens.well.insertDiameter.InsertDiameterViewModel
import android.example.myapplication002.screens.well.insertDiameter.InsertDiameterViewModelFactory
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import timber.log.Timber


class InsertDepthFragment : Fragment() {
    private lateinit var viewModel: InsertDiameterViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Βάθος")
        val binding: FragmentInsertDepthBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_insert_depth,container,false)
        val application= requireNotNull(this.activity).application
        val dataSource= ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val viewModelFactory= InsertDepthViewModelFactory(dataSource,application)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(InsertDepthViewModel::class.java)

        binding.insertDepthViewModel=viewModel

        //Pass depth to viewModel
        binding.storeDepthButton.setOnClickListener{

            if (binding.insertDepth.text.isNullOrBlank()||binding.insertDepth.text.toString().toDouble()==0.0){
                Toast.makeText(activity, "Μη έγκυρη τιμή", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.newDepthString = binding.insertDepth.text.toString()
                viewModel.updateDepth()
                Navigation.findNavController(it).navigate(InsertDepthFragmentDirections.actionInsertDepthFragmentToTitleOrUpdateFragment())
            }
        }

//        binding.insertLvlButton.setOnClickListener {
//            if (binding.insertlvl.text.isNullOrBlank()) {
//                Toast.makeText(activity, "Μη έγκυρη τιμή", Toast.LENGTH_SHORT).show()
//            } else {
//                viewModel.newWaterLevelString = binding.insertlvl.text.toString()
//                viewModel.updateWaterLevel()
//            }
//        }


        binding.helpDepthButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_insertDepthFragment_to_insertDepthHelpFragment))
        //binding.depthNextButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_insertDepthFragment_to_titleOrUpdateFragment))
        binding.insertDepth.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }

                else -> false
            }
        }

        return binding.root
    }

}