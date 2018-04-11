/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lqSPRimaging.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.lqSPRimaging.R;
import com.lqSPRimaging.util.CameraUtil;
import com.lqSPRimaging.util.ChartUtil;
import com.lqSPRimaging.util.SaveUtil;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYSeries;

import java.io.File;
import java.util.Arrays;

public class ImageSPR extends Activity {

    private Switch button_record;
    private Switch button_start;
    private Switch button_OnOff;


    private TextView textview1;
    private TextView textview2;
    private TextView textview3;
    private TextView textview4;

    private LinearLayout layout;

    private SurfaceView mPreviewSV = null;
    private CameraUtil cameraUtil;

    private Context context;
    private ChartUtil chartUtil;

    //模式
    private int mode = 1;

    private double[] result;
    private int t = 0;

    //用以添加数据点的数据系列
    private XYSeries series1 = null;
    private XYSeries series2 = null;
    private XYSeries series3 = null;
    private XYSeries series4 = null;

    private GraphicalView mChartView;
    private SaveUtil saveUtil;

    private boolean newFileFlag = true;
    private File file = null;

    private Handler handler;

    private long mExitTime;
    private Runnable getResult = new Runnable() {

        @Override
        public void run() {
            result = cameraUtil.getResult();
            System.out.println(Arrays.toString(result));
            // 显示
            textViewDisplay(mode);
            handler.postDelayed(getResult, 200);
        }
    };
    private Runnable saveResult = new Runnable() {

        @Override
        public void run() {
            chartDisplay(t, result, mode);
            saveToSDcard(t, result);
            t = t + 1;
            handler.postDelayed(saveResult, 200);

        }
    };
    OnCheckedChangeListener toggleButtonListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.start:
                    if (isChecked) {
                        doJob(mode);
                        button_record.setEnabled(true);

                    } else {
                        // Determine if there is data
                        // Cancel record
                        if (button_record.isChecked()) {
                            button_record.setChecked(false);
                        }
                        if (t != 0 && newFileFlag) {
                            newDetection();
                        }
                        handler.removeCallbacks(getResult);
                        cameraUtil.cancelTask();
                        button_record.setEnabled(false);

                        saveUtil.closeOutputStream();

                        textview1.setText("");
                        textview2.setText("");
                        textview3.setText("");
                        textview4.setText("");

                    }
                    break;
                case R.id.record:
                    if (isChecked) {
                        handler.postDelayed(saveResult, 1000);
                    } else {
                        handler.removeCallbacks(saveResult);
                    }
                    break;
                case R.id.turnOnOff:
                    if (isChecked) {
                        cameraUtil.lightOn();
                    } else {
                        cameraUtil.lightOff();
                    }
                    break;

                default:
                    break;
            }
        }
    };

   /* public void selectMode() {
        String[] modeType = {"Single", "Double", "RGB + I Four"};
        new AlertDialog.Builder(this)
                .setTitle("Mode")
                .setSingleChoiceItems(modeType, -1,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        mode = 1;
                                        System.out.println("mode" + mode);
                                        break;
                                    case 1:
                                        mode = 2;
                                        System.out.println("mode" + mode);
                                        break;
                                    case 2:
                                        mode = 4;
                                        System.out.println("mode" + mode);
                                        break;

                                    default:
                                        break;
                                }
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        button_start.setEnabled(false);
                    }
                })
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //根据mode初始化series
                        chartUtil.addSeries(mode);
                        getChartViewAndSeries(chartUtil, mode);

                        //初始化存储工具
                        saveUtil = new SaveUtil();
                    }
                }).show();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detection);

        findView();
        addListener();
        handler = new Handler();
        context = this;
        //Call the cameraUtil method to open the camera and display it on the interface
        cameraUtil = new CameraUtil(mPreviewSV, context);

        //Initialize the chart area
        chartUtil = new ChartUtil();

        //The screen is always on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //selectMode();
        chartUtil.initChartView(layout, context);
        chartUtil.addSeries(mode);
        getChartViewAndSeries(chartUtil, mode);
        saveUtil = new SaveUtil();
    }

    private void findView() {
        button_start = (Switch) findViewById(R.id.start);

        button_record = (Switch) findViewById(R.id.record);
        button_record.setEnabled(false);

        mPreviewSV = (SurfaceView) findViewById(R.id.previewSV);
        button_OnOff = (Switch) findViewById(R.id.turnOnOff);

        layout = (LinearLayout) findViewById(R.id.chart);

        textview1 = (TextView) findViewById(R.id.textView1);
        textview2 = (TextView) findViewById(R.id.textView2);
        textview3 = (TextView) findViewById(R.id.textView3);
        textview4 = (TextView) findViewById(R.id.textView4);

    }

    @SuppressLint("NewApi")
    private void addListener() {
        button_record.setOnCheckedChangeListener(toggleButtonListener);

        button_OnOff.setOnCheckedChangeListener(toggleButtonListener);
        button_start.setOnCheckedChangeListener(toggleButtonListener);

    }

    public void getChartViewAndSeries(ChartUtil cu, int mode) {

        mChartView = cu.getmChartView();

        switch (mode) {
            case 1:
                series1 = cu.getSeries2();
                break;
            case 2:
            /*series1 = cu.getSeries1();
            series2 = cu.getSeries2();*/
                series1 = cu.getSeries2();
                series2 = cu.getSeries3();
                break;
            case 4:
                series1 = cu.getSeries1();
                series2 = cu.getSeries2();
                series3 = cu.getSeries3();
                series4 = cu.getSeries4();
                break;

            default:
                break;
        }

    }

    public void doJob(int mode) {
        System.out.println("dojob");
        cameraUtil.doTask(mode);

        handler.postDelayed(getResult, 1000);
    }

    //New detection
    private void newDetection() {
        new AlertDialog.Builder(this)
                .setTitle("Initialize the chart")
                .setMessage("Will clear the chart and start recording data again")
                .setNegativeButton("Do not clear",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        chartUtil.initSeries();
                        mChartView.repaint();
                        t = 0;
                        saveUtil.closeOutputStream();
                        file = null;
                    }
                }).show();

    }

    public void saveToSDcard(int time, double[] resultData) {

        if (file == null) {
            file = saveUtil.createFile();
        }
        saveUtil.saveToSDcard(time, resultData, file);

    }

    public void chartDisplay(int time, double[] resultData, int mode) {

        switch (mode) {
            case 1:
                series1.add(time, resultData[0]);
                break;
            case 2:
                series1.add(time, resultData[0]);
                series2.add(time, resultData[1]);
                break;
            case 4:
                series1.add(time, resultData[0]);
                series2.add(time, resultData[1]);
                series3.add(time, resultData[2]);
                series4.add(time, resultData[3]);
                break;
            default:
                break;
        }

        mChartView.repaint();

    }

    public void textViewDisplay(int mode) {

        switch (mode) {
            case 1:
                textview2.setText(String.valueOf(result[0]));
                break;
            case 2:
                textview2.setText(String.valueOf(result[0]));
                textview3.setText(String.valueOf(result[1]));
                break;
            case 4:
                textview1.setText(String.valueOf(result[0]));
                textview2.setText(String.valueOf(result[1]));
                textview3.setText(String.valueOf(result[2]));
                textview4.setText(String.valueOf(result[3]));
                break;
            default:
                break;
        }

    }

    //Prevent crashes when other programs interfere
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        newFileFlag = true;
        chartUtil.initChartView(layout, context);
    }


    @Override
    protected void onPause() {
        System.out.println("onPause");
        super.onPause();
        doBeforeLeave();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "Press again to exit the program", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();

            } else {
                //If you do not close the stream at this time, data loss will result.
                doBeforeLeave();
                cameraUtil.releaseCamera();
                ImageSPR.this.finish();
                System.exit(0);// Used to free up memory
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void doBeforeLeave() {
        System.out.println("doBeforeLeave");
        newFileFlag = false;
        if (button_start.isChecked())
            button_start.setChecked(false);

    }


}
