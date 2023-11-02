package android.example.myapplication002.screens.well.insertDiameter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.example.myapplication002.R
import android.example.myapplication002.databinding.FragmentInsertDiameterHelpBinding
import androidx.databinding.DataBindingUtil


class InsertDiameterHelpFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentInsertDiameterHelpBinding =DataBindingUtil.inflate(inflater,R.layout.fragment_insert_diameter_help,container,false)


        // Inflate the layout for this fragment
        return binding.root
    }

}