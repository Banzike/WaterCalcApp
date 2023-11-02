package android.example.myapplication002.screens.title



import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentSignInBinding
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.example.myapplication002.R
import androidx.fragment.app.Fragment

import android.example.myapplication002.databinding.FragmentTitleBinding
import android.example.myapplication002.screens.database.ReservoirDatabase
import android.example.myapplication002.screens.signInOrUp.SignInViewModel
import android.example.myapplication002.screens.signInOrUp.SignInViewModelFactory
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * Use the [TitleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

//need to add user retrieval get current user: status logged in variable which will  only be local. Else
//this brings more questions as to when it will be true or false. and how many users can be logged in on a single device. I'll make it 1.
//slide bar to logout that comes later
class TitleFragment : Fragment() {
    private lateinit var binding:FragmentTitleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Κεντρική")
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_title,container,false)
        binding.lifecycleOwner = this
        binding.insertReservoirButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_titleFragment_to_mapMethodFragment))
        binding.showMapButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_titleFragment_to_showMapFragment))
        return binding.root
    }
}