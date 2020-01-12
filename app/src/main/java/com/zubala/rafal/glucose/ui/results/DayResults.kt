package com.zubala.rafal.glucose.ui.results

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.databinding.DayResultFragmentBinding
import com.zubala.rafal.glucose.logic.getCurrentDateTime
import com.zubala.rafal.glucose.logic.toString
import com.zubala.rafal.glucose.ui.main.openSheet

class DayResults : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: DayResultFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.day_result_fragment, container, false
        )

        binding.header.text = getString(R.string.results) + " " + getCurrentDateTime().toString("dd.MM.yyyy")

        val arguments = DayResultsArgs.fromBundle(arguments!!)
        val viewModelFactory = DayResultsViewModelFactory(openSheet(context!!, arguments.accountData))
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(DayResultsViewModel::class.java)

        val dayResults = viewModel.getResults()
        Log.i("DayResults", dayResults.toString())

        return binding.root
    }
}