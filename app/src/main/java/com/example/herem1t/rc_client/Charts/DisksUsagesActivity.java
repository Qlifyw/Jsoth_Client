package com.example.herem1t.rc_client.Charts;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Database.Server;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisksUsagesActivity extends AppCompatActivity {

    protected BarChart mChart;
    List<Server> servers_disks_usages;
    long start_timestamp_sec;
    float start_view_point = 0;
    final float RANGE = 12*60*60;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_disks_usages);

        String ext_ip = getIntent().getStringExtra("ext_ip");

        //DBOperations.setData(DisksUsagesActivity.this);
        servers_disks_usages = DBOperations.getDiskUsages(DisksUsagesActivity.this, ext_ip);
        if (servers_disks_usages.size() <= 2) {
            Toast.makeText(getApplicationContext(), "No data to display", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Server first_server = servers_disks_usages.get(0);
        String first_timestamp = first_server.getTimestamp();
        start_timestamp_sec = stringToDateSec(first_timestamp);

        mChart = (BarChart) findViewById(R.id.barchart);
        mChart.setBackgroundColor(Color.WHITE);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(5);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(270);
        xAxis.setValueFormatter(new MyDateValueFormatter(start_timestamp_sec));

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(true);
        //left.setSpaceTop(25f);
        //left.setSpaceBottom(25f);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
        left.setValueFormatter(new MyGBValueFormatter());
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);


        List<Entry> data = new ArrayList<>();
        float previous = Float.valueOf(first_server.getDisksUsages().split(" ")[0].split(";")[1]);
        data.add(new Entry(0,0));
        float delta_time = 0;
        int servers_amount = servers_disks_usages.size();
        for (int i=1; i<servers_amount; i++) {
            Server server = servers_disks_usages.get(i);

            String temp_timestamp = server.getTimestamp();
            long sec = stringToDateSec(temp_timestamp);
            delta_time = sec - start_timestamp_sec;

            String[] usages = server.getDisksUsages().split(" ");
            String[] disk1 = usages[0].split(";");
            data.add(new Entry(delta_time, Float.valueOf(disk1[1])-previous));
            Log.d("ramusages", delta_time +"");
            if (i == servers_amount-144) start_view_point = delta_time;
            if (i == servers_amount-144) Log.d("ramusages", start_view_point +" dsfdsfsdf");
        }

        setData(data);

        mChart.moveViewToX(start_view_point);
        mChart.setVisibleXRangeMaximum(RANGE);
    }

    public static class MyGBValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return (int)value + " Gb";
        }
    }

    public class MyDateValueFormatter implements IAxisValueFormatter {
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

    private void setData(List<Entry> dataList) {

        ArrayList<BarEntry> values = new ArrayList<BarEntry>();
        List<Integer> colors = new ArrayList<Integer>();

        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);

        for (int i = 0; i < dataList.size(); i++) {
            BarEntry entry = new BarEntry(dataList.get(i).getX(), dataList.get(i).getY());
            values.add(entry);

            // specific colors
            if (dataList.get(i).getY() >= 0)
                colors.add(red);
            else
                colors.add(green);
        }

        BarDataSet set;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet)mChart.getData().getDataSetByIndex(0);
            set.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueFormatter((IValueFormatter) new ValueFormatter());
            data.setBarWidth(200f);

            mChart.setData(data);
            mChart.invalidate();
        }
    }


    private class ValueFormatter implements IValueFormatter
    {

        private DecimalFormat mFormat;

        public ValueFormatter() {
            mFormat = new DecimalFormat("######.0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }
    }
}
