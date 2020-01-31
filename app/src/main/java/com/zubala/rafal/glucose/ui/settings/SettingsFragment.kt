package com.zubala.rafal.glucose.ui.settings

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.app.ComponentActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.account.GoogleAccountConfig
import com.zubala.rafal.glucose.databinding.SettingsFragmentBinding
import kotlinx.android.synthetic.main.day_result_fragment.*

const val PREFS_FILENAME = "com.zubala.rafal.glucose.prefs"
const val SPREADSHEET_ID_KEY = "spreadsheet_id_key"

class SettingsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: SettingsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)

        val prefs = this.activity!!.getSharedPreferences(PREFS_FILENAME, 0)

        val viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        viewModel.savedEvent.observe(this, Observer {
            if (it) {
                val editor = prefs!!.edit()
                editor.putString(SPREADSHEET_ID_KEY, binding.spreadsheetId.text.toString())
                editor.apply()
                this.findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToMainFragment(GoogleAccountConfig.account(null)!!))
                viewModel.onSaved()
            }
        })

        val spreadsheetId = getSpreadsheetUrl(this.activity!!)
        binding.spreadsheetId.setText(spreadsheetId)
        if (spreadsheetId.isNullOrEmpty()) {
            val value = getString(R.string.insert_spreadsheet_id)
            val snack = Snackbar.make(activity?.findViewById(android.R.id.content)!!, value, Snackbar.LENGTH_LONG)
            snack.show()
        }

        binding.settingsModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }
}

fun getSpreadsheetUrl(activity: ComponentActivity): String? {
    val prefs = activity.getSharedPreferences(PREFS_FILENAME, 0)
    return prefs.getString(SPREADSHEET_ID_KEY, "")
}

