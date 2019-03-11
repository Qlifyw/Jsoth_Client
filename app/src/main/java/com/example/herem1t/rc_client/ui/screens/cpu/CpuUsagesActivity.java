package com.example.herem1t.rc_client.ui.screens.cpu;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.utils.formatter.DateValueFormatter;
import com.example.herem1t.rc_client.utils.formatter.PercentValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.List;

import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;


public class CpuUsagesActivity extends AppCompatActivity implements CpuUsagesMvpView {

    private LineChart chart;

    float startViewPoint = 0;
    final float RANGE = 12*60*60;
    private long startTimestampSec;

    private CpuUsagesMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_usages);

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,null, null, null );
        presenter = new CpuUsagesPresenter(dataManager,this);

        String extIp = getIntent().getStringExtra(INTENT_EXTRAS_EXTERNAL_IP);

        chart = findViewById(R.id.linechart2);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);
        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);
        chart.setViewPortOffsets(70, 20, 0, 150);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);

        // add data
        presenter.getCpuStats(extIp);

        chart.animateX(2500);

        // move to x value (date)
        chart.moveViewToX(startViewPoint);
        // visible range  = 11700 + 600
        chart.setVisibleXRangeMaximum(RANGE);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextSize(11f);
        l.setTextColor(Color.BLACK);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setValueFormatter(new DateValueFormatter(startTimestampSec));
        xAxis.setLabelRotationAngle(270);


        YAxis y = chart.getAxisLeft();
        y.setLabelCount(12, false);
        y.setTextColor(Color.rgb(62, 17, 109));
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.rgb(104, 241, 175));

        y.setValueFormatter(new PercentValueFormatter());
        y.setAxisMaximum(100);
        y.setAxisMinimum(0);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public void showEmptyData() {
        Toast.makeText(getApplicationContext(), R.string.chart_no_data, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void setStartTimestamp(long startTimestamp) {
        startTimestampSec = startTimestamp;
    }

    @Override
    public void setStartViewPoint(float startViewPoint) {
        this.startViewPoint = startViewPoint;
    }

    @Override
    public void loadDataset(List<Entry> ... yVals) {
        LineDataSet set1, set3;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set3 = (LineDataSet) chart.getData().getDataSetByIndex(2);
            set1.setValues(yVals[0]);
            set3.setValues(yVals[1]);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals[0], "System");

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

            set3 = new LineDataSet(yVals[0], "User");
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
            chart.setData(data);
        }
    }




}
