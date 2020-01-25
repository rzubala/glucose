package com.zubala.rafal.glucose.account

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.zubala.rafal.glucose.ui.signin.AccountData

object GoogleAccountConfig {
    private var userAccount: AccountData? = null

    fun account(account: GoogleSignInAccount?): AccountData? {
        account?.let {
            userAccount = AccountData(account)
        }
        return userAccount
    }

    fun logout() {
        userAccount = null
    }
}