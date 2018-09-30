package com.example.herem1t.rc_client.Charts;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Database.Server;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CpuUsagesActivity extends AppCompatActivity {

    private LineChart mChart;

    List<Server> servers_cpu_usages;
    long start_timestamp_sec;

    float start_view_point = 0;
    final float RANGE = 12*60*60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cpu_usages);

        String ext_ip = getIntent().getStringExtra("ext_ip");

        servers_cpu_usages = DBOperations.getCPUUsages(CpuUsagesActivity.this, ext_ip);
        if (servers_cpu_usages.size() <= 2) {
            Log.d("qweqweqwe", servers_cpu_usages.size() + "");
            Toast.makeText(getApplicationContext(), "No data to display", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Server first_stat_server = servers_cpu_usages.get(0);
        String first_timestamp = first_stat_server.getTimestamp();
        start_timestamp_sec = stringToDateSec(first_timestamp);


        mChart = (LineChart) findViewById(R.id.linechart2);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);
        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);
        mChart.setViewPortOffsets(70, 20, 0, 150);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        // add data
        setData(20, 30);

        mChart.animateX(2500);

        // move to x value (date)
        mChart.moveViewToX(start_view_point);
        // visible range  = 11700 + 600
        mChart.setVisibleXRangeMaximum(RANGE);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        //l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
        //xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new MyDateValueFormatter(start_timestamp_sec));
        xAxis.setLabelRotationAngle(270);


        YAxis y = mChart.getAxisLeft();
        //y.setTypeface(mTfLight);
        y.setLabelCount(12, false);
        //y.setTextColor(Color.rgb(104, 241, 175));  //
        y.setTextColor(Color.rgb(62, 17, 109));
        //y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.rgb(104, 241, 175));

        y.setValueFormatter(new MyPercentValueFormatter());
        y.setAxisMaximum(100);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private class MyDateValueFormatter implements IAxisValueFormatter {
        private long start_timestamp;

        public MyDateValueFormatter(long start_point) {
            this.start_timestamp = start_point;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return MsToDate(start_timestamp*1000 + (long)value*1000);
        }
    }

    private static class MyPercentValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return (int)value + " %";
        }

    }

    public String MsToDate(long ms) {
        String dateFormat = "yy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        Date date = new Date(ms);
        return simpleDateFormat.format(date);
    }

    private long stringToDateSec(String date){
        long date_sec = 0;
        SimpleDateFormat simpleDF = new SimpleDateFormat("yy-MM-dd HH:mm");
        try {
            Date mDate = simpleDF.parse(date);
            date_sec = (long)mDate.getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_sec;
    }


    private void setData(int count, float range) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<Entry> yVals3 = new ArrayList<Entry>();

        // TODO
        float delta_time = 0;
        int servers_amount = servers_cpu_usages.size();
        for (int i=0; i<servers_amount; i++) {
            Server server = servers_cpu_usages.get(i);

            String temp_timestamp = server.getTimestamp();
            long sec = stringToDateSec(temp_timestamp);
            delta_time = sec - start_timestamp_sec;

            String[] usages = server.getCpuUsages().split("/");
            float system = Float.valueOf(usages[0]);
            float user = Float.valueOf(usages[1]);
            yVals1.add(new Entry(delta_time, system));
            yVals3.add(new Entry(delta_time, user));
            if (i == servers_amount-144) start_view_point = delta_time;
        }

        LineDataSet set1, set3;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
            set3.setValues(yVals3);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "System");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.MAGENTA);
            set1.setCircleColor(Color.MAGENTA);
            set1.setDrawFilled(true);
            set1.setDrawValues(false);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(65);
            set1.setFillColor(Color.MAGENTA);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(true);
            set1.setDrawCircles(false);

            set3 = new LineDataSet(yVals3, "User");
            set3.setAxisDependency(YAxis.AxisDependency.LEFT);
            set3.setColor(ColorTemplate.getHoloBlue());
            set3.setCircleColor(ColorTemplate.getHoloBlue());
            set3.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set3.setDrawFilled(true);
            set3.setDrawValues(false);
            set3.setLineWidth(2f);
            set3.setCircleRadius(3f);
            set3.setFillAlpha(65);
            set3.setFillColor(ColorTemplate.getHoloBlue());
            set3.setDrawCircleHole(true);
            set3.setDrawCircles(false);
            set3.setHighLightColor(Color.rgb(244, 117, 117));

            // create a data object with the datasets
            LineData data = new LineData(set1, set3);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);

            // set data
            mChart.setData(data);
        }
    }

}
