package com.example.printers_3d

import android.accounts.AuthenticatorDescription
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class User(
    var name: String? = null,
    var email: String? = null,
    var description: String? =null

)

