package com.zubala.rafal.glucose.ui.results

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.databinding.DayResultFragmentBinding
import com.zubala.rafal.glucose.logic.getCurrentDateTime
import com.zubala.rafal.glucose.logic.toString

class DayResults : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: DayResultFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.day_result_fragment, container, false
        )

        binding.header.text = getString(R.string.results) + " " + getCurrentDateTime().toString("dd.MM.yyyy")

        val viewModel = ViewModelProviders.of(this).get(DayResultsViewModel::class.java)
        viewModel.getResults()

        viewModel.resultEvent.observe(this, Observer {
            it?.let {
                Log.i("DayResults", it.toString())

                binding.onEmptyTime.text = it.onEmpty.time
                binding.onEmptyResult.text = it.onEmpty.result.toString()
                markResult(binding.onEmptyResult, it.onEmpty.result, ON_EMPTY_LIMIT)

                binding.breakfastTime.text = it.breakfast.time
                binding.breakfastResult.text = it.breakfast.result.toString()
                markResult(binding.breakfastResult, it.breakfast.result, AFTER_1H_LIMIT)

                binding.dinnerTime.text = it.dinner.time
                binding.dinnerResult.text = it.dinner.result.toString()
                markResult(binding.dinnerResult, it.dinner.result, AFTER_1H_LIMIT)

                binding.supperTime.text = it.supper.time
                binding.supperResult.text = it.supper.result.toString()
                markResult(binding.supperResult, it.supper.result, AFTER_1H_LIMIT)

                viewModel.doneResults()
            }
        })

        return binding.root
    }

    private fun markResult(view: TextView?, result: Int, limit: Int) {
        view?.let {
            if (result <= limit) {
                view.setTextColor(ContextCompat.getColor(context!!, R.color.green))
            } else {
                view.setTextColor(ContextCompat.getColor(context!!, R.color.red))
            }
        }
    }
}