package com.waterreserve.myapplication002.screens.roundOrRec

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waterreserve.myapplication002.R
import com.waterreserve.myapplication002.databinding.FragmentRoundOrRectangularBinding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation


class RoundOrRectangularFragment : Fragment() {
private lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Επιλογή σχήματος")
        // Inflate the layout for this fragment
        val binding: FragmentRoundOrRectangularBinding = DataBindingUtil.inflate(inflater,
        R.layout.fragment_round_or_rectangular,container,false)

        viewModel=ViewModelProvider(this).get(RoundOrRectangularViewModel::class.java)

        binding.wellImageButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_roundOrRectangularFragment_to_insertDiameterFragment))
        binding.rectangularImageButton.setOnClickListener((Navigation.createNavigateOnClickListener(R.id.action_roundOrRectangularFragment_to_insertDimensFragment)))
        return binding.root

    }


}