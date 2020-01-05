package com.zubala.rafal.glucose.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.zubala.rafal.glucose.R
import com.zubala.rafal.glucose.databinding.SigninFragmentBinding

class SigninFragment : Fragment() {

    val RC_SIGN_IN: Int = 1

    lateinit var mGoogleSignInClient: GoogleSignInClient

    lateinit var mGoogleSignInOptions: GoogleSignInOptions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: SigninFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.signin_fragment, container, false)

        configureGoogleSignIn()

        binding.signInButton.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        if (GoogleSignIn.getLastSignedInAccount(context) == null) {
            startActivityForResult(mGoogleSignInClient.signInIntent, RC_SIGN_IN)
        } else {
            // use the already signed in account
            val account = GoogleSignIn.getLastSignedInAccount(context)
            Log.i("SigninFragment", "already signed: ${account?.account?.name}")
            this.findNavController().navigate(SigninFragmentDirections.actionPassAccount(AccountData(account!!)))
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //val account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException::class.java)?.account
                val account = task.getResult(ApiException::class.java)
                Log.i("SigninFragment", "Signed: ${account?.account?.name}")
                this.findNavController().navigate(SigninFragmentDirections.actionPassAccount(AccountData(account!!)))
            } catch (e: ApiException) {
                Log.w("SigninFragment", "signInResult: failed code=" + e.statusCode + ", reason: " + GoogleSignInStatusCodes.getStatusCodeString(e.statusCode), e)
            }
        }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope("https://www.googleapis.com/auth/spreadsheets"))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(context!!, mGoogleSignInOptions)
    }
}