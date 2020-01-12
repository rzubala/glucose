package com.zubala.rafal.glucose.ui.signin

import android.os.Parcelable
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.sheets.v4.Sheets
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccountData(val account: GoogleSignInAccount) : Parcelable
