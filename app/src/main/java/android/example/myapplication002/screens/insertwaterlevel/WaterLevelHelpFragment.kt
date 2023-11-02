package android.example.myapplication002.screens.insertwaterlevel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentWaterLevelHelpBinding
import androidx.databinding.DataBindingUtil


class WaterLevelHelpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentWaterLevelHelpBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_water_level_help,container,false)
        return binding.root
    }
}