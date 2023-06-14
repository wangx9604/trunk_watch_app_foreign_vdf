
package com.xiaoxun.xun.views;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;
import com.xiaoxun.xun.R;
import com.xiaoxun.xun.utils.ToolUtils;

/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private String isFormatType = "0";//计步：0 流量统计：1
    private Context context;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        this.context = context;
        tvContent = findViewById(R.id.tvContent);
    }

    public void setisFormatType(String  MarkType){
        this.isFormatType = MarkType;
    }

    @Override
    public void refreshContent(Entry e, int i) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            if(isFormatType.equals("0")){
                tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, false));
            }else {
                String[] format = ToolUtils.formatFlowStatiticsDataInfo(context, ce.getHigh() * 1024);
                tvContent.setText(format[0]+format[1]);
            }
        } else {
            if(isFormatType.equals("0")){
                tvContent.setText("" + Utils.formatNumber(e.getVal(), 0, false));
            }else {
                String[] format = ToolUtils.formatFlowStatiticsDataInfo(context, e.getVal() * 1024);
                tvContent.setText(format[0]+format[1]);
            }
        }
    }

    @Override
    public int getXOffset() {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        return -getHeight();
    }


}
