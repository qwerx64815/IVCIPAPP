package com.example.pi.mqtt_test;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ControllerFishActuatorActivity extends Fragment {
    private Context mContext;
    private View view;
    private String Type = "d0", Topic = "Actuator", IPString;
    private int previous_position = 0, ListViewVisibleCount = 0, SelectedPosition = 0;
    private Message message;
    private MqttAndroidClient client;
    private Gson gson = new Gson();
    private final Map<String, String> LED = new HashMap<>();
    private String State = "0";
    private boolean Speech = false;
    private int[] TypeStates = new int[] {0, 0, 0, 0}; //d0, d1, d2, d3

    ControllerFishActuatorActivity(Context mContext, ArrayList<String> IPStr) {
        this.mContext = mContext;
        IPString = IPStr.get(0) + ":" + IPStr.get(1);
        Log.d("IPString", IPString);
    }

    ControllerFishActuatorActivity(
            Context mContext, String Type, String[] States, ArrayList<String> IPStr) {
        this.mContext = mContext;
        this.Type = Type;
        IPString = IPStr.get(0) + ":" + IPStr.get(1);

        int state = 0;
        switch (States[0]) {
            case "on":
                state = 1;
                break;
            case "off":
                state = 0;
                break;
        }

        switch (Type) {
            case "d0":
                TypeStates[0] = state;
                break;
            case "d1":
                TypeStates[1] = state;
                break;
            case "d2":
                TypeStates[2] = state;
                break;
            case "d3":
                TypeStates[3] = state;
                break;
        }
        Speech = true;
    }

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(
                R.layout.view_controller_fish,
                container,
                false
        );
        view.setBackgroundResource(R.mipmap.diversbg);

        ListViewAddData();
        MQTTConnect();
        SetCrontrollerEvent();

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
        String[] MenuStr = new String[] {"", "", "", ""};
        ListView Left_Type_List = view.findViewById(R.id.Left_Type_List);
        setListViewHeight(Left_Type_List);

        // ListView 填充資料
        Left_Type_List.setAdapter(new ArrayAdapter<String>(
                mContext,
                android.R.layout.simple_expandable_list_item_1, MenuStr) {
            @Override //預設首個選項背景色
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);
                if (position == SelectedPosition) {
                    //row.setBackgroundColor(Color.GRAY);
                    switch (position) {
                        case 0:
                            row.setBackgroundResource(R.drawable.d0_on);
                            break;
                        case 1:
                            row.setBackgroundResource(R.drawable.d1_on);
                            break;
                        case 2:
                            row.setBackgroundResource(R.drawable.d2_on);
                            break;
                        case 3:
                            row.setBackgroundResource(R.drawable.d3_on);
                            break;
                    }
                }
                else {
                    //row.setBackground(null);
                    switch (position) {
                        case 0:
                            row.setBackgroundResource(R.drawable.d0);
                            break;
                        case 1:
                            row.setBackgroundResource(R.drawable.d1);
                            break;
                        case 2:
                            row.setBackgroundResource(R.drawable.d2);
                            break;
                        case 3:
                            row.setBackgroundResource(R.drawable.d3);
                            break;
                    }
                }
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
                switch (FirstPosition) {
                    case 0:
                        parent.getChildAt(temp).setBackgroundResource(R.drawable.d0);
                        break;
                    case 1:
                        parent.getChildAt(temp).setBackgroundResource(R.drawable.d1);
                        break;
                    case 2:
                        parent.getChildAt(temp).setBackgroundResource(R.drawable.d2);
                        break;
                    case 3:
                        parent.getChildAt(temp).setBackgroundResource(R.drawable.d3);
                        break;
                }
                //parent.getChildAt(temp).setBackground(null);
                temp++;
                FirstPosition++;
            }

            // 將選擇項目設背景顏色
            int n = parent.getFirstVisiblePosition(); //ListView 可視範圍第一項
            int ChildIndex = position - n;
            switch (position) {
                case 0:
                    parent.getChildAt(ChildIndex).setBackgroundResource(R.drawable.d0_on);
                    break;
                case 1:
                    parent.getChildAt(ChildIndex).setBackgroundResource(R.drawable.d1_on);
                    break;
                case 2:
                    parent.getChildAt(ChildIndex).setBackgroundResource(R.drawable.d2_on);
                    break;
                case 3:
                    parent.getChildAt(ChildIndex).setBackgroundResource(R.drawable.d3_on);
                    break;
            }
            //parent.getChildAt(ChildIndex).setBackgroundColor(Color.GRAY);

            TypeChangeView(position);
        });
    }

    //根據 Type 切換 View
    private void TypeChangeView() {
        // 切換 Controller 種類
        TextView Title_Main = view.findViewById(R.id.Title_Main);
        switch (Type) {
            case "d0":
                SelectedPosition = 0;
                Title_Main.setText("電燈 Light");
                break;
            case "d1":
                SelectedPosition = 1;
                Title_Main.setText("打氣機 Air");
                break;
            case "d2":
                SelectedPosition = 2;
                Title_Main.setText("加熱棒 Heater");
                break;
            case "d3":
                SelectedPosition = 3;
                Title_Main.setText("過濾器 Percolator");
                break;
        }
    }

    //根據 Position 切換 View
    private void TypeChangeView(int position) {
        TextView Title_Main = view.findViewById(R.id.Title_Main);
        switch (position) {
            case 0:
                Type = "d0";
                Title_Main.setText("LED燈 D0");
                if(TypeStates[0] == 0)
                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                else ((ImageButton)view.findViewById(R.id.ControllerBar))
                        .setImageDrawable(getResources().getDrawable(R.drawable.open));
                break;
            case 1:
                Type = "d1";
                Title_Main.setText("氣泡石 D1");
                if(TypeStates[1] == 0)
                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                else ((ImageButton)view.findViewById(R.id.ControllerBar))
                        .setImageDrawable(getResources().getDrawable(R.drawable.open));
                break;
            case 2:
                Type = "d2";
                Title_Main.setText("加熱棒 D2");
                if(TypeStates[2] == 0)
                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                else ((ImageButton)view.findViewById(R.id.ControllerBar))
                        .setImageDrawable(getResources().getDrawable(R.drawable.open));
                break;
            case 3:
                Type = "d3";
                Title_Main.setText("過濾器 D3");
                if(TypeStates[3] == 0)
                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                else ((ImageButton)view.findViewById(R.id.ControllerBar))
                        .setImageDrawable(getResources().getDrawable(R.drawable.open));
                break;
        }
        if(position != previous_position) {
            previous_position = position;
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
        String host = "tcp://" + IPString,
                Password = "password",
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
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(
                            mContext,
                            "已離線", Toast.LENGTH_SHORT).show(); //連線失敗
                    new Thread(() -> { //更改連線狀態TextView
                        message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }).start();
                }
            });
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(
                            mContext,
                            "連線失敗",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {}

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
        } catch (MqttException e) { e.printStackTrace(); }
    }

    // UI 多線程(TextView)
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView Connect_State = view.findViewById(R.id.ConnectState);
            switch (msg.what) {
                case 0:
                    Connect_State.setText("已連線");
                    if(Speech) {
                        switch (Type) {
                            case "d0":
                                if(TypeStates[0] == 0)
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                                else
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.open));
                                LED.put(Type, String.valueOf(TypeStates[0]));
                                break;
                            case "d1":
                                if(TypeStates[1] == 0)
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                                else
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.open));
                                LED.put(Type, String.valueOf(TypeStates[1]));
                                break;
                            case "d2":
                                if(TypeStates[2] == 0)
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                                else
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.open));
                                LED.put(Type, String.valueOf(TypeStates[2]));
                                break;
                            case "d3":
                                if(TypeStates[3] == 0)
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.close));
                                else
                                    ((ImageButton)view.findViewById(R.id.ControllerBar))
                                            .setImageDrawable(getResources().getDrawable(R.drawable.open));
                                LED.put(Type, String.valueOf(TypeStates[3]));
                                break;
                        }
                        Log.d("State", "Call: Speech");
                        Publish();
                        Speech = false;
                    }
                    break;
                case 1:
                    Connect_State.setText("已離線");
                    break;
            }
        }
    };

    private void Publish() {
        String message = gson.toJson(LED);
        Log.d("State", "State: " + State);
        Log.d("State", "Type: " + Type);
        Log.d("State", "Topic: " + Topic);
        Log.d("State", "message: " + message);
        Log.d("State", "message: " + Arrays.toString(message.getBytes()));
        try { client.publish(Topic, message.getBytes(), 1, false); }
        catch (MqttException e) { e.printStackTrace(); }
    }

    private void SetCrontrollerEvent() {
        view.findViewById(R.id.ControllerBar).setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (Type) {
                    case "d0":
                        if(TypeStates[0] == 0) {
                            TypeStates[0] = 1;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.open));
                        }
                        else {
                            TypeStates[0] = 0;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.close));
                        }
                        LED.put(Type, String.valueOf(TypeStates[0]));
                        break;
                    case "d1":
                        if(TypeStates[1] == 0) {
                            TypeStates[1] = 1;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.open));
                        }
                        else {
                            TypeStates[1] = 0;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.close));
                        }
                        LED.put(Type, String.valueOf(TypeStates[1]));
                        break;
                    case "d2":
                        if(TypeStates[2] == 0) {
                            TypeStates[2] = 1;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.open));
                        }
                        else {
                            TypeStates[2] = 0;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.close));
                        }
                        LED.put(Type, String.valueOf(TypeStates[2]));
                        break;
                    case "d3":
                        if(TypeStates[3] == 0) {
                            TypeStates[3] = 1;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.open));
                        }
                        else {
                            TypeStates[3] = 0;
                            ((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.close));
                        }
                        LED.put(Type, String.valueOf(TypeStates[3]));
                        break;
                }
                Log.d("State", "Call: Button");
                Publish();

                Date date = new Date();
                DateFormat dateFormat_date = new SimpleDateFormat("- yyyy 年 MM 月 dd 日");
                TextView SubscribeDate = view.findViewById(R.id.SubscribeDate);
                SubscribeDate.setText(dateFormat_date.format(date));

                DateFormat dateFormat_time = new SimpleDateFormat("- HH 時 mm 分 ss 秒");
                TextView SubscribeTime = view.findViewById(R.id.SubscribeTime);
                SubscribeTime.setText(dateFormat_time.format(date));
            }

            return true;
        });
    }
}
