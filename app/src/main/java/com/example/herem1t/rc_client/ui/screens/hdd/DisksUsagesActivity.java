package com.example.herem1t.rc_client.ui.screens.hdd;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.utils.formatter.DateValueFormatter;
import com.example.herem1t.rc_client.utils.formatter.DecimalValueFormatter;
import com.example.herem1t.rc_client.utils.formatter.GbValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;

import java.util.ArrayList;
import java.util.List;

import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;

public class DisksUsagesActivity extends AppCompatActivity implements DisksUsagesMvpView {

    protected BarChart chart;
    long startTimestampSec;
    float startViewPoint = 0;
    final float RANGE = 12*60*60;

    private static final String EXTRAS_ITEM_ORDER = "item";
    private int item;

    private DisksUsagesMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disks_usages);

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,null, null, null);
        presenter = new DisksUsagesPresenter(dataManager,this);

        String extIp = getIntent().getStringExtra(INTENT_EXTRAS_EXTERNAL_IP);
        item = getIntent().getIntExtra(EXTRAS_ITEM_ORDER, 0);

        chart = findViewById(R.id.barchart);
        chart.setBackgroundColor(Color.WHITE);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        chart.animateX(2500);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(11f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(270);
        xAxis.setValueFormatter(new DateValueFormatter(startTimestampSec));

        YAxis left = chart.getAxisLeft();
        left.setDrawLabels(true);
        left.setLabelCount(10, false);
        left.setDrawAxisLine(false);
        left.setDrawGridLines(false);
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
        left.setValueFormatter(new GbValueFormatter());
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);


        presenter.getHddStats(extIp, item);

        chart.moveViewToX(startViewPoint);
        chart.setVisibleXRangeMaximum(RANGE);
    }

    @Override
    public void showEmptyData() {
        Toast.makeText(getApplicationContext(), getString(R.string.chart_no_data), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void setStartTimestamp(long startTimestamp) {

    }

    @Override
    public void setStartViewPoint(float startViewPoint) {

    }

    @Override
    public void loadDataset(List<Entry> ... dataList) {

        ArrayList<BarEntry> values = new ArrayList<BarEntry>();
        List<Integer> colors = new ArrayList<Integer>();

        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);

        for (int i = 0; i < dataList[0].size(); i++) {
            BarEntry entry = new BarEntry(dataList[0].get(i).getX(), dataList[0].get(i).getY());
            values.add(entry);

            // specific colors
            if (dataList[0].get(i).getY() >= 0)
                colors.add(green);
            else
                colors.add(red);
        }

        BarDataSet set;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueFormatter((IValueFormatter) new DecimalValueFormatter());
            data.setBarWidth(200f);

            chart.setData(data);
            chart.invalidate();
        }
    }


}
