package com.xiaoxun.xun.region

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaoxun.xun.Constants
import com.xiaoxun.xun.ImibabyApp
import com.xiaoxun.xun.R
import com.xiaoxun.xun.activitys.NormalActivity
import com.xiaoxun.xun.region.bean.WatchBean
import com.xiaoxun.xun.utils.StatusBarUtil

class RegionWatchActivity : NormalActivity() {

    private val mApp: ImibabyApp by lazy { this.application as ImibabyApp }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_watch)
        StatusBarUtil.changeStatusBarColor(
            this,
            resources.getColor(R.color.bg_color_orange)
        )
        initView()
    }

    private fun initView() {
        val iv_back = findViewById<ImageView>(R.id.iv_back)
        iv_back.setOnClickListener { finish() }
        val cl_select_watch = findViewById<ConstraintLayout>(R.id.cl_select_watch)
        val tv_edit_watch = findViewById<TextView>(R.id.tv_edit_watch)
        val tv_name = findViewById<TextView>(R.id.tv_name)
        val watchSelected = mApp.getStringValue(Constants.KEY_NAME_WATCH_SELECTED, "")
        tv_name.text = watchSelected
        tv_edit_watch.setOnClickListener {
            val it = Intent(
                this,
                WatchSelectActivity::class.java
            )
            it.putExtra("entry_type", 1)
            startActivity(it)
        }
    }

    override fun onResume() {
        super.onResume()
        val tv_name = findViewById<TextView>(R.id.tv_name)
        val watchSelected = mApp.getStringValue(Constants.KEY_NAME_WATCH_SELECTED, "")
        tv_name.text = watchSelected
    }
}