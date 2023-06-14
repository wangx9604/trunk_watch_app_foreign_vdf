package com.xiaoxun.xun.region.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class WatchBean(
    val domains: List<Domain> = ArrayList(),
    val model: List<Model> = ArrayList()
) : Parcelable

@Parcelize
data class Domain(
    val app: String? = null,
    val cou: String? = null,
    val dcenter: String? = null,
    val device: String? = null,
    val dial: String? = null,
    val file: String? = null,
    val ota: String? = null,
    val steps: String? = null,
    val qr :String?=null
) : Parcelable

@Parcelize
data class Model(
    val name: String? = null,
    val region: List<Region> = ArrayList(),
    val type: String? = null
) : Parcelable

@Parcelize
data class Region(
    val domainIndex: Int? = null,
    val name: String? = null
) : Parcelable

class RegionBean {
    var regionName: String? = null
    var regionSimpleName: String? = null
    var appHostUrl: String? = null
    var dialUrl: String? = null
    var otaUrl: String? = null
    var fileUrl: String? = null
    var steps: String? = null
    var dcenter: String? = null
    var cou: String? = null
    var qr: String? = null
}

//{
//    "device": "ru-sw707h01.xunkids.com:7755",
//    "app": "ru-cmibro.xunkids.com:8555",
//    "ota": "ru-nupgrade.xunkids.com",
//    "file": "ru-nfdsfile.xunkids.com",
//    "steps": "ru-steps.xunkids.com",
//    "dial": "ru-dial-shop.xunkids.com",
//    "dcenter": "ru-dcenter.xunkids.com",
//    "cou": "ru-couserver.xunkids.com"
//}
