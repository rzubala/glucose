package com.zubala.rafal.glucose.ui.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.databinding.MainFragmentBinding
import com.zubala.rafal.glucose.ui.signin.AccountData
import java.util.*


class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: MainFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false)

        val arguments = MainFragmentArgs.fromBundle(arguments!!)
        val viewModelFactory = MainViewModelFactory(openSheet(arguments.accountData))
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        binding.radioGroup.check(binding.onEmpty.id)

        viewModel.addMeasurementsEvent.observe(this, androidx.lifecycle.Observer {
            if (it) {
                val checked = binding.radioGroup.checkedRadioButtonId
                val type: Type? = when {
                    binding.onEmpty.id == checked -> {
                        Type.ON_EMPTY
                    }
                    binding.breakfast.id == checked -> {
                        Type.BREAKFAST
                    }
                    binding.dinner.id == checked -> {
                        Type.DINNER
                    }
                    binding.supper.id == checked -> {
                        Type.SUPPER
                    }
                    else -> null
                }
                type?.let {
                    viewModel.sendMeasurements(binding.glucoseMeasurement.text.toString(), type)
                }
                viewModel.doneSubmit()
            }
        })

        viewModel.snackbarEvent.observe(this, androidx.lifecycle.Observer {
            if (it.isNotEmpty()) {
                viewModel.snackbarDone()
                val snack = Snackbar.make(activity?.findViewById(android.R.id.content)!!, it, Snackbar.LENGTH_LONG)
                val view = snack.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                snack.show()
                binding.glucoseMeasurement.text = Editable.Factory.getInstance().newEditable("")
                hideKeyboard()
            }
        })

        binding.mainModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun openSheet(accountData: AccountData): Sheets {
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            Collections.singleton("https://www.googleapis.com/auth/spreadsheets")
        )
        credential.selectedAccount = accountData.account.account
        return Sheets.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        ).build()
    }
}
