package com.example.pi.mqtt_test;

import android.content.Context;
import java.util.ArrayList;

class ChartHomeActivity extends ChartActivity {
    private static String[]
            TypeString = new String[] {"TEMPHOME", "HUMIDITY", "HCHO", "PM1.0", "PM2.5", "PM10"},
            TypeTitle = new String[] {
                    "溫度 Temp", "濕度 Humidity", "甲醛 HCHO", "超細懸浮微粒 PM1.0",
                    "細懸浮微粒 PM 2.5", "懸浮微粒 PM 10"},
            DataSetType = new String[] {"temp", "hum", "hcho", "pm_aqi_10", "pm_aqi_25", "pm_aqi_100"},
            ListViewStr = new String[] {"", "", "", "", "", ""};
    private static int[] ImgBtnOn = new int[] {
            R.drawable.temp_on,
            R.drawable.hum_on,
            R.drawable.hcho_on,
            R.drawable.pm_10_on,
            R.drawable.pm_25_on,
            R.drawable.pm_100_on
    }, ImgBtnOff = new int[] {
            R.drawable.temp,
            R.drawable.hum,
            R.drawable.hcho,
            R.drawable.pm_10,
            R.drawable.pm_25,
            R.drawable.pm_100
    };

    //手動部份初始化
    ChartHomeActivity(Context mContext, ArrayList<String> IPStr) {
        super(mContext, IPStr, "PMS", ImgBtnOn, ImgBtnOff,
                R.layout.view_chart_home, "pms", TypeString,
                TypeTitle, DataSetType, ListViewStr);
    }

    //語音部份初始化
    ChartHomeActivity(
            Context mContext, String Type, String[] States, ArrayList<String> IPStr) {
        super(mContext, IPStr, Type, "PMS", States,
                ImgBtnOn, ImgBtnOff, R.layout.view_chart_home, "pms",
                TypeString, TypeTitle, DataSetType, ListViewStr);
    }
}