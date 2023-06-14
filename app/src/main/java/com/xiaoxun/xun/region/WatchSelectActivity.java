package com.xiaoxun.xun.region;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.ImibabyApp;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.activitys.NewLoginActivity;
import com.xiaoxun.xun.region.bean.Domain;
import com.xiaoxun.xun.region.bean.Model;
import com.xiaoxun.xun.region.bean.RegionBean;
import com.xiaoxun.xun.region.bean.WatchBean;
import com.xiaoxun.xun.utils.RegionSelectUtil;
import com.xiaoxun.xun.utils.StatusBarUtil;
import com.xiaoxun.xun.utils.ToastUtil;

import java.util.List;

public class WatchSelectActivity extends AppCompatActivity {

    //手表—国家json地址
    private final static String JSON_URL = "https://app-cdn.xunkids.com/2023/region.json";

    //local json内容串
    private final static String LoCAL_WATCH_LIST = "{\n" +
            "  \"model\": [\n" +
            "    {\n" +
            "      \"name\": \"KidCare S8\",\n" +
            "      \"type\": \"SW708_A06\",\n" +
            "      \"region\": [\n" +
            "        {\n" +
            "          \"name\": \"CHN\",\n" +
            "          \"domainIndex\": 0\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"KidCare S6\",\n" +
            "      \"type\": \"SW709_A03\",\n" +
            "      \"region\": [\n" +
            "        {\n" +
            "          \"name\": \"CHN\",\n" +
            "          \"domainIndex\": 0\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Mibro Kids Smart Watch Y2\",\n" +
            "      \"type\": \"SW708_A07\",\n" +
            "      \"region\": [\n" +
            "        {\n" +
            "          \"name\": \"CHN\",\n" +
            "          \"domainIndex\": 0\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Mibro Kids Smart Watch S5\",\n" +
            "      \"type\": \"SW709_A05\",\n" +
            "      \"region\": [\n" +
            "        {\n" +
            "          \"name\": \"CHN\",\n" +
            "          \"domainIndex\": 0\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Kidcare S88\",\n" +
            "      \"type\": \"SW707_A05\",\n" +
            "      \"region\": [\n" +
            "        {\n" +
            "          \"name\": \"CHN\",\n" +
            "          \"domainIndex\": 0\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Mibro Watch Phone Z3\",\n" +
            "      \"type\": \"SW707_H01\",\n" +
            "      \"region\": [\n" +
            "        {\n" +
            "          \"name\": \"RU\",\n" +
            "          \"domainIndex\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"TH\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"HK\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"TW\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"BY\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"KZ\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"ID\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"SG\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"MY\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"PH\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"MM\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"VI\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"LA\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"KH\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"TR\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"AE\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"SA\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"QA\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"OM\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"IL\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"IQ\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"PK\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"JO\",\n" +
            "          \"domainIndex\": 2\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Mibro Watch Phone P5\",\n" +
            "      \"type\": \"SW709_H01\",\n" +
            "      \"region\": [\n" +
            "        {\n" +
            "          \"name\": \"RU\",\n" +
            "          \"domainIndex\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"TH\",\n" +
            "           \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"HK\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"TW\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"BY\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"KZ\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"ID\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"SG\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"MY\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"PH\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"MM\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"VI\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"LA\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"KH\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"TR\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"AE\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"SA\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"QA\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"OM\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"IL\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"IQ\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"PK\",\n" +
            "          \"domainIndex\": 2\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"JO\",\n" +
            "          \"domainIndex\": 2\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"domains\": [\n" +
            "    {\n" +
            "      \"app\": \"cmibro.xunkids.com:8555\",\n" +
            "      \"ota\": \"nupgrade.xunkids.com\",\n" +
            "      \"file\": \"nfdsfile.xunkids.com\",\n" +
            "      \"steps\": \"steps.xunkids.com\",\n" +
            "      \"dial\": \"dial-shop.xunkids.com\",\n" +
            "      \"dcenter\": \"dcenter.xunkids.com\",\n" +
            "      \"cou\": \"couserver.xunkids.com\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"device\": \"ru-sw707h01.xunkids.com:7755\",\n" +
            "      \"app\": \"ru-cmibro.xunkids.com:8555\",\n" +
            "      \"ota\": \"ru-nupgrade.xunkids.com\",\n" +
            "      \"file\": \"ru-nfdsfile.xunkids.com\",\n" +
            "      \"steps\": \"ru-steps.xunkids.com\",\n" +
            "      \"dial\": \"ru-dial-shop.xunkids.com\",\n" +
            "      \"dcenter\": \"ru-dcenter.xunkids.com\",\n" +
            "      \"cou\": \"ru-couserver.xunkids.com\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"device\": \"sgp-sw707h01.xunkids.com:7755\",\n" +
            "      \"app\": \"sgp-cmibro.xunkids.com:8555\",\n" +
            "      \"ota\": \"sgp-nupgrade.xunkids.com\",\n" +
            "      \"file\": \"sgp-nfdsfile.xunkids.com\",\n" +
            "      \"steps\": \"sgp-steps.xunkids.com\",\n" +
            "      \"dial\": \"sgp-dial-shop.xunkids.com\",\n" +
            "      \"dcenter\": \"sgp-dcenter.xunkids.com\",\n" +
            "      \"cou\": \"sgp-couserver.xunkids.com\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    ImibabyApp mApp;

    WatchBean watchBean;
    WatchSelectAdapter adapter;

    int type = 0;//0 登录流程进入 1 登录/关于页面修改进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_select);
        StatusBarUtil.changeStatusBarColor(WatchSelectActivity.this, getResources().getColor(R.color.bg_color_orange));
        mApp = (ImibabyApp) getApplication();
        type = getIntent().getIntExtra("entry_type", 0);
        parseRegionJson(LoCAL_WATCH_LIST);
        initView();
    }

    private void initView() {
        ImageView iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        RecyclerView rv_list = findViewById(R.id.rv_list);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rv_list.setLayoutManager(manager);
        adapter = new WatchSelectAdapter(WatchSelectActivity.this, watchBean);
        rv_list.setAdapter(adapter);
    }

    private void parseRegionJson(String json) {
        watchBean = new Gson().fromJson(json, new TypeToken<WatchBean>() {
        }.getType());
    }

    static class WatchHolder extends RecyclerView.ViewHolder {

        private TextView region;

        public WatchHolder(@NonNull View itemView) {
            super(itemView);
            region = itemView.findViewById(R.id.tv_name);
        }
    }

    class WatchSelectAdapter extends RecyclerView.Adapter<WatchHolder> {
        private Activity context;
        private List<Model> models;

        private ImibabyApp mApp;

        private WatchBean mWatchBean;

        public WatchSelectAdapter(Activity ctxt, WatchBean watchBean) {
            context = ctxt;
            mWatchBean = watchBean;
            models = mWatchBean.getModel();
            mApp = (ImibabyApp) ctxt.getApplication();
        }

        @NonNull
        @Override
        public WatchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WatchHolder(LayoutInflater.from(context).inflate(R.layout.layout_region_select_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WatchHolder holder, int position) {
            Model model = models.get(position);
            holder.region.setText(model.getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.getRegion().size() == 1 && model.getRegion().get(0).getDomainIndex() == 0) {
                        Domain chnDomain = mWatchBean.getDomains().get(0);
                        RegionBean bean = new RegionBean();
                        bean.setRegionName(model.getRegion().get(0).getName());
                        bean.setOtaUrl(chnDomain.getOta());
                        bean.setDialUrl(chnDomain.getDial());
                        bean.setFileUrl(chnDomain.getFile());
                        bean.setSteps(chnDomain.getSteps());
                        bean.setAppHostUrl(chnDomain.getApp());
                        bean.setRegionSimpleName(RegionSelectUtil.getNameFromSimple(context, model.getRegion().get(0).getName()));
                        mApp.setValue(Constants.KEY_NAME_WATCH_SELECTED, model.getName());
                        mApp.setValue("region", bean.getRegionSimpleName());

                        if (mApp.getIntValue(Constants.KEY_NAME_COUNTRY_SELECTED, 1) != model.getRegion().get(0).getDomainIndex()) {
                            //保存当前所选地区
                            mApp.setValue(Constants.KEY_NAME_COUNTRY_SELECTED, model.getRegion().get(0).getDomainIndex());
                            //设置服务器地址
                            XunKidsDomain.getInstance(context)
                                    .setDomainWebSocketAndHttpBaseUrl(bean, true);
                            Intent it = new Intent(context, NewLoginActivity.class);
                            context.startActivity(it);
                            context.finish();
                        } else {
                            Intent it = new Intent(context, NewLoginActivity.class);
                            context.startActivity(it);
                            context.finish();
                        }
                    } else {
                        Intent it = new Intent(context, RegionSelectActivity.class);
                        it.putExtra("model", model);
                        it.putExtra("entry_type", type);
                        it.putExtra("watchBean", mWatchBean);
                        context.startActivity(it);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return (models != null ? models.size() : 0);
        }
    }
}