package com.zubala.rafal.glucose.ui.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.account.GoogleAccountConfig
import com.zubala.rafal.glucose.databinding.MainFragmentBinding
import com.zubala.rafal.glucose.logic.getCurrentDateTime
import com.zubala.rafal.glucose.ui.signin.AccountData
import com.zubala.rafal.glucose.ui.signin.SigninFragment
import java.util.*

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: MainFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.main_fragment, container, false)

        val arguments = MainFragmentArgs.fromBundle(arguments!!)
        val viewModelFactory = MainViewModelFactory(openSheet(context!!, arguments.accountData))
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

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
                binding.submit.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                binding.glucoseMeasurement.text.clear()

                viewModel.doneSubmit()
                hideKeyboard()
            }
        })

        viewModel.snackbarEvent.observe(this, androidx.lifecycle.Observer {
            if (it != DataResult.EMPTY) {
                val value = getResultValue(it)
                if (value.isNotEmpty()) {
                    val snack = Snackbar.make(activity?.findViewById(android.R.id.content)!!,
                        value, Snackbar.LENGTH_LONG)
                    val view = snack.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    snack.show()
                }

                binding.progressBar.visibility = View.INVISIBLE
                binding.submit.visibility = View.VISIBLE

                viewModel.snackbarDone()

                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToDayResults(
                    getCurrentDateTime()))
            }
        })

        binding.mainModel = viewModel
        binding.lifecycleOwner = this

        handleGreetings(binding)

        return binding.root
    }

    private fun handleGreetings(binding: MainFragmentBinding) {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..8 -> {
                binding.greetings.text = getString(R.string.good_morning)
                binding.radioGroup.check(binding.onEmpty.id)
            }
            in 9..12 -> {
                binding.greetings.text = getString(R.string.good_morning)
                binding.radioGroup.check(binding.breakfast.id)
            }
            in 13..17 -> {
                binding.greetings.text = getString(R.string.good_afternoon)
                binding.radioGroup.check(binding.dinner.id)
            }
            in 18..23 -> {
                binding.greetings.text = getString(R.string.good_evening)
                binding.radioGroup.check(binding.supper.id)
            }
        }
    }

    private fun getResultValue(it: DataResult?): String {
        it?.let{
            return when(it) {
                DataResult.EMPTY -> ""
                DataResult.NEW_DATA -> ""
                DataResult.DATA_EXISTS -> getString(R.string.data_exists)
                DataResult.NO_ROW -> getString(R.string.no_row)
                DataResult.SHOW_RESULTS -> ""
            }
        }
        return ""
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    //enable options menu in this fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }
    //inflate the menu
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    //handle item clicks of menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //get item id to handle item clicks
        val id = item!!.itemId
        //handle item clicks
        if (id == R.id.action_settings){
            //do your action here, im just showing toast
            Toast.makeText(activity, "Settings", Toast.LENGTH_SHORT).show()
        }
        if (id == R.id.action_logout){
            val mGoogleSignInOptions: GoogleSignInOptions =  SigninFragment.buildGoogleSignInOptions()
            val mGoogleSignInClient: GoogleSignInClient = SigninFragment.buildGoogleSignInClient(context!!, mGoogleSignInOptions)
            mGoogleSignInClient.signOut().addOnCompleteListener {
                GoogleAccountConfig.logout()
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToSigninFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

fun openSheet(context: Context, accountData: AccountData): Sheets {
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

