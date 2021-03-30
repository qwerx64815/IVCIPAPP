package com.example.pi.mqtt_test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.multidex.MultiDex;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.VoiceToText.TextGrpc;
import io.grpc.VoiceToText.TextRequest;
import io.grpc.VoiceToText.command_list;

public class MainFrameActivity extends AppCompatActivity {
    private String User_Speech_Line;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ArrayList<String> IPStr_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_main_frame);

        //取得手機高度並自適應高度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int vHeight = dm.heightPixels;
        ConstraintLayout cl = findViewById(R.id.mainframelayout);
        cl.getLayoutParams().height = (int)(vHeight * 0.9);

        //Tab 和 Page 區塊
        CenterArea();

        //語音區塊
        new SpeechArea();
    }

    @Override
    protected void onDestroy() { super.onDestroy(); }

    private void CenterArea() {
        //返回按鈕
        findViewById(R.id.BackPrePage_btn).setOnClickListener(v -> finish());

        //依照 Tab 進行切換的頁面
        mViewPager = findViewById(R.id.Container);
        tabLayout = findViewById(R.id.Tabs);
        Bundle bundle = getIntent().getExtras();
        IPStr_all = bundle.getStringArrayList("IPStr");
        SetPageName(bundle.getString("Class"));
        SetupViewPager(mViewPager, bundle.getString("Class"));
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void SetPageName(String Class) {
        TextView PageClassNameText = findViewById(R.id.PageClassNameText);
        switch(Class) {
            case "DashBoard":
                PageClassNameText.setText("【即時動態頁面】");
                break;
            case "Controller":
                PageClassNameText.setText("【即時控制頁面】");
                break;
            case "Chart":
                PageClassNameText.setText("【歷史數據頁面】");
                break;
        }
    }

    private void SetupViewPager(ViewPager viewPager, String Class) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        ArrayList<String> IPStr = new ArrayList<>();
        IPStr.add(IPStr_all.get(0));
        switch (Class) {
            case "DashBoard":
                IPStr.add(IPStr_all.get(1));
                adapter.addFragment(
                        new DashboardFishWaterSensorActivity(this, IPStr),
                        "水族養殖");
                adapter.addFragment(
                        new DashboardHomeActivity(this, IPStr),
                        "室內居家");
                break;
            case "Controller":
                IPStr.add(IPStr_all.get(1));
                adapter.addFragment(
                        new ControllerFishActuatorActivity(this, IPStr),
                        "水族養殖");
                break;
            case "Chart":
                IPStr.add(IPStr_all.get(2));
                adapter.addFragment(
                        new ChartFishWaterSensorActivity(this, IPStr),
                        "水族養殖 感測器");
                adapter.addFragment(
                        new ChartFishActuatorActivity(this, IPStr),
                        "水族養殖 致動器");
                adapter.addFragment(
                        new ChartHomeActivity(this, IPStr),
                        "室內居家");
                break;
        }
        viewPager.setAdapter(adapter);
    }

    private void SetupViewPager(ViewPager viewPager, String Class, String Type, String[] States) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        ArrayList<String> IPStr = new ArrayList<>();
        IPStr.add(IPStr_all.get(0));
        switch (Class) {
            case "DashBoard":
                IPStr.add(IPStr_all.get(1));
                SetPageName("DashBoard");
                adapter.addFragment(
                        new DashboardFishWaterSensorActivity(this, Type, States, IPStr),
                        "水族養殖");
                adapter.addFragment(
                        new DashboardHomeActivity(this, Type, States, IPStr),
                        "室內居家");
                break;
            case "Controller":
                IPStr.add(IPStr_all.get(1));
                SetPageName("Controller");
                adapter.addFragment(
                        new ControllerFishActuatorActivity(this, Type, States, IPStr),
                        "水族養殖");
                break;
            case "Chart":
                IPStr.add(IPStr_all.get(2));
                SetPageName("Chart");
                adapter.addFragment(
                        new ChartFishWaterSensorActivity(this, Type, States, IPStr),
                        "水族養殖 感測器");
                adapter.addFragment(
                        new ChartFishActuatorActivity(this, Type, States, IPStr),
                        "水族養殖 致動器");
                adapter.addFragment(
                        new ChartHomeActivity(this, Type, States, IPStr),
                        "室內居家");
                break;
        }
        viewPager.setAdapter(adapter);
    }

    private void ResultToArrayList(String result) {
        ArrayList<String> JSONDataSet = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(result);
            Log.d("JSONObject", object.getString("target"));
            JSONDataSet.add(object.getString("target"));
            JSONDataSet.add(object.getString("location"));
            JSONDataSet.add(object.getString("function"));
            JSONDataSet.add(object.getString("states"));
        } catch (JSONException e) { e.printStackTrace(); }

        //JSONDataSet[target[], location[], function[], states[]]
        String[] States = JSONDataSet.get(3).split("\\s+");
        Log.d("JSONDataSet", "states.length: " + States.length);
        switch(JSONDataSet.get(2)) {
            case "setturn":
                mViewPager.removeAllViews();
                SetupViewPager(mViewPager, "Controller", JSONDataSet.get(0), States);
                Log.d("JSONDataSet", "over!");
                break;
            case "search":
                mViewPager.removeAllViews();
                String[] search_class = JSONDataSet.get(0).split("\\s+");
                if(JSONDataSet.get(3).equals("now")) {
                    if(search_class[1].equals("pm"))
                        SetupViewPager(mViewPager, "DashBoard", search_class[1] + search_class[2], States);
                    else SetupViewPager(mViewPager, "DashBoard", search_class[1], States);
                    switch (search_class[0]) {
                        case "watersensor":
                            tabLayout.getTabAt(0).select();
                            break;
                        case "pms":
                            tabLayout.getTabAt(1).select();
                            break;
                    }
                } else {
                    if(search_class[1].equals("pm"))
                        SetupViewPager(
                                mViewPager, "Chart", search_class[1] + search_class[2], States);
                    else SetupViewPager(mViewPager, "Chart", search_class[1], States);
                    switch (search_class[0]) {
                        case "watersensor":
                            tabLayout.getTabAt(0).select();
                            break;
                        case "actuator":
                            tabLayout.getTabAt(1).select();
                            break;
                        case "pms":
                            tabLayout.getTabAt(2).select();
                            break;
                    }
                }
                break;
        }
    }

    private class SpeechArea {
        private ConstraintLayout SpeechArea;
        private View v_bottom;

        SpeechArea() { Bottom(); }

        // 底部區塊
        private void Bottom() {
            SpeechArea = findViewById(R.id.SpeechArea);
            BottomHide();
        }

        // 底部視窗收合時
        private void BottomHide() { //view_bottom_bar
            SpeechArea.removeView(v_bottom);
            v_bottom = getLayoutInflater()
                    .inflate(R.layout.view_bottom_bar, SpeechArea, false);
            SpeechArea.addView(v_bottom);
            findViewById(R.id.imageButton_up).setOnClickListener(v -> BottomShow());
        }

        // 底部視窗展開時
        private void BottomShow() { //view_bottom_windows
            SpeechArea.removeView(v_bottom);
            v_bottom = getLayoutInflater()
                    .inflate(R.layout.view_bottom_windows, SpeechArea, false);
            SpeechArea.addView(v_bottom);
            findViewById(R.id.imageButton_down).setOnClickListener(v -> BottomHide());
            findViewById(R.id.imgBtn_Speech).setOnClickListener(v -> {
                //判斷是否連網
                ConnectivityManager cm =
                        (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                if(info == null || !info.isConnected()) { return; }
                try {
                    SpeechClick();
                    if(User_Speech_Line.length() != 0) SendMessage();
                } catch (Exception ignored) {}
            });
        }

        //語音辨識按鈕事件
        private void SpeechClick() {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說");
            startActivityForResult(intent, 1);
        }
    }

    //利用gRPC傳送字串到Server端
    private void SendMessage() {
        /*EditText Speech_Result_TextView = findViewById(R.id.Speech_Result_EditText);
        Speech_Result_TextView.setText("");
        Speech_Result_TextView.setHint("Please Wait...");*/
        Toast.makeText(this, User_Speech_Line, Toast.LENGTH_SHORT).show();
        new GrpcTask(this, output -> {
            Log.d("result_output", output);
            ResultToArrayList(output);
        }).execute(
                IPStr_all.get(0),
                User_Speech_Line,
                IPStr_all.get(3)
        );
    }

    //取得Google API最佳辨識結果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //把所有辨識的可能結果印出來 第一筆最相符
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                User_Speech_Line = result.get(0);
                SendMessage();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // Grpc 傳輸內容
    private static class GrpcTask extends AsyncTask<String, Void, String> {
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;
        private AsyncResponse delegate;

        public interface AsyncResponse { void processFinish(String output); }

        private GrpcTask(Activity activity, AsyncResponse delegate) {
            this.activityReference = new WeakReference<>(activity);
            this.delegate = delegate;
        }

        //執行中 在背景做事情
        @Override
        protected String doInBackground(String... params) {
            String host = params[0];
            String message = params[1];
            String portStr = params[2];
            int port = TextUtils.isEmpty(portStr) ? 0 : Integer.valueOf(portStr);
            try {
                channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
                TextGrpc.TextBlockingStub stub = TextGrpc.newBlockingStub(channel);
                TextRequest request = TextRequest.newBuilder().setText(message).build();
                command_list response = stub.showText(request);
                return response.getData();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return String.format("Failed... : %n%s", sw);
            }
        }

        //執行後 完成背景任務
        @Override
        protected void onPostExecute(String result) {
            try { channel.shutdown().awaitTermination(1, TimeUnit.SECONDS); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            Activity activity = activityReference.get();
            if (activity == null || activity.isFinishing() || activity.isDestroyed()) { return; }

            // 回傳結果
            /*TextView resultText = activity.findViewById(R.id.Speech_Result_EditText);
            resultText.setText(result + "\n");
            Log.d("result_string", resultText.getText().toString());*/
            delegate.processFinish(result);
        }
    }
}