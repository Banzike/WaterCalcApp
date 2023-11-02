package android.example.myapplication002.screens.rect.insertDimens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentInsertDimensBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.example.myapplication002.screens.mapLocationIn.MapLocationInViewModel
import android.example.myapplication002.screens.mapLocationIn.MapLocationInViewModelFactory
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController


class InsertDimensFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Ορθογώνιο σχήμα")
        val binding: FragmentInsertDimensBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_insert_dimens,container,false)
        binding.lifecycleOwner = this

        //viewModel creation setup
        val application= requireNotNull(this.activity).application
        val dataSource= ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val viewModelFactory= InsertDimensViewModelFactory(dataSource,application)
        val viewModel= ViewModelProvider(this,viewModelFactory).get(InsertDimensViewModel::class.java)

        binding.insertDimensViewModel=viewModel


        binding.dimensNextButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_insertDimensFragment_to_insertDepthFragment))
        binding.dimensNextButton.setOnClickListener{
            if(binding.insertLength.text.isNullOrBlank() || binding.insertWidth.text.isNullOrBlank()||binding.insertLength.text.toString().toDouble()==0.0
                ||binding.insertWidth.text.toString().toDouble()==0.0){
                Toast.makeText(activity,"Μη έγκυρη τιμή",Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.lengthText = binding.insertLength.text.toString()
                viewModel.widthText = binding.insertWidth.text.toString()
                viewModel.storeMyDimens()
                Navigation.findNavController(it)
                    .navigate(InsertDimensFragmentDirections.actionInsertDimensFragmentToInsertDepthFragment())
            }
        }
        binding.insertLength.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                }

                else -> false
            }
        }


        binding.insertWidth.setOnEditorActionListener { v, actionId, event ->
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