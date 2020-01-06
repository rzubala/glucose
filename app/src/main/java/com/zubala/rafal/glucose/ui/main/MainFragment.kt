package com.zubala.rafal.glucose.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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

        viewModel.addMeasurementsEvent.observe(this, androidx.lifecycle.Observer {
            if (it) {
                viewModel.sendMeasurements(binding.glucoseMeasurement.text.toString())
                viewModel.doneSubmit()
            }
        })

        binding.mainModel = viewModel
        binding.lifecycleOwner = this


        return binding.root
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
