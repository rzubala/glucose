package com.zubala.rafal.glucose.ui.results

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
import com.zubala.rafal.glucose.logic.toString

class DayResults : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: DayResultFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.day_result_fragment, container, false
        )

        val date = DayResultsArgs.fromBundle(arguments!!).date
        val viewModel = ViewModelProviders.of(this).get(DayResultsViewModel::class.java)
        viewModel.date = date
        binding.viewModel = viewModel
        viewModel.getResults()

        binding.plus.setOnClickListener { viewModel.onPlus() }
        binding.minus.setOnClickListener { viewModel.onMinus() }

        viewModel.showProgressEvent.observe(this, Observer {
            if (it) {
                binding.resultsProgressBar.visibility = View.VISIBLE
                binding.header.text = ""
                reset(binding.onEmptyTime, binding.onEmptyResult)
                reset(binding.breakfastTime, binding.breakfastResult)
                reset(binding.dinnerTime, binding.dinnerResult)
                reset(binding.supperTime, binding.supperResult)
                viewModel.doneShowProgressEvent()
            }
        })

        viewModel.resultEvent.observe(this, Observer {
            it?.let {
                Log.i("DayResults", it.toString())
                binding.header.text = getString(R.string.results) + " " + viewModel.date.toString("dd.MM.yyyy")

                if (it.onEmpty.result > 0) {
                    binding.onEmptyTime.text = it.onEmpty.time
                    binding.onEmptyResult.text = it.onEmpty.result.toString()
                    markResult(binding.onEmptyResult, it.onEmpty.result, ON_EMPTY_LIMIT)
                }

                if (it.breakfast.result > 0) {
                    binding.breakfastTime.text = it.breakfast.time
                    binding.breakfastResult.text = it.breakfast.result.toString()
                    markResult(binding.breakfastResult, it.breakfast.result, AFTER_1H_LIMIT)
                }

                if (it.dinner.result > 0) {
                    binding.dinnerTime.text = it.dinner.time
                    binding.dinnerResult.text = it.dinner.result.toString()
                    markResult(binding.dinnerResult, it.dinner.result, AFTER_1H_LIMIT)
                }

               if (it.supper.result > 0) {
                    binding.supperTime.text = it.supper.time
                    binding.supperResult.text = it.supper.result.toString()
                    markResult(binding.supperResult, it.supper.result, AFTER_1H_LIMIT)
                }

                binding.resultsProgressBar.visibility = View.INVISIBLE
                viewModel.doneResults()
            }
        })

        return binding.root
    }

    private fun reset(time: TextView?, result: TextView?) {
        time?.let {
            time.text = "-"
        }
        result?.let {
            result.text = "-"
            result.setTextColor(ContextCompat.getColor(context!!, R.color.gray))
        }
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