package com.example.herem1t.rc_client.ui.screens.overview;

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

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class OverviewActivity extends AppCompatActivity implements OverviewMvpView {

    private PieChart mChart;

    private ImageView iv_available;
    private ImageView iv_not_response;
    private ImageView iv_down;

    private TextView tv_count_available;
    private TextView tv_count_not_response;
    private TextView tv_count_down;

    int available;
    int notResponding;
    int down;

    private OverviewMvpPresenter presenter;

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

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,null, null, null);
        presenter = new OverviewPresenter(dataManager,this);

        iv_available = findViewById(R.id.iv_available);
        iv_not_response = findViewById(R.id.iv_not_response);
        iv_down = findViewById(R.id.iv_down);

        tv_count_available = findViewById(R.id.tv_count_available);
        tv_count_not_response = findViewById(R.id.tv_count_not_response);
        tv_count_down = findViewById(R.id.tv_count_down);

        GradientDrawable availableBg = (GradientDrawable) iv_available.getBackground();
        availableBg.setStroke(4, getResources().getColor(R.color.green));
        GradientDrawable notResponseBg = (GradientDrawable) iv_not_response.getBackground();
        notResponseBg.setStroke(4, getResources().getColor(R.color.yellow));
        GradientDrawable downBg = (GradientDrawable) iv_down.getBackground();
        downBg.setStroke(4, getResources().getColor(R.color.red));


        mChart = (PieChart) findViewById(R.id.piechart);
        presenter.getServersStatus();

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

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);


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
        entries.add(new PieEntry((float) notResponding, ""));
        entries.add(new PieEntry((float) down, ""));

        PieDataSet dataSet = new PieDataSet(entries, "Servers");
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

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString(getString(R.string.chart_pie_center_text) +"\n"+(available + notResponding + down));
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 12, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 12, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 12, 0);

        s.setSpan(new RelativeSizeSpan(2.0f), 12, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 12, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), 12, s.length(), 0);
        return s;
    }

    @Override
    public void setServerStatusValue(int available, int notResponse, int down) {
        this.available = available;
        this.notResponding = notResponse;
        this.down = down;

        tv_count_available.setText(String.valueOf(available));
        tv_count_not_response.setText(String.valueOf(notResponse));
        tv_count_down.setText(String.valueOf(down));

        setData();
    }
}
