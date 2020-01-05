package com.zubala.rafal.glucose.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: MainFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false)

        val viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.mainModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}
