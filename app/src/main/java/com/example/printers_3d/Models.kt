package com.example.printers_3d

import android.media.ThumbnailUtils
import android.net.Uri
import android.widget.ImageView
import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Forum_List(
    var post_id: String? = null,
    var user_id: String? = null,
    var title: String? = null,
    var author: String? = null,
    var date: String? =null,
    var image: String?= null
)

data class Forum_List_Thread(
    var post_id: String? = null,
    var user_id: String? = null,
    var title: String? = null,
    var author: String? = null,
    var date: String? =null,
    var description: String?=null,
    var avatar: String?= null,
    var imageThumbnail: String?=null,
    var imageBigPicture: String?=null
)
data class Market_List_Thread(
    var post_id: String? = null,
    var user_id: String? = null,
    var title: String? = null,
    var author: String? = null,
    var date: String? =null,
    var description: String?=null,
    var avatar : String?= null,
    var imageThumbnail: String?=null,
    var imageBigPicture: String?=null,
    var prize: String?=null,
    var contact:String?=null
)
data class Market_Post(
    var user_id: String? = null,
    var name:String? = null,
    var title: String? = null,
    var description: String? =null,
    var date: String?= null,
    var prize: String?=null,
    var contact:String?=null,
    var avatar: String?= null
)
