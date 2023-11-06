package com.waterreserve.myapplication002.screens.user

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import com.waterreserve.myapplication002.MainActivityViewModel
import com.waterreserve.myapplication002.R
import com.waterreserve.myapplication002.databinding.FragmentUserBinding
import com.waterreserve.myapplication002.screens.database.ReservoirDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class UserFragment: Fragment(){
    private lateinit var viewModel:UserViewModel
    private lateinit var viewModelMA: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Σελίδα Χρήστη")
        val binding:FragmentUserBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_user,container,false)
        binding.lifecycleOwner

        //viewModel creation setup
        val application= requireNotNull(this.activity).application
        val dataSourceR=ReservoirDatabase.getInstance(application).reservoirDatabaseDao
        val dataSourceU=ReservoirDatabase.getInstance(application).userDatabaseDao
        val dataSourceM=ReservoirDatabase.getInstance(application).measurementDatabaseDao
        val viewModelFactory=UserViewModelFactory(dataSourceR,dataSourceU,dataSourceM,application)
        viewModel=ViewModelProvider(this,viewModelFactory).get(UserViewModel::class.java)
        binding.userViewModel=viewModel
        binding.lifecycleOwner=this
        viewModelMA = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)


        binding.deleteUserClickableText.setOnClickListener {
            showAlert(it)
        }
        return binding.root
    }

    fun showAlert(view: View){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Προσοχή")
            .setMessage("Ο λογαριασμός και οι σχετικές του πληροφορίες θα διαγραφούν. Είστε σίγουροι πως θέλετε να τον διαγράψετε;")
            .setNegativeButton("Όχι"){ dialog, which->
            }
            .setPositiveButton("Ναι"){ dialog, which->
               signOutFirebase()
               viewModel.deleteUser()
               viewModelMA.signOut()
            //  findNavController().navigate(R.id.action_userFragment_to_signInOrUpFragment)
            }
            .show()
    }


    private fun signOutFirebase()
    {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                // ...
                // Toast.makeText(requireContext(),"SignIn: User has signed out from firebase",Toast.LENGTH_SHORT).show(
                Timber.i("User has signed out")
                viewModelMA.getUser()
            }
    }
}