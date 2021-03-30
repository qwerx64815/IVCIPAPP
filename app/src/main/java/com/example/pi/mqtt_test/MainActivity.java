package com.example.pi.mqtt_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    private AutoCompleteTextView HostEditText, MQTTPortEditText, SQLPortEditText, SpeechPortEditText;

    MainActivity() { mContext = this; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //取得手機高度 並自適應高度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int vHeight = dm.heightPixels;
        ConstraintLayout cl = findViewById(R.id.mainlayout);
        cl.getLayoutParams().height = (int)(vHeight * 0.9);

        findView();
        new EditTextClear(this);
    }

    private void findView() {
        HostEditText = findViewById(R.id.HostEditText);
        MQTTPortEditText = findViewById(R.id.MQTTPortEditText);
        SQLPortEditText = findViewById(R.id.SQLPortEditText);
        SpeechPortEditText = findViewById(R.id.SpeechPortEditText);
    }

    //即時動態按鈕
    public void DashBoardClick(View v) {
        if(HostEditText.getText().toString().equals("")
                || HostEditText.getText().toString().split("\\.").length < 4
                || MQTTPortEditText.getText().toString().equals("")
                || SpeechPortEditText.getText().toString().equals(""))
            Toast.makeText(mContext, "請再次檢查 Host、MQTTPort 及 SpeechPort !", Toast.LENGTH_LONG).show();
        else StartIntent("DashBoard",
                HostEditText.getText().toString(),
                MQTTPortEditText.getText().toString(),
                SQLPortEditText.getText().toString(),
                SpeechPortEditText.getText().toString());
    }

    //即時控制按鈕
    public void ControllerClick(View v) {
        if(HostEditText.getText().toString().equals("")
                || HostEditText.getText().toString().split("\\.").length < 4
                || MQTTPortEditText.getText().toString().equals("")
                || SpeechPortEditText.getText().toString().equals(""))
            Toast.makeText(mContext, "請再次檢查 Host、MQTTPort 及 SpeechPort !", Toast.LENGTH_LONG).show();
        else StartIntent("Controller",
                HostEditText.getText().toString(),
                MQTTPortEditText.getText().toString(),
                SQLPortEditText.getText().toString(),
                SpeechPortEditText.getText().toString());
    }

    //歷史資料按鈕
    public void ChartClick(View v) {
        if(HostEditText.getText().toString().equals("")
                || HostEditText.getText().toString().split("\\.").length < 4
                || SQLPortEditText.getText().toString().equals("")
                || SpeechPortEditText.getText().toString().equals(""))
            Toast.makeText(mContext, "請再次檢查 Host、SQLPort 及 SpeechPort !", Toast.LENGTH_LONG).show();
        else StartIntent("Chart",
                HostEditText.getText().toString(),
                MQTTPortEditText.getText().toString(),
                SQLPortEditText.getText().toString(),
                SpeechPortEditText.getText().toString());
    }

    //創建Intent
    private void StartIntent(String Class, String host, String MQTTPort,
                             String SQLPort, String SpeechPort) {
        ArrayList<String> IPStr = new ArrayList<>();
        IPStr.add(host);
        IPStr.add(MQTTPort);
        IPStr.add(SQLPort);
        IPStr.add(SpeechPort);
        Log.d("StartIntent", IPStr.get(0));

        Intent it = new Intent(this, MainFrameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Class", Class);
        bundle.putStringArrayList("IPStr", IPStr);
        it.putExtras(bundle);
        startActivity(it);
    }

    //觸控事件判斷
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) hideKeyboard(v.getWindowToken());
        }
        return super.dispatchTouchEvent(ev);
    }

    //判斷使用者點擊位置是否為EditText 否則退出軟鍵盤
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) return false;
            else return true;
        } return false;
    }

    //隱藏軟鍵盤
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //EditText清空事件
    private class EditTextClear extends MainActivity {
        private Context mContext;
        private ArrayList<String> Input_Host, Input_MQTTPort, Input_SQLPort, Input_SpeechPort;

        EditTextClear(Context mContext) {
            super();
            this.mContext = mContext;
            InitArrList();
            AddEditTextEvent(HostEditText, ((Activity) mContext).findViewById(R.id.imgBtn_Clear_Host));
            AddEditTextEvent(MQTTPortEditText, ((Activity) mContext).findViewById(R.id.imgBtn_Clear_MQTTPort));
            AddEditTextEvent(SQLPortEditText, ((Activity) mContext).findViewById(R.id.imgBtn_Clear_SQLPort));
            AddEditTextEvent(SpeechPortEditText, ((Activity) mContext).findViewById(R.id.imgBtn_Clear_SpeechPort));
        }

        //EditText下拉式選單內容
        private void InitArrList() {
            //Host
            Input_Host = new ArrayList<>();
            Input_Host.add("192.168.1.100");
            Input_Host.add("203.68.252.54");
            AutoComplete(Input_Host, HostEditText);

            //MQTTPort
            Input_MQTTPort = new ArrayList<>();
            Input_MQTTPort.add("1883");
            Input_MQTTPort.add("8099");
            AutoComplete(Input_MQTTPort, MQTTPortEditText);

            //SQLPort
            Input_SQLPort = new ArrayList<>();
            Input_SQLPort.add("1880");
            Input_SQLPort.add("8058");
            AutoComplete(Input_SQLPort, SQLPortEditText);

            //SpeechPort
            Input_SpeechPort = new ArrayList<>();
            Input_SpeechPort.add("50051");
            Input_SpeechPort.add("8089");
            AutoComplete(Input_SpeechPort, SpeechPortEditText);
        }

        //自動完成EditText下拉式選單
        private void AutoComplete(ArrayList<String> InputList, AutoCompleteTextView edittext) {
            ArrayAdapter<String> ad_host = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_dropdown_item_1line, InputList);
            edittext.setThreshold(1);
            edittext.setAdapter(ad_host);
            edittext.setOnClickListener(v -> edittext.showDropDown());
        }

        private void AddEditTextEvent(EditText edittext, ImageView img) {
            edittext.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length() > 0) img.setVisibility(View.VISIBLE);
                    else img.setVisibility(View.INVISIBLE);
                    /*if(s.length() > 0) new Thread(() -> {
                        message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }).start();
                    else new Thread(() -> {
                        message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    }).start();*/
                }
            });

            //清空EditText
            img.setOnClickListener(v -> edittext.setText(""));
        }
    }

    // UI 多線程
    /*private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageView img = ((Activity) mContext).findViewById(R.id.imgBtn_Clear_Host);
            switch (msg.what) {
                case 0:
                    img.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    img.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };*/
}

