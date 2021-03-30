package com.example.pi.mqtt_test;

import android.content.Context;
import java.util.ArrayList;

class ChartFishWaterSensorActivity extends ChartActivity {
    private static String[]
            TypeString = new String[] {"TEMPFISH", "TDS", "PH", "ORP", "DO"},
            TypeTitle = new String[] {
                    "溫度 Temp", "總溶解固體 TDS", "酸鹼值 PH", "氧化還原值 ORP", "溶氧量 DO"},
            DataSetType = new String[] {"temp", "do", "orp", "ph", "tds"},
            ListViewStr = new String[] {"", "", "", "", ""};
    private static int[] ImgBtnOn = new int[] {
            R.drawable.temp_on,
            R.drawable.tds_on,
            R.drawable.ph_on,
            R.drawable.orp_on,
            R.drawable.doo_on
    }, ImgBtnOff = new int[] {
            R.drawable.temp,
            R.drawable.tds,
            R.drawable.ph,
            R.drawable.orp,
            R.drawable.doo
    };

    //手動部份初始化
    ChartFishWaterSensorActivity(Context mContext, ArrayList<String> IPStr) {
        super(mContext, IPStr, "WaterSensor", ImgBtnOn, ImgBtnOff,
                R.layout.view_chart_fish_watersensor, "watersensor", TypeString,
                TypeTitle, DataSetType, ListViewStr);
    }

    //語音部份初始化
    ChartFishWaterSensorActivity(
            Context mContext, String Type, String[] States, ArrayList<String> IPStr) {
        super(mContext, IPStr, Type, "WaterSensor", States,
                ImgBtnOn, ImgBtnOff, R.layout.view_chart_fish_watersensor, "watersensor",
                TypeString, TypeTitle, DataSetType, ListViewStr);
    }
}
