package com.example.herem1t.rc_client.ui.screens.ram;

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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;

public class RamUsagesActivity extends AppCompatActivity implements RamUsagesMvpView {

    private LineChart chart;
    long startTimestampSec;
    float startViewPoint = 0;
    final float RANGE = 12*60*60;

    private RamUsagesMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ram_usages);

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,null, null, null);
        presenter = new RamUsagesPresenter(dataManager,this);

        String extIp = getIntent().getStringExtra(INTENT_EXTRAS_EXTERNAL_IP);

        chart = findViewById(R.id.linechart0);
        chart.setViewPortOffsets(70, 20, 0, 150);
        chart.setBackgroundColor(Color.WHITE);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);

        presenter.getRamStats(extIp);

        XAxis x = chart.getXAxis();
        x.setEnabled(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setValueFormatter(new DateValueFormatter(startTimestampSec));
        x.setLabelRotationAngle(270);


        YAxis y = chart.getAxisLeft();
        y.setLabelCount(12, false);
        y.setTextColor(Color.rgb(62, 17, 109));
        y.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.rgb(104, 241, 175));

        y.setValueFormatter(new PercentValueFormatter());
        y.setAxisMaximum(100);
        y.setAxisMinimum(0);

        chart.getAxisRight().setEnabled(false);

        chart.getLegend().setEnabled(false);

        chart.animateXY(2000, 2000);

        // move to x value (date)
        chart.moveViewToX(startViewPoint);
        // visible range  = 11700 + 600
        chart.setVisibleXRangeMaximum(RANGE);

        // dont forget to refresh the drawing
        chart.invalidate();

    }

    @Override
    public void showEmptyData() {
        Toast.makeText(getApplicationContext(), getString(R.string.chart_no_data), Toast.LENGTH_SHORT).show();
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
    public void loadDataset(List<Entry>... yVals) {
        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(yVals[0]);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals[0], "DataSet 1");

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
            set1.setFillFormatter((dataSet, dataProvider) -> -10);

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);


            // set data
            chart.setData(data);
        }
    }




}
