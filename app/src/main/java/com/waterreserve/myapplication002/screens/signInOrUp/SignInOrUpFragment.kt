package com.waterreserve.myapplication002.screens.signInOrUp

import com.waterreserve.myapplication002.MainActivityViewModel
import com.waterreserve.myapplication002.R
import com.waterreserve.myapplication002.databinding.FragmentSignInOrUpBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import timber.log.Timber

class SignInOrUpFragment:Fragment() {
    private lateinit var viewModelMA: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Επιλέξτε")
        Timber.i("onCreate called")
        //View Binding is better here. Databinding is declared below in comments
        val binding:FragmentSignInOrUpBinding=FragmentSignInOrUpBinding.inflate(inflater,container,false)
        // val binding:FragmentSignInOrUpBinding=DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in_or_up,container,false)
        viewModelMA = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        viewModelMA.getUser()
        Timber.i("get user executed")
        binding.signInButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInOrUpFragment_to_signInFragment))
        binding.signUpButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInOrUpFragment_to_signUpFragment))
      //  binding.skipButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_signInOrUpFragment_to_titleFragment))

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy called")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume called")
    }

}