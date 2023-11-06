package com.waterreserve.myapplication002.screens.mapmethod

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waterreserve.myapplication002.R
import androidx.databinding.DataBindingUtil
import com.waterreserve.myapplication002.databinding.FragmentMapMethodBinding
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



class MapMethodFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setTitle("Μέθοδος Υποβολής Θέσης")
        val binding: FragmentMapMethodBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_map_method,
        container,false)

        //binding.manualUpdateButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mapMethodFragment_to_mapLocationInFragment))
        binding.autoUpdateButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mapMethodFragment_to_mapsAutoFragment))
        binding.actualManualMapButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_mapMethodFragment_to_mapsFragment))

        return binding.root
    }


//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MapMethodFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MapMethodFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}