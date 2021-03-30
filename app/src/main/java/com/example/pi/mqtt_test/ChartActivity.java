package com.example.pi.mqtt_test;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChartActivity extends Fragment {
    private Context mContext;
    private View view;
    private Message message;
    private ArrayList<String> myDataSet;
    private String Type, Topic, year_str, month_str, day_str, IPString, LinkType;
    private String[] States = new String[]{}, TypeString, TypeTitle, DataSetType, ListViewStr;
    private int[] ListImgBtnOn, ListImgBtnOff;
    private int FillLayout, previous_position = 0, ListViewVisibleCount = 0, SelectedPosition = 0,
            year, month, dayOfMonth;
    private float sum, sum2, sum3, SD;
    private LineChart LineChartDay, LineChartMonth, LineChartYear, chart;

    //手動部份初始化
    ChartActivity(Context mContext, ArrayList<String> IPStr, String DefaultTopic,
                  int[] ListImgBtnON, int[] ListImgBtnOFF, int FillLayout, String LinkType,
                  String[] TypeString, String[] TypeTitle, String[] DataSetType,
                  String[] ListViewStr) {
        this.mContext = mContext;
        Type = TypeString[0];
        Topic = DefaultTopic; //傳入預設Topic
        IPString = IPStr.get(0) + ":" + IPStr.get(1); //IP Connect String
        Log.d("IPString", IPString);
        this.ListImgBtnOn = ListImgBtnON; //ListView BtnImg
        this.ListImgBtnOff = ListImgBtnOFF;
        this.FillLayout = FillLayout;
        this.LinkType = LinkType; //watersensor
        this.TypeString = TypeString; //Ex. ["TEMPFISH", "TDS", "PH", "ORP", "DO"]
        this.TypeTitle = TypeTitle; //Ex. ["溫度 Temp", "總溶解固體 TDS", "酸鹼值 PH", "氧化還原值 ORP", "溶氧量 DO"]
        this.DataSetType = DataSetType; //Ex. {"temp", "do", "orp", "ph", "tds"}
        this.ListViewStr = ListViewStr; //Ex. {"", "", "", "", ""}
    }

    //語音部份初始化
    ChartActivity(
            Context mContext, ArrayList<String> IPStr, String DefaultType, String DefaultTopic,
            String[] States, int[] ListImgBtnON, int[] ListImgBtnOFF, int FillLayout,
            String LinkType, String[] TypeString, String[] TypeTitle, String[] DataSetType,
            String[] ListViewStr) {
        this.mContext = mContext;
        Log.d("speech", "DefaultType: " + DefaultType + ", DefaultTopic: " + DefaultTopic);
        if(DefaultType.equals("temp")) {
            switch (DefaultTopic) {
                case "WaterSensor":
                    Type = "TEMPFISH";
                    break;
                case "PMS":
                    Type = "TEMPHOME";
                    break;
            }
        }
        else Type = DefaultType.toUpperCase();
        Topic = DefaultTopic;
        this.States = States;
        IPString = IPStr.get(0) + ":" + IPStr.get(1);
        Log.d("speech", "Type: " + Type);
        Log.d("speech", "States: " + States[0] + " " + States[1] + " " + States[2]);
        Log.d("speech", "IPString: " + IPString);
        this.ListImgBtnOn = ListImgBtnON; //ListView BtnImg
        this.ListImgBtnOff = ListImgBtnOFF;
        this.FillLayout = FillLayout;
        this.LinkType = LinkType; //watersensor
        this.TypeString = TypeString; //Ex. {"TEMP", "TDS", "PH", "ORP", "DO"}
        this.TypeTitle = TypeTitle; //Ex. {"溫度 Temp", "總溶解固體 TDS", "酸鹼值 PH", "氧化還原值 ORP", "溶氧量 DO"}
        this.DataSetType = DataSetType; //Ex. {"temp", "do", "orp", "ph", "tds"}
        this.ListViewStr = ListViewStr; //Ex. {"", "", "", "", ""}
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(
                FillLayout,
                container,
                false
        );
        switch (FillLayout) {
            case R.layout.view_chart_fish_watersensor:
            case R.layout.view_chart_fish_actuator:
                view.setBackgroundResource(R.mipmap.diversbg);
                break;
            case R.layout.view_chart_home:
                view.setBackgroundResource(R.mipmap.brickbg);
                break;
        }
        ListViewAddData();
        ChartLoad();

        return view;
    }

    // ListView 加入資料
    private void ListViewAddData() {
        ListView Left_Type_List = view.findViewById(R.id.Left_Type_List);
        setListViewHeight(Left_Type_List);

        // ListView 填充資料
        Left_Type_List.setAdapter(new ArrayAdapter<String>(
                mContext, android.R.layout.simple_expandable_list_item_1, ListViewStr) {
            @Override //預設首個選項背景色
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                if (position == SelectedPosition)
                    row.setBackgroundResource(ListImgBtnOn[position]);
                else
                    row.setBackgroundResource(ListImgBtnOff[position]);
                return row;
            }
        });

        TypeChangeView();

        Left_Type_List.setOnItemClickListener((parent, view, position, id) -> {
            // 讓 getView 能夠取得選擇位置
            SelectedPosition = position;

            // 先清空所有項目的背景
            int temp = 0, FirstPosition = parent.getFirstVisiblePosition();
            ListViewVisibleCount = parent.getLastVisiblePosition() - parent.getFirstVisiblePosition();
            while (temp <= ListViewVisibleCount) {
                parent.getChildAt(temp).setBackgroundResource(ListImgBtnOff[FirstPosition]);
                temp++;
                FirstPosition++;
            }

            // 將選擇項目設背景顏色
            int n = parent.getFirstVisiblePosition(); //ListView 可視範圍第一項
            int ChildIndex = position - n;
            parent.getChildAt(ChildIndex).setBackgroundResource(ListImgBtnOn[position]);
            TypeChangeView(position);
        });
    }

    //根據 Type 切換 View
    private void TypeChangeView() {
        // 切換 Controller 種類
        TextView Title_Main = view.findViewById(R.id.Title_Main);
        for(int i = 0; i < TypeString.length; i++) {
            if(TypeString[i].equals(Type)) {
                SelectedPosition = i;
                Title_Main.setText(TypeTitle[i]);
            }
        }
    }

    //根據 Position 切換 View
    private void TypeChangeView(int position) {
        TextView Title_Main = view.findViewById(R.id.Title_Main);
        Type = TypeString[position];
        Title_Main.setText(TypeTitle[position]);
        ChartLoad();
        if(position != previous_position) { previous_position = position; }
    }

    // 重新計算 ListView 高度
    private void setListViewHeight(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) return;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1)) + 15;
        listView.setLayoutParams(params);
    }

    // 報表讀取
    private void ChartLoad() {
        ChartFindView();
        Chart_Day();
        Chart_Month();
        Chart_Year();

        Date date = new Date();
        String link = "";
        if(States.length > 0) {
            DateFormat dateFormat_month = new SimpleDateFormat("M"),
                    dateFormat_day = new SimpleDateFormat("d");
            month_str = dateFormat_month.format(date);
            day_str = dateFormat_day.format(date);
            if(States[2].equals("default_day")) {
                if(States[1].equals("default_month")) //2019 default_month default_day
                    link = "http://" + IPString + "/" + LinkType + "?id="
                            + Topic + "&year=2019&month=" + month_str
                            + "&day=" + day_str + "&button=提交";
                else //2019 MM default_day
                    link = "http://" + IPString + "/" + LinkType + "?id="
                            + Topic + "&year=2019&month=" + States[1]
                            + "&day=" + day_str + "&button=提交";
                LoadData(link, "day");
            } else if(States[2].equals("0") && !States[1].equals("0")) {
                if(States[1].equals("default_month")) //2019 default_month 0
                    link = "http://" + IPString + "/" + LinkType + "?id="
                            + Topic + "&year=2019&month=" + month_str
                            + "&day=0&button=提交";
                else //2019 mm 0
                    link = "http://" + IPString + "/" + LinkType + "?id="
                            + Topic + "&year=2019&month=" + States[1]
                            + "&day=0&button=提交";
                new Thread(() -> {
                    message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);
                }).start();
                LoadData(link, "month");
            } else { //2019 0 0
                if(States[1].equals("0") && States[2].equals("0")) {
                    link = "http://" + IPString + "/" + LinkType + "?id="
                            + Topic + "&year=2019&month=0&day=0&button=提交";
                    new Thread(() -> {
                        message = new Message();
                        message.what = 4;
                        handler.sendMessage(message);
                    }).start();
                    LoadData(link, "year");
                }
                else {
                    if(States[1].equals("default_month")) //2019 default_month dd
                        link = "http://" + IPString + "/" + LinkType + "?id="
                                + Topic + "&year=2019&month=" + month_str
                                + "&day=" + States[2] + "&button=提交";
                    else //2019 MM dd
                        link = "http://" + IPString + "/" + LinkType + "?id="
                                + Topic + "&year=2019&month=" + States[1]
                                + "&day=" + States[2] + "&button=提交";
                    LoadData(link, "day");
                }
            }
        }
        Log.d("speech", link);
    }

    private void ChartFindView() {
        LineChartDay = view.findViewById(R.id.LineChartDay);
        LineChartDay.setDragEnabled(true);
        LineChartDay.setScaleEnabled(false);

        LineChartMonth = view.findViewById(R.id.LineChartMonth);
        LineChartMonth.setDragEnabled(true);
        LineChartMonth.setScaleEnabled(false);

        LineChartYear = view.findViewById(R.id.LineChartYear);
        LineChartYear.setDragEnabled(true);
        LineChartYear.setScaleEnabled(false);
    }

    // 日報表
    private void Chart_Day() {
        view.findViewById(R.id.SelectDateBtn).setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    mContext,
                    (datePicker, year, month, day) -> {
                        TextView SelectedDateTextView = view.findViewById(R.id.SelectedDateTextView);
                        SelectedDateTextView.setText(year + "/" + (month + 1) + "/" + day);
                        dayOfMonth = day;
                        month_str = Integer.toString(month + 1);
                        year_str = Integer.toString(year);
                        day_str = Integer.toString(day);
                    }, year, month, dayOfMonth);
            datePickerDialog.show();
        });

        view.findViewById(R.id.DayLoadBtn).setOnClickListener(v -> {
            String link = "http://" + IPString + "/" + LinkType + "?id="
                    + Topic + "&year=" + year_str + "&month="
                    + month_str + "&day=" + day_str + "&button=提交";
            LoadData(link, "day");
        });
    }

    // 月報表
    private void Chart_Month() {
        view.findViewById(R.id.MonthLoadBtn).setOnClickListener(v -> {
            Spinner month_select_year = view.findViewById(R.id.MonthSelectYear),
                    month_select_month = view.findViewById(R.id.MonthSelectMonth);
            year_str = ((TextView) month_select_year.getSelectedView())
                    .getText().toString();
            month_str = ((TextView) month_select_month.getSelectedView())
                    .getText().toString();
            String link = "http://" + IPString + "/" + LinkType + "?id="
                    + Topic + "&year=" + year_str + "&month=" + month_str
                    + "&day=0&button=提交";
            LoadData(link, "month");
        });
    }

    // 年報表
    private void Chart_Year() {
        view.findViewById(R.id.YearLoadBtn).setOnClickListener(v -> {
            Spinner year_select_year = view.findViewById(R.id.YearSelectYear);
            year_str = ((TextView) year_select_year.getSelectedView())
                    .getText().toString();
            String link = "http://" + IPString + "/" + LinkType + "?id="
                    + Topic + "&year=" + year_str + "&month=0&day=0&button=提交";
            LoadData(link, "year");
        });
    }

    // UI 多線程(TextView)
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Date date = new Date();
            DateFormat dateFormat_date = new SimpleDateFormat("- yyyy 年 MM 月 dd 日"),
                    dateFormat_time = new SimpleDateFormat("- HH 時 mm 分 ss 秒"),
                    dateFormat_month = new SimpleDateFormat("M");
            switch (msg.what) {
                //報表查詢時間
                case 0:
                    TextView ChartShowType_day = view.findViewById(R.id.ChartShowType_day);
                    ChartShowType_day.setText(Type);

                    TextView SubscribeDate_day = view.findViewById(R.id.SubscribeDate_day),
                            SubscribeTime_day = view.findViewById(R.id.SubscribeTime_day);
                    SubscribeDate_day.setText(dateFormat_date.format(date));
                    SubscribeTime_day.setText(dateFormat_time.format(date));
                    break;
                case 1:
                    TextView ChartShowType_month = view.findViewById(R.id.ChartShowType_month);
                    ChartShowType_month.setText(Type);

                    TextView SubscribeDate_month = view.findViewById(R.id.SubscribeDate_month),
                            SubscribeTime_month = view.findViewById(R.id.SubscribeTime_month);
                    SubscribeDate_month.setText(dateFormat_date.format(date));
                    SubscribeTime_month.setText(dateFormat_time.format(date));
                    break;
                case 2:
                    TextView ChartShowType_year = view.findViewById(R.id.ChartShowType_year);
                    ChartShowType_year.setText(Type);

                    TextView SubscribeDate_year = view.findViewById(R.id.SubscribeDate_year),
                            SubscribeTime_year = view.findViewById(R.id.SubscribeTime_year);
                    SubscribeDate_year.setText(dateFormat_date.format(date));
                    SubscribeTime_year.setText(dateFormat_time.format(date));
                    break;
                //變更Spinner內容
                case 3: //月報表
                    Spinner MonthSelectYear = view.findViewById(R.id.MonthSelectYear),
                            MonthSelectMonth = view.findViewById(R.id.MonthSelectMonth);
                    MonthSelectYear.setSelection(1);
                    if(States[1].equals("default_month"))
                        MonthSelectMonth.setSelection(Integer.valueOf(dateFormat_month.format(date)));
                    else
                        MonthSelectMonth.setSelection(Integer.valueOf(States[1]));
                    break;
                case 4: //年報表
                    Spinner YearSelectYear = view.findViewById(R.id.YearSelectYear);
                    YearSelectYear.setSelection(1);
                    break;
            }
        }
    };

    // 讀取歷史資料
    private void LoadData(String link, String DateType) {
        new Thread(() -> {
            InputStream is;
            try {
                Log.d("links", "link: " + link);
                URL url = new URL(link);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                is = urlConnection.getInputStream();
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(is));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = bufReader.readLine()) != null) builder.append(line + "\n");
                is.close();
                final String result = builder.toString();
                try {
                    chart = LineChartDay;
                    switch (DateType) {
                        case "day":
                            new Thread(() -> { //更改TextView
                                message = new Message();
                                message.what = 0;
                                handler.sendMessage(message);
                            }).start();
                            break;
                        case "month":
                            chart = LineChartMonth;
                            new Thread(() -> {
                                message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }).start();
                            break;
                        case "year":
                            chart = LineChartYear;
                            new Thread(() -> {
                                message = new Message();
                                message.what = 2;
                                handler.sendMessage(message);
                            }).start();
                            break;
                    }
                    Log.d("DateType", "DateType: " + DateType);

                    myDataSet = new ArrayList<>();
                    JSONArray jsonArray = new JSONArray(result);
                    final int numberOfItemsInResp = jsonArray.length();
                    for (String s : DataSetType) //取得資料轉為JSON
                        for (int i = 0; i < numberOfItemsInResp; i++)
                            myDataSet.add(jsonArray.getJSONObject(i).getString(s));
                    ChartSelect();
                } catch (JSONException e) { e.printStackTrace(); }
            } catch (IOException e) { e.printStackTrace(); }
        }).start();
    }

    // 報表初始化
    private int ChartData_Init(ArrayList<String> myDataSet) {
        sum = 0; sum2 = 0; sum3 = 0; SD = 0;
        return myDataSet.size();
    }

    private void ChartSelect() {
        if (myDataSet.size() != 0) {
            Log.d("type", Type);
            int TypeAmount = ChartData_Init(myDataSet);
            switch (Type) {
                case "TEMPFISH":
                    ChartCreate((TypeAmount / 5), 0, (TypeAmount / 5),
                            5f);
                    break;
                case "TDS":
                    ChartCreate((TypeAmount / 5), (4 * (TypeAmount / 5)), (5 * (TypeAmount / 5)),
                            50f);
                    break;
                case "PH":
                    ChartCreate((TypeAmount / 5), (3 * (TypeAmount / 5)), (4 * (TypeAmount / 5)),
                            1f);
                    break;
                case "ORP":
                    ChartCreate((TypeAmount / 5), (2 * (TypeAmount / 5)), (3 * (TypeAmount / 5)),
                            5f);
                    break;
                case "DO":
                    ChartCreate((TypeAmount / 5), (TypeAmount / 5), (2 * (TypeAmount / 5)),
                            5f);
                    break;

                case "D0":
                    ChartCreate((TypeAmount / 4), 0, (TypeAmount / 4),
                            5f);
                    break;
                case "D1":
                    ChartCreate((TypeAmount / 4), (TypeAmount / 4), (2 * (TypeAmount / 4)),
                            5f);
                    break;
                case "D2":
                    ChartCreate((TypeAmount / 4), (2 * (TypeAmount / 4)), (3 * (TypeAmount / 4)),
                            5f);
                    break;
                case "D3":
                    ChartCreate((TypeAmount / 4), (3 * (TypeAmount / 4)), (4 * (TypeAmount / 4)),
                            5f);
                    break;

                case "TEMPHOME":
                    ChartCreate((TypeAmount / 6), 0, (TypeAmount / 6),
                            5f);
                    break;
                case "HUMIDITY":
                    ChartCreate((TypeAmount / 6), (TypeAmount / 6), (2 * (TypeAmount / 6)),
                            5f);
                    break;
                case "HCHO":
                    ChartCreate((TypeAmount / 6), (2 * (TypeAmount / 6)), (3 * (TypeAmount / 6)),
                            5f);
                    break;
                case "PM1.0":
                    ChartCreate((TypeAmount / 6), (3 * (TypeAmount / 6)), (4 * (TypeAmount / 6)),
                            5f);
                    break;
                case "PM2.5":
                    ChartCreate((TypeAmount / 6), (4 * (TypeAmount / 6)), (5 * (TypeAmount / 6)),
                            5f);
                    break;
                case "PM10":
                    ChartCreate((TypeAmount / 6), (5 * (TypeAmount / 6)), (6 * (TypeAmount / 6)),
                            5f);
                    break;
            }
        }
        else Toast.makeText(mContext, "LineChart No Data", Toast.LENGTH_LONG).show();
    }

    private void ChartCreate(int a, int StartValue, int EndValue, float IntervalNum) {
        //int index = a;
        //N1位置 遞增值 5 次數 24 (24hr) -- i*5
        float ChartMaxHeight = 0;
        ArrayList<Entry> yvalue = new ArrayList<>();
        for (int i = StartValue; i < EndValue; i++) {
            yvalue.add(new Entry(i - StartValue, Float.valueOf(myDataSet.get(i))));
            sum += yvalue.get(i - StartValue).getY();

            //獲取圖表最大值
            if(yvalue.get(i - StartValue).getY() >= ChartMaxHeight)
                ChartMaxHeight = yvalue.get(i - StartValue).getY() + IntervalNum;
        }
        Data(chart, yvalue, a, ChartMaxHeight);
    }

    // 繪製報表資料
    private void Data(LineChart Chart, ArrayList<Entry> Yvalue, int a, float ChartMaxHeight) {
        float avg = sum / a;
        for (int j = 0; j < a; j++)
            sum2 += (Yvalue.get(j).getY() - avg) * (Yvalue.get(j).getY() - avg);
        sum3 = (sum2 / a);
        SD = (float) Math.sqrt(sum3);

        //正標準差
        LimitLine upper_limit = new LimitLine(avg + 2 * SD, "+SD");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(5f);

        //平均線
        LimitLine avg_limit = new LimitLine(avg, "Average");
        avg_limit.setLineWidth(10f);
        avg_limit.enableDashedLine(10f, 10f, 0f);
        avg_limit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        avg_limit.setTextSize(5f);

        //負標準差
        LimitLine lower_limit = new LimitLine(avg - 2 * SD, "-SD");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(5f);

        YAxis leftAxis = Chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(avg_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaximum(ChartMaxHeight);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0);
        leftAxis.setDrawLimitLinesBehindData(true);
        Chart.getAxisRight().setEnabled(false);

        LineDataSet set = new LineDataSet(Yvalue, "Data Set ");
        set.setFillAlpha(110);
        set.setColor(Color.RED);
        set.setLineWidth(3f);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.BLUE);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData data = new LineData(dataSets);
        Chart.setData(data);
        Chart.setVisibleXRangeMaximum(8);
        XAxis xAxis = Chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] values = new String[50];
        for (int i = 0; i <= a; i++) values[i] = "" + i;
        IAxisValueFormatter formatter = (value, axis) -> values[(int) value + 1];
        xAxis.setValueFormatter(formatter);

        Chart.invalidate();
    }
}
