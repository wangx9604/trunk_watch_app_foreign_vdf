package com.xiaoxun.xun.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.xiaoxun.xun.Constants;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.Sport2Utils;

import java.util.ArrayList;

/**
 * Created by zhangjun5 on 2019/7/27.
 */

public class SportRankFragment extends Fragment {

    private BarChart barChart;
    private String mSportRankData;
    private String mSportType;
    private String mSportRankPos;

    public static SportRankFragment newInstance(String sportData, String sportType,String sportRankPos){
        SportRankFragment sportFragment = new SportRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SPORT_RANK_LAMINATION, sportData);
        bundle.putString(Constants.SPORT_RANK_TYPE, sportType);
        bundle.putString(Constants.SPORT_RANK_RANGERANK, sportRankPos);
        sportFragment.setArguments(bundle);
        return sportFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mSportRankData = bundle.getString(Constants.SPORT_RANK_LAMINATION);
        mSportType = bundle.getString(Constants.SPORT_RANK_TYPE);
        mSportRankPos = bundle.getString(Constants.SPORT_RANK_RANGERANK);
    }

    public void initSportRankData(){
        String mHintTxt;
        if(mSportType.equals("1")){
            mHintTxt = getString(R.string.sport_rank_info_0);
        }else if(mSportType.equals("2")){
            mHintTxt = getString(R.string.ranks_country);
        }else{
            mHintTxt = getString(R.string.sport_rank_info_1);
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        String[] array = Sport2Utils.parseXValsPageByLamin(mSportRankData, Constants.SPORT_DAYS_DATA_KEY);
        barEntries.addAll(Sport2Utils.parseYValsPageByLamin(mSportRankData));
        Sport2Utils.setBarChartValue(mHintTxt,array,barEntries,barChart);
        int moveIndex = Sport2Utils.matchSportArrayAtPos(array, mSportRankPos);
        Sport2Utils.moveToEndAndHighlight(barChart,moveIndex);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewRoot= inflater.inflate(R.layout.fragment_sport_rank, container, false);
        return viewRoot;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        Sport2Utils.initChartParms(barChart);
        initSportRankData();
        Sport2Utils.setShowChartFix(barChart);
    }

    private void initView() {
        barChart = getView().findViewById(R.id.view_bar);
    }
}
