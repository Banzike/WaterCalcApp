package com.waterreserve.myapplication002.screens.titleOrUpdate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waterreserve.myapplication002.R
import com.waterreserve.myapplication002.databinding.FragmentTitleOrUpdateBinding
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

class TitleOrUpdateFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Επιλέξτε")
        val binding:FragmentTitleOrUpdateBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_title_or_update,container,false)
        binding.goToTitleButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_titleOrUpdateFragment_to_titleFragment))
        binding.updateReservoirButton.setOnClickListener {view:View->
            //(Navigation.createNavigateOnClickListener(R.id.action_titleOrUpdateFragment_to_waterLevelFragment))

            Navigation.findNavController(view).navigate(TitleOrUpdateFragmentDirections.actionTitleOrUpdateFragmentToWaterLevelFragment(
                isOld = false,
                idIs=0L//WaterLevelFragment should get Id from its View Model
            ))

        }
        return binding.root
    }

}