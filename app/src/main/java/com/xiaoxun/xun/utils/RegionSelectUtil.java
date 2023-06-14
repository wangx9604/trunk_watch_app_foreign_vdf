package com.xiaoxun.xun.utils;

import android.content.Context;

import com.xiaoxun.xun.R;

public class RegionSelectUtil {

    public static String getNameFromSimple(Context context, String simple){
        String name = "";
        switch (simple){
            //IL
            //TR
            //SA
            //IR
            //CN
            //HK
            //TW
            //VN
            //MY
            //TH
            //ID
            //SG
            //PH
            //MM
            //LA
            //KH
            //TN"
            case "UA":
                name = context.getResources().getString(R.string.UA);
                break;
            case "SE":
                name = context.getResources().getString(R.string.SE);
                break;
            case "DK":
                name = context.getResources().getString(R.string.DK);
                break;
            case "NO":
                name = context.getResources().getString(R.string.NO);
                break;
            case "GR":
                name = context.getResources().getString(R.string.GR);
                break;
            case "HU":
                name = context.getResources().getString(R.string.HU);
                break;
            case "DE":
                name = context.getResources().getString(R.string.DE);
                break;
            case "GB":
                name = context.getResources().getString(R.string.GB);
                break;
            case "IT":
                name = context.getResources().getString(R.string.IT);
                break;
            case "FR":
                name = context.getResources().getString(R.string.FR);
                break;
            case "ES":
                name = context.getResources().getString(R.string.ES);
                break;
            case "PT":
                name = context.getResources().getString(R.string.PT);
                break;
            case "RS":
                name = context.getResources().getString(R.string.RS);
                break;
            case "RO":
                name = context.getResources().getString(R.string.RO);
                break;
            case "LT":
                name = context.getResources().getString(R.string.LT);
                break;
            case "LV":
                name = context.getResources().getString(R.string.LV);
                break;
            case "EE":
                name = context.getResources().getString(R.string.EE);
                break;
            case "PL":
                name = context.getResources().getString(R.string.PL);
                break;
            case "NL":
                name = context.getResources().getString(R.string.NL);
                break;
            case "BA":
                name = context.getResources().getString(R.string.BA);
                break;
            case "RU":
                name = context.getResources().getString(R.string.RU);
                break;
            case "IL":
                name = context.getResources().getString(R.string.IL);
                break;
            case "BY":
                name = context.getResources().getString(R.string.PT);
                break;
            case "TR":
                name = context.getResources().getString(R.string.TR);
                break;
            case "SA":
                name = context.getResources().getString(R.string.SA);
                break;
            case "IR":
                name = context.getResources().getString(R.string.IR);
                break;
            case "CN":
                name = context.getResources().getString(R.string.CN);
                break;
            case "HK":
                name = context.getResources().getString(R.string.HK);
                break;
            case "TW":
                name = context.getResources().getString(R.string.TW);
                break;
            case "VN":
                name = context.getResources().getString(R.string.VN);
                break;
            case "MY":
                name = context.getResources().getString(R.string.MY);
                break;
            case "TH":
                name = context.getResources().getString(R.string.TH);
                break;
            case "ID":
                name = context.getResources().getString(R.string.ID);
                break;
            case "SG":
                name = context.getResources().getString(R.string.SG);
                break;
            case "PH":
                name = context.getResources().getString(R.string.PH);
                break;
            case "MM":
                name = context.getResources().getString(R.string.MM);
                break;
            case "LA":
                name = context.getResources().getString(R.string.LA);
                break;
            case "KH":
                name = context.getResources().getString(R.string.KH);
                break;
            case "TN":
                name = context.getResources().getString(R.string.TN);
                break;
            case "VI(S5,S6,S8,S88,Y2)":
                name = "S5,S6,S8,S88,Y2";
                break;
            default:
                name = simple;
                break;
        }
        return name;
    }
}
