package com.xiaoxun.xun.region.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatchRegionBean(
    val domains: List<Domain>,
    val model: List<WatchModel>,
    val region: List<WatchRegion>
) : Parcelable

@Parcelize
data class WatchModel(
    val name: String,
    val type: String
) : Parcelable

@Parcelize
data class WatchRegion(
    val country: String,
    val domainIndex: Int,
    val model: List<String>,
    val name: String
) : Parcelable