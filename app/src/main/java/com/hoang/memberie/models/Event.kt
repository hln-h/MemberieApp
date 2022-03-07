package com.hoang.memberie.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    var id: String?,
    val title: String,
    val photosUrls: List<String>,
    val usersEmails: List<String>
) : Parcelable {
    constructor() : this(null, "", listOf(), listOf())
}