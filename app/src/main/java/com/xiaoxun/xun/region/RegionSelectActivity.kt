package com.xiaoxun.xun.region

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xiaoxun.xun.Constants
import com.xiaoxun.xun.ImibabyApp
import com.xiaoxun.xun.R
import com.xiaoxun.xun.activitys.BindNewActivity
import com.xiaoxun.xun.activitys.NewLoginActivity
import com.xiaoxun.xun.region.bean.*
import com.xiaoxun.xun.utils.LogUtil
import com.xiaoxun.xun.utils.RegionSelectUtil
import com.xiaoxun.xun.utils.StatusBarUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.Scanner


class RegionSelectActivity : AppCompatActivity() {

    //local json内容串
    private val LoCAL_WATCH_LIST =
        "{\"region\":[{\"name\":\"VI(S5,S6,S8,S88,Y2)\",\"model\":[\"SW708_A06\",\"SW709_A03\",\"SW708_A07\",\"SW709_A05\",\"SW707_A05\"],\"domainIndex\":0},{\"name\":\"RU\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":1},{\"name\":\"TH\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"HK\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"TW\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"BY\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"KZ\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"ID\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"SG\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"MY\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"PH\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"MM\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"VI\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"LA\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"KH\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"TR\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"AE\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"SA\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"QA\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"OM\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"IL\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"IQ\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"PK\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2},{\"name\":\"JO\",\"model\":[\"SW707_H01\",\"SW709_H01\"],\"domainIndex\":2}],\"domains\":[{\"app\":\"cmibro.xunkids.com:8555\",\"ota\":\"nupgrade.xunkids.com\",\"file\":\"nfdsfile.xunkids.com\",\"steps\":\"steps.xunkids.com\",\"dial\":\"dial-shop.xunkids.com\",\"dcenter\":\"dcenter.xunkids.com\",\"cou\":\"couserver.xunkids.com\",\"qr\":\"app-as.xunkids.com\"},{\"device\":\"ru-sw707h01.xunkids.com:7755\",\"app\":\"ru-cmibro.xunkids.com:8555\",\"ota\":\"ru-nupgrade.xunkids.com\",\"file\":\"ru-nfdsfile.xunkids.com\",\"steps\":\"ru-steps.xunkids.com\",\"dial\":\"ru-dial-shop.xunkids.com\",\"dcenter\":\"ru-dcenter.xunkids.com\",\"cou\":\"ru-couserver.xunkids.com\",\"qr\":\"ru-app.xunkids.com\"},{\"device\":\"sgp-sw707h01.xunkids.com:7755\",\"app\":\"sgp-cmibro.xunkids.com:8555\",\"ota\":\"sgp-nupgrade.xunkids.com\",\"file\":\"sgp-nfdsfile.xunkids.com\",\"steps\":\"sgp-steps.xunkids.com\",\"dial\":\"sgp-dial-shop.xunkids.com\",\"dcenter\":\"sgp-dcenter.xunkids.com\",\"cou\":\"sgp-couserver.xunkids.com\",\"qr\":\"sgp-app.xunkids.com\"}],\"model\":[{\"name\":\"S8\",\"type\":\"SW708_A06\"},{\"name\":\"S6\",\"type\":\"SW709_A03\"},{\"name\":\"Y2\",\"type\":\"SW708_A07\"},{\"name\":\"S5\",\"type\":\"SW709_A05\"},{\"name\":\"S88\",\"type\":\"SW707_A05\"},{\"name\":\"Mibro Watch Phone Z3\",\"type\":\"SW707_H01\"},{\"name\":\"Mibro Watch Phone P5\",\"type\":\"SW709_H01\"}]}";
    private val REGION_URL = "https://app-cdn.xunkids.com/2023/file/v2/region.json";
    var mApp: ImibabyApp? = null
    var adapter: CountryAdapter? = null
    var type = 0 //0 登录流程进入 1 登录/关于页面修改进入
    var mWatchBean: WatchRegionBean? = null
    private val watchRegion by lazy { this.intent.getIntExtra("watchRegion", 3) }

    //    private val mWatchBean by lazy { this.intent.getParcelableExtra("watchBean") as WatchBean? }
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_region_select)
        StatusBarUtil.changeStatusBarColor(
            this,
            resources.getColor(R.color.bg_color_orange)
        )
        mApp = application as ImibabyApp
        type = intent.getIntExtra("entry_type", 0)
        val locale: String = this.getResources().getConfiguration().locale.getDisplayCountry()
        LogUtil.e("mDevice Locale = ${locale}")
//        parseRegionJson(LoCAL_WATCH_LIST)
        GlobalScope.launch(Dispatchers.IO) {
            parseRegionJson(getHttpsContent(REGION_URL).toString())
            withContext(Dispatchers.Main) {
                getDeviceRegion()
                initView()
            }
        }
    }

    private fun parseRegionJson(json: String) {
        mWatchBean =
            Gson().fromJson<WatchRegionBean>(json, object : TypeToken<WatchRegionBean?>() {}.type)
    }

    private fun initView() {
        val iv_back = findViewById<ImageView>(R.id.iv_back)
        iv_back.setOnClickListener { finish() }
        val rv_list = findViewById<RecyclerView>(R.id.rv_list)

        val manager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        rv_list.layoutManager = manager
        adapter = CountryAdapter(this@RegionSelectActivity, mWatchBean, type, watchRegion)
        rv_list.adapter = adapter
        val tv_customized_region = findViewById<TextView>(R.id.tv_customized_region)
        tv_customized_region.setOnClickListener {
            val region = mWatchBean?.region?.find { it.name == "VI(S5,S6,S8,S88,Y2)" }!!
            regionSelect(region)
        }
    }

    class RegionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val region: TextView

        init {
            region = itemView.findViewById(R.id.tv_name)
        }
    }

    class CountryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_initial: TextView
        val county_tip: TextView
        val recycler_view: RecyclerView

        init {
            tv_initial = itemView.findViewById(R.id.tv_initial)
            county_tip = itemView.findViewById(R.id.county_tip)
            recycler_view = itemView.findViewById(R.id.recycler_view)
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getDeviceRegion() {
        val mApp = application as ImibabyApp
        var countryName = "";
        //获取LocationManager对象
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //获取Location对象
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        //获取经纬度
        val latitude = location!!.latitude
        val longitude = location!!.longitude
        //创建Geocoder对象
        val geocoder = Geocoder(this, Locale.getDefault())
        //根据经纬度获取地址列表
        var addresses: List<Address> = ArrayList()
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1) as List<Address>
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //判断地址列表是否为空
        if (addresses.isNotEmpty()) {
            //获取第一个地址对象
            val address: Address = addresses[0]
            //获取地区码
            countryName = address.countryName
            //打印地区码
            Log.d("CountryCode", countryName)
        }
        var tv_recommend_region = this.findViewById(R.id.tv_recommend_region) as TextView
        var recommended_region = this.findViewById(R.id.recommended_region) as ConstraintLayout
        if (countryName != "") {
            if (mWatchBean?.region?.find { it.country == countryName } != null) {
                recommended_region.visibility = View.VISIBLE
                val region = mWatchBean?.region?.find { it.country == countryName }!!
                tv_recommend_region.text = RegionSelectUtil.getNameFromSimple(this, region.name)
                tv_recommend_region.setOnClickListener {
                    regionSelect(region)
                }
                Log.d("Recommended Region", region.country)
            }
        }
        if (type != 0) {
            recommended_region.visibility = View.GONE
        }
    }

    fun regionSelect(region: WatchRegion) {
        val mApp = application as ImibabyApp
        val bean1 = mWatchBean?.domains!![region.domainIndex]
        val bean = RegionBean()
        bean.regionName = region.name
        bean.otaUrl = bean1.ota
        bean.dialUrl = bean1.dial
        bean.fileUrl = bean1.file
        bean.steps = bean1.steps
        bean.dcenter = bean1.dcenter
        bean.appHostUrl = bean1.app
        bean.cou = bean1.cou
        bean.qr = bean1.qr
        bean.regionSimpleName = RegionSelectUtil.getNameFromSimple(this, region.name)
        mApp.setValue("region", bean.regionSimpleName)
        mApp.setValue(Constants.KEY_NAME_WATCH_SELECTED, region.name)

        if (mApp.getIntValue(
                Constants.KEY_NAME_COUNTRY_SELECTED,
                0
            ) != region.domainIndex
        ) {
            if (watchRegion != 3) {
                val dlg = RegionConfirmDialog(this,
                    bean.regionSimpleName,
                    {},
                    {
                        //保存当前所选地区
                        mApp.setValue(
                            Constants.KEY_NAME_COUNTRY_SELECTED,
                            region.domainIndex
                        )
                        //设置服务器地址
                        XunKidsDomain.getInstance(this)
                            .setDomainWebSocketAndHttpBaseUrl(bean, true)
                        val it = Intent(
                            this,
                            NewLoginActivity::class.java
                        )
                        this.startActivity(it)
                        this.finish()
                    })
                dlg.show()
            } else {
                //保存当前所选地区
                mApp.setValue(
                    Constants.KEY_NAME_COUNTRY_SELECTED,
                    region.domainIndex
                )
                //设置服务器地址
                XunKidsDomain.getInstance(this)
                    .setDomainWebSocketAndHttpBaseUrl(bean, true)
                val it = Intent(
                    this,
                    NewLoginActivity::class.java
                )
                this.startActivity(it)
                this.finish()
            }
        } else {
            when (type) {
                1 -> {
                    ActivityUtils.finishToActivity(BindNewActivity::class.java, true)
                }

                2 -> {
                    this.finish()
                }

                else -> {
                    val it = Intent(
                        this,
                        NewLoginActivity::class.java
                    )
                    this.startActivity(it)
                    this.finish()
                }
            }
        }
    }

    class RegionAdapter(
        private val context: Activity,
        private val mWatchBean: WatchRegionBean?,
        private val type: Int,
        private val watchRegion: Int,
        private val datas: List<WatchRegion>
    ) :
        RecyclerView.Adapter<RegionHolder>() {
        private val list: List<WatchRegion> = datas
        private val mApp: ImibabyApp = context.application as ImibabyApp

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionHolder {
            return RegionHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_region_select_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: RegionHolder, position: Int) {
            val region: WatchRegion = list!![position]
            holder.region.text = RegionSelectUtil.getNameFromSimple(context, region.name)
            holder.itemView.setOnClickListener(View.OnClickListener {
                val bean1 = mWatchBean?.domains!![region.domainIndex]
                val bean = RegionBean()
                bean.regionName = region.name
                bean.otaUrl = bean1.ota
                bean.dialUrl = bean1.dial
                bean.fileUrl = bean1.file
                bean.steps = bean1.steps
                bean.dcenter = bean1.dcenter
                bean.appHostUrl = bean1.app
                bean.cou = bean1.cou
                bean.qr = bean1.qr
                bean.regionSimpleName = RegionSelectUtil.getNameFromSimple(context, region.name)
                mApp.setValue("region", bean.regionSimpleName)
                mApp.setValue(Constants.KEY_NAME_WATCH_SELECTED, region.name)

                if (mApp.getIntValue(
                        Constants.KEY_NAME_COUNTRY_SELECTED,
                        0
                    ) != region.domainIndex
                ) {
                    if (watchRegion != 3 || type != 0) {
                        val dlg = RegionConfirmDialog(context,
                            bean.regionSimpleName,
                            {},
                            {
                                //保存当前所选地区
                                mApp.setValue(
                                    Constants.KEY_NAME_COUNTRY_SELECTED,
                                    region.domainIndex
                                )
                                //设置服务器地址
                                XunKidsDomain.getInstance(context)
                                    .setDomainWebSocketAndHttpBaseUrl(bean, true)
                                val it = Intent(
                                    context,
                                    NewLoginActivity::class.java
                                )
                                context.startActivity(it)
                                context.finish()
                            })
                        dlg.show()
                    } else {
                        //保存当前所选地区
                        mApp.setValue(
                            Constants.KEY_NAME_COUNTRY_SELECTED,
                            region.domainIndex
                        )
                        //设置服务器地址
                        XunKidsDomain.getInstance(context)
                            .setDomainWebSocketAndHttpBaseUrl(bean, true)
                        val it = Intent(
                            context,
                            NewLoginActivity::class.java
                        )
                        context.startActivity(it)
                        context.finish()
                    }
                } else {
                    when (type) {
                        1 -> {
                            ActivityUtils.finishToActivity(BindNewActivity::class.java, true)
                        }

                        2 -> {
                            context.finish()
                        }

                        else -> {
                            val it = Intent(
                                context,
                                NewLoginActivity::class.java
                            )
                            context.startActivity(it)
                            context.finish()
                        }
                    }
                }
            })
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }
    }

    class CountryAdapter(
        private val context: Activity,
        private val mWatchBean: WatchRegionBean?,
        private val type: Int,
        private val watchRegion: Int
    ) :
        RecyclerView.Adapter<CountryHolder>() {
        private val list: List<WatchModelByInitial> = getWatchModelByInitial(mWatchBean?.region!!)
        private val mApp: ImibabyApp = context.application as ImibabyApp

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {
            return CountryHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.layout_country_select_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: CountryHolder, position: Int) {
            holder.tv_initial.text = list[position].initial
            if (position == 0 && type != 0) {
                holder.county_tip.visibility = View.VISIBLE
            } else {
                holder.county_tip.visibility = View.GONE
            }
            holder.recycler_view.layoutManager = LinearLayoutManager(context)
            var adapter: RegionAdapter? = null
            adapter = RegionAdapter(context, mWatchBean, type, watchRegion, list[position].region)
            holder.recycler_view.adapter = adapter
        }

        data class WatchModelByInitial(
            val initial: String,
            val region: List<WatchRegion>
        )

        fun getWatchModelByInitial(datas: List<WatchRegion>): List<WatchModelByInitial> {
            val list: MutableList<WatchModelByInitial> = ArrayList()
            // 排序
            var datas =
                datas.filter { it.country != null && it.country != "" }.sortedBy { it.country }
            val map: MutableMap<String?, MutableList<WatchRegion>> = HashMap()
            for (bean in datas) {
                var initial = bean.country.substring(0, 1)
                if (map.containsKey(initial)) {
                    val rList = map[initial]
                    rList?.add(bean)
                } else {
                    val rList: MutableList<WatchRegion> = ArrayList()
                    rList.add(bean)
                    map[initial] = rList
                }
            }
            LogUtil.v("----map----" + map.toString());
            for ((key, value) in map) {
                list.add(WatchModelByInitial(key!!, value))
            }
            list.sortBy { it.initial }
            LogUtil.v("----list----$list")
            return list
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }
    }


    /**
     * 获取URL中字符串
     *
     * @param url 网址字符串
     * @return String 网页内容
     */
    fun getHttpsContent(url: String?): String? {
        //利用java Scanner从URL读取Content
        var httpsContent: String? = null
        try {
            val scanner = Scanner(URL(url).openStream(), StandardCharsets.UTF_8.toString())
            scanner.useDelimiter("\\A")
            if (scanner.hasNext()) {
                httpsContent = scanner.next()
            }
            scanner.close()
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        if (httpsContent != null) {
            Log.i("getHttpsContent()", httpsContent!!)
        }
        return httpsContent
    }
}