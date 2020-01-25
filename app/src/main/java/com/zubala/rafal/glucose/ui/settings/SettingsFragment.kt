package com.zubala.rafal.glucose.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: SettingsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)

        val viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        binding.settingsModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}