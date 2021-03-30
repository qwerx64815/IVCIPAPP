package com.example.pi.mqtt_test;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DashboardActivity extends Fragment {
    private Context mContext;
    private View view;
    private WeighingMeterView wmv;
    private ArrayList<Float> myDataSet = new ArrayList<>();
    private MqttAndroidClient client;
    private String Type, Topic, IPString;
    private String[] States = new String[]{}, TypeString, TypeTitle, DataSetType, ListViewStr;
    private int FillLayout, previous_position = 0, ListViewVisibleCount = 0, SelectedPosition = 0;
    private int[] ListImgBtnOn, ListImgBtnOff;
    private Message message;

    //手動部份初始化
    DashboardActivity(Context mContext, ArrayList<String> IPStr, String DefaultTopic,
                  int[] ListImgBtnON, int[] ListImgBtnOFF, int FillLayout,
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
        this.TypeString = TypeString; //Ex. ["TEMPFISH", "TDS", "PH", "ORP", "DO"]
        this.TypeTitle = TypeTitle; //Ex. ["溫度 Temp", "總溶解固體 TDS", "酸鹼值 PH", "氧化還原值 ORP", "溶氧量 DO"]
        this.DataSetType = DataSetType; //Ex. {"temp", "do", "orp", "ph", "tds"}
        this.ListViewStr = ListViewStr; //Ex. {"", "", "", "", ""}
    }

    //語音部份初始化
    DashboardActivity(
            Context mContext, ArrayList<String> IPStr, String DefaultType, String DefaultTopic,
            String[] States, int[] ListImgBtnON, int[] ListImgBtnOFF, int FillLayout,
            String[] TypeString, String[] TypeTitle, String[] DataSetType,
            String[] ListViewStr) {
        this.mContext = mContext;
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
        Log.d("speech", "States: " + States[0]);
        Log.d("speech", "IPString: " + IPString);
        this.ListImgBtnOn = ListImgBtnON; //ListView BtnImg
        this.ListImgBtnOff = ListImgBtnOFF;
        this.FillLayout = FillLayout;
        this.TypeString = TypeString; //Ex. {"TEMP", "TDS", "PH", "ORP", "DO"}
        this.TypeTitle = TypeTitle; //Ex. {"溫度 Temp", "總溶解固體 TDS", "酸鹼值 PH", "氧化還原值 ORP", "溶氧量 DO"}
        this.DataSetType = DataSetType; //Ex. {"temp", "do", "orp", "ph", "tds"}
        this.ListViewStr = ListViewStr; //Ex. {"", "", "", "", ""}
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(FillLayout, container, false);
        switch (FillLayout) {
            case R.layout.view_dashboard_fish:
                view.setBackgroundResource(R.mipmap.diversbg);
                break;
            case R.layout.view_dashboard_home:
                view.setBackgroundResource(R.mipmap.brickbg);
                break;
        }

        ListViewAddData();
        MQTTConnect();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.unregisterResources();
        client.close();
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
            ListViewVisibleCount = parent.getLastVisiblePosition() - FirstPosition;
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
        // 切換 Dashboard 種類
        for(int i = 0; i < TypeString.length; i++)
            if(TypeString[i].equals(Type)) SelectedPosition = i;
        new Thread(() -> {
            message = new Message();
            message.what = 42;
            handler.sendMessage(message);
        }).start();
    }

    //根據 Position 切換 View
    private void TypeChangeView(int position) {
        new Thread(() -> {
            message = new Message();
            message.what = 41;
            handler.sendMessage(message);
        }).start();

        if(myDataSet.size() != 0) wmv.setPercentData(myDataSet.get(position), new DecelerateInterpolator());
        else wmv.setPercentData(0, new DecelerateInterpolator());

        if(position != previous_position) {
            previous_position = position;
            Type = TypeString[position];
            wmv.setType(Type);
        }
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

    // MQTT 連線
    private void MQTTConnect() {
        new Thread(() -> {
            message = new Message();
            message.what = 5;
            handler.sendMessage(message);
        }).start();

        String host = "tcp://" + IPString, Password = "password",
                clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(mContext, host, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true); //自動重新連線
        options.setConnectionTimeout(60);
        options.setUserName("admin");
        options.setPassword(Password.toCharArray());
        try {
            client.connect(options).setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(
                            mContext,
                            "已連線", Toast.LENGTH_SHORT).show(); //連線成功
                    new Thread(() -> { //更改連線狀態TextView
                        message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }).start();
                    subscribe(client, Topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(mContext, "已離線", Toast.LENGTH_SHORT).show(); //連線失敗
                    new Thread(() -> {
                        message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }).start();
                }
            });
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(mContext, "連線失敗", Toast.LENGTH_SHORT).show();
                }

                // 傳過來的訊息內容
                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    try {
                        Toast.makeText(mContext, Type + " 資料收到!", Toast.LENGTH_SHORT).show();
                        new Thread(() -> {
                            Message messageText = new Message();
                            messageText.what = 2;
                            handler.sendMessage(messageText);
                        }).start();

                        myDataSet = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(new String(message.getPayload()));
                        for (String s : DataSetType)
                            myDataSet.add(Float.parseFloat(jsonObject.getString(s)));

                        switch (Type) {
                            //水族部分
                            case "TEMPFISH":
                                wmv.setPercentData(myDataSet.get(0), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 30;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuewater", "TEMPFISH: " + myDataSet.get(0));
                                break;
                            case "TDS":
                                wmv.setPercentData(myDataSet.get(1), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 31;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuewater", "TDS: " + myDataSet.get(1));
                                break;
                            case "PH":
                                myDataSet.set(2, myDataSet.get(2) / 56 + 7);
                                wmv.setPercentData(myDataSet.get(2), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 32;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuewater", "PH: " + myDataSet.get(2));
                                break;
                            case "ORP":
                                wmv.setPercentData(myDataSet.get(3), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 33;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuewater", "ORP: " + myDataSet.get(3));
                                break;
                            case "DO":
                                wmv.setPercentData(myDataSet.get(4), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 34;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuewater", "DO: " + myDataSet.get(4));
                                break;

                            //居家部分
                            case "TEMPHOME":
                                wmv.setPercentData(myDataSet.get(0), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 30;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuehome", "TEMPHOME: " + myDataSet.get(0));
                                break;
                            case "HUMIDITY":
                                wmv.setPercentData(myDataSet.get(1), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 31;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuehome", "Humidity: " + myDataSet.get(1));
                                break;
                            case "HCHO":
                                wmv.setPercentData(myDataSet.get(2), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 32;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuehome", "HCHO: " + myDataSet.get(2));
                                break;
                            case "PM1.0":
                                wmv.setPercentData(myDataSet.get(3), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 33;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuehome", "PM 1.0: " + myDataSet.get(3));
                                break;
                            case "PM2.5":
                                wmv.setPercentData(myDataSet.get(4), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 34;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuehome", "PM 2.5: " + myDataSet.get(4));
                                break;
                            case "PM10":
                                wmv.setPercentData(myDataSet.get(5), new DecelerateInterpolator());
                                new Thread(() -> {
                                    Message messageText = new Message();
                                    messageText.what = 35;
                                    handler.sendMessage(messageText);
                                }).start();
                                Log.d("valuehome", "PM 10: " + myDataSet.get(5));
                                break;
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
        } catch (MqttException e) { e.printStackTrace(); }
    }

    // UI 多線程
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView Connect_State = view.findViewById(R.id.ConnectState),
                    LastPublishValueText = view.findViewById(R.id.LastPublishValueText),
                    Title_Main = view.findViewById(R.id.Title_Main);
            switch (msg.what) {
                //MQTT是否連線
                case 0:
                    Connect_State.setText("已連線");
                    break;
                case 1:
                    Connect_State.setText("已離線");
                    break;
                //資料傳送時的時間
                case 2:
                    Date date = new Date();
                    DateFormat dateFormat_date = new SimpleDateFormat("- yyyy 年 MM 月 dd 日");
                    TextView SubscribeDate = view.findViewById(R.id.SubscribeDate);
                    SubscribeDate.setText(dateFormat_date.format(date));

                    DateFormat dateFormat_time = new SimpleDateFormat("- HH 時 mm 分 ss 秒");
                    TextView SubscribeTime = view.findViewById(R.id.SubscribeTime);
                    SubscribeTime.setText(dateFormat_time.format(date));
                    break;
                //目前資料數據
                case 30:
                    LastPublishValueText.setText(String.valueOf(myDataSet.get(0)));
                    break;
                case 31:
                    LastPublishValueText.setText(String.valueOf(myDataSet.get(1)));
                    break;
                case 32:
                    LastPublishValueText.setText(String.valueOf(myDataSet.get(2)));
                    break;
                case 33:
                    LastPublishValueText.setText(String.valueOf(myDataSet.get(3)));
                    break;
                case 34:
                    LastPublishValueText.setText(String.valueOf(myDataSet.get(4)));
                    break;
                case 35:
                    LastPublishValueText.setText(String.valueOf(myDataSet.get(5)));
                    break;
                //主標題變更
                case 41:
                    Title_Main.setText(TypeTitle[SelectedPosition]);
                    break;
                case 42:
                    Title_Main.setText(TypeTitle[SelectedPosition]);
                    break;
                case 5:
                    wmv = view.findViewById(R.id.CLWeighing);
                    wmv.setType(Type);
                    break;
            }
        }
    };

    // MQTT 訂閱
    private void subscribe(MqttAndroidClient client, String topic) { //訂閱的方法
        try {
            client.subscribe(topic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MqttService", "onSuccess: Success to Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("MqttService", "onFailure: Failed to subscribe");
                }
            });
        } catch (MqttException e) {
            Log.d("MqttService", "subscribeToTopic: Exception whilst subscribing");
            e.printStackTrace();
        }
    }
}
