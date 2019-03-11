package com.example.herem1t.rc_client.utils;

import org.junit.Test;


import java.text.ParseException;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class DateTimeUtilsTest {

    @Test
    public void testStringtoDate(){
        assertSame(DateTimeUtils.stringToDateSec("unformated string"), 0L);
        assertThat(DateTimeUtils.stringToDateSec("99-02-01 16:41"), not(0L));
    }

}
