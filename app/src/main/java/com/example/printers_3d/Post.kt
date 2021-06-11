package com.example.printers_3d

import android.widget.ImageView
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Post(
    var user_id: String? = null,
    var name:String? = null,
    var title: String? = null,
    var description: String? =null,
    var date: String?= null,
    var avatar: String?= null,
)