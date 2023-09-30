package com.selcukileri.wakeup.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.selcukileri.wakeup.R
import com.selcukileri.wakeup.databinding.FragmentBookmarksBinding
import com.selcukileri.wakeup.databinding.FragmentMainBinding

class FragmentMain : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bookmarks.setOnClickListener{
            val action = FragmentMainDirections.actionFragmentMainToBookmarksFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.settings.setOnClickListener{
            val action = FragmentMainDirections.actionFragmentMainToSettingsFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

}