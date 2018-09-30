package com.example.herem1t.rc_client.Charts;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Database.Server;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RamUsagesActivity extends AppCompatActivity {

//    private LineChart lineChart;

    private LineChart mChart;
    List<Server> servers_ram_usages;
    long start_timestamp_sec;
    float start_view_point = 0;
    final float RANGE = 12*60*60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ram_usages);

        String ext_ip = getIntent().getStringExtra("ext_ip");

        //DBOperations.setData(RamUsagesActivity.this);
        servers_ram_usages = DBOperations.getRamUsages(RamUsagesActivity.this, ext_ip);
        if (servers_ram_usages.size() <= 2) {
            Toast.makeText(getApplicationContext(), "No data to display", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Server first_server = servers_ram_usages.get(0);
        String first_timestamp = first_server.getTimestamp();
        start_timestamp_sec = stringToDateSec(first_timestamp);

        mChart = (LineChart) findViewById(R.id.linechart0);
        mChart.setViewPortOffsets(70, 20, 0, 150);
        mChart.setBackgroundColor(Color.WHITE);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        mChart.setMaxHighlightDistance(300);


        XAxis x = mChart.getXAxis();
        //x.setEnabled(false);
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setValueFormatter(new MyDateValueFormatter(start_timestamp_sec));
        x.setLabelRotationAngle(270);


        YAxis y = mChart.getAxisLeft();
        //y.setTypeface(mTfLight);
        y.setLabelCount(12, false);
        //y.setTextColor(Color.rgb(104, 241, 175));  //
        y.setTextColor(Color.rgb(62, 17, 109));
        //y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.rgb(104, 241, 175));

        y.setValueFormatter(new MyGbValueFormatter());
        y.setAxisMaximum(100);

        mChart.getAxisRight().setEnabled(false);

        // add data
        setData();

        mChart.getLegend().setEnabled(false);

        mChart.animateXY(2000, 2000);

        // move to x value (date)
        mChart.moveViewToX(start_view_point);
        // visible range  = 11700 + 600
        mChart.setVisibleXRangeMaximum(RANGE);

        // dont forget to refresh the drawing
        mChart.invalidate();

    }

    public static class MyGbValueFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return (int)value + " %";
        }

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

    private void setData() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        // TODO use it later

        float delta_time = 0;
        int servers_amount = servers_ram_usages.size();
        for(int i=0; i<servers_amount; i++){
            Server server = servers_ram_usages.get(i);
            String temp_timestamp = server.getTimestamp();
            long sec = stringToDateSec(temp_timestamp);
            delta_time = sec - start_timestamp_sec;
            yVals.add(new Entry(delta_time, Float.valueOf(server.getRamUsages())));
            if (i == servers_amount-144) start_view_point = delta_time;
            Log.d("ramusage", delta_time + " " + Float.valueOf(server.getRamUsages()) + " " + start_timestamp_sec + " " + MsToDate(1526550000000L));
        }


        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");

            set1.setDrawVerticalHighlightIndicator(true);
            set1.setDrawHorizontalHighlightIndicator(true);

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.rgb(104, 241, 175));
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.rgb(104, 241, 175));
            set1.setFillColor(Color.rgb(104, 241, 175));
            set1.setFillAlpha(100);
            //set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set1);
            //data.setValueTypeface();
            data.setValueTextSize(9f);
            data.setDrawValues(false);


            // set data
            mChart.setData(data);
        }
    }


}
