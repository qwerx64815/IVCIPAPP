package com.example.pi.mqtt_test;

import android.content.Context;
import java.util.ArrayList;

class DashboardHomeActivity extends DashboardActivity {
    private static String[]
            TypeString = new String[] {"TEMPHOME", "HUMIDITY", "HCHO", "PM1.0", "PM2.5", "PM10"},
            TypeTitle = new String[] {"溫度 Temp", "濕度 Humidity", "甲醛 HCHO", "超細懸浮微粒 PM1.0",
                    "細懸浮微粒 PM2.5", "懸浮微粒 PM10"},
            DataSetType = new String[] {"Temp", "Hum", "HCHO", "pm_aqi_10", "pm_aqi_25", "pm_aqi_100"},
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

    DashboardHomeActivity(Context mContext, ArrayList<String> IPStr) {
        super(mContext, IPStr, "PMS", ImgBtnOn, ImgBtnOff, R.layout.view_dashboard_home,
                TypeString, TypeTitle, DataSetType, ListViewStr);
    }

    DashboardHomeActivity(
            Context mContext, String Type, String[] States, ArrayList<String> IPStr) {
        super(mContext, IPStr, Type, "PMS", States, ImgBtnOn, ImgBtnOff,
                R.layout.view_dashboard_home, TypeString, TypeTitle, DataSetType, ListViewStr);
    }
}
