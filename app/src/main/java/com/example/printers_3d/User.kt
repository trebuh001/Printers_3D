package com.example.printers_3d

import android.accounts.AuthenticatorDescription
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var name: String? = "",
    var email: String? = "",
    var description: String? =""

)