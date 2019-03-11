package com.example.herem1t.rc_client.utils.formatter;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import static com.example.herem1t.rc_client.utils.DateTimeUtils.MsToDate;

public class DateValueFormatter implements IAxisValueFormatter {

    private long start_timestamp;

    public DateValueFormatter(long start_point) {
        this.start_timestamp = start_point;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return MsToDate(start_timestamp*1000 + (long)value*1000);
    }
}
