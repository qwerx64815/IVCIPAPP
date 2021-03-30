package com.example.pi.mqtt_test;

import android.content.Context;
import java.util.ArrayList;

class ChartFishActuatorActivity extends ChartActivity {
    private static String[]
            TypeString = new String[] {"D0", "D1", "D2", "D3"},
            TypeTitle = new String[] {
                    "LED燈 D0", "氣泡石 D1", "加熱棒 D2", "過濾器 D3"},
            DataSetType = new String[] {"d0", "d1", "d2", "d3"},
            ListViewStr = new String[] {"", "", "", ""};
    private static int[] ImgBtnOn = new int[] {
            R.drawable.d0_on,
            R.drawable.d1_on,
            R.drawable.d2_on,
            R.drawable.d3_on
    }, ImgBtnOff = new int[] {
            R.drawable.d0,
            R.drawable.d1,
            R.drawable.d2,
            R.drawable.d3
    };

    //手動部份初始化
    ChartFishActuatorActivity(Context mContext, ArrayList<String> IPStr) {
        super(mContext, IPStr, "Actuator", ImgBtnOn, ImgBtnOff,
                R.layout.view_chart_fish_actuator, "Actuator", TypeString,
                TypeTitle, DataSetType, ListViewStr);
    }

    //語音部份初始化
    ChartFishActuatorActivity(
            Context mContext, String Type, String[] States, ArrayList<String> IPStr) {
        super(mContext, IPStr, Type, "Actuator", States,
                ImgBtnOn, ImgBtnOff, R.layout.view_chart_fish_actuator, "Actuator",
                TypeString, TypeTitle, DataSetType, ListViewStr);
    }
}
