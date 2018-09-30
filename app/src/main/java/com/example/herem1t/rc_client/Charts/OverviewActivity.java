package com.example.herem1t.rc_client.Charts;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Map;

public class OverviewActivity extends AppCompatActivity{

    private PieChart mChart;
    private TextView tvX, tvY;

    private ImageView iv_available;
    private ImageView iv_not_response;
    private ImageView iv_down;

    private TextView tv_available;
    private TextView tv_not_response;
    private TextView tv_down;
    private TextView tv_count_available;
    private TextView tv_count_not_response;
    private TextView tv_count_down;

    int available;
    int not_responding;
    int down;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            w.setStatusBarColor(Color.CYAN);
        }

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_overview);

        iv_available = (ImageView) findViewById(R.id.iv_available);
        iv_not_response = (ImageView) findViewById(R.id.iv_not_response);
        iv_down = (ImageView) findViewById(R.id.iv_down);

        tv_available = (TextView) findViewById(R.id.tv_available);
        tv_not_response = (TextView) findViewById(R.id.tv_not_response);
        tv_down = (TextView) findViewById(R.id.tv_down);
        tv_count_available = (TextView) findViewById(R.id.tv_count_available);
        tv_count_not_response = (TextView) findViewById(R.id.tv_count_not_response);
        tv_count_down = (TextView) findViewById(R.id.tv_count_down);

        GradientDrawable av_bg = (GradientDrawable) iv_available.getBackground();
        av_bg.setStroke(4, getResources().getColor(R.color.green));
        GradientDrawable nr_bg = (GradientDrawable) iv_not_response.getBackground();
        nr_bg.setStroke(4, getResources().getColor(R.color.yellow));
        GradientDrawable d_bg = (GradientDrawable) iv_down.getBackground();
        d_bg.setStroke(4, getResources().getColor(R.color.red));

        // TODO do it later
        Map<String, Integer> info = DBOperations.getServersStatusValue(OverviewActivity.this);
        available = info.get("available");
        not_responding = info.get("not_responding");
        down = info.get("down");

        tv_count_available.setText(String.valueOf(available));
        tv_count_not_response.setText(String.valueOf(not_responding));
        tv_count_down.setText(String.valueOf(down));


        //  TODO delete it in prod
        //DBOperations.setData(OverviewActivity.this);


        mChart = (PieChart) findViewById(R.id.piechart);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterText(generateCenterSpannableText());

        mChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        setData();

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }


    private void setData() {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries.add(new PieEntry((float) available, ""));
        entries.add(new PieEntry((float) not_responding, ""));
        entries.add(new PieEntry((float) down, ""));

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        //dataSet.setUsingSliceColorAsValueLineColor(true);

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        //data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Total amount\n"+(available+ not_responding +down));
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 12, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 12, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 12, 0);

        s.setSpan(new RelativeSizeSpan(2.0f), 12, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 12, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 12, s.length(), 0);
        return s;
    }

}
