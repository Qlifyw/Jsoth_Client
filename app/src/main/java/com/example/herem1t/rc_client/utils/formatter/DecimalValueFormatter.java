package com.example.herem1t.rc_client.utils.formatter;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

public class DecimalValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;

    public DecimalValueFormatter() {
        mFormat = new DecimalFormat("######.0");
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormat.format(value);
    }
}
