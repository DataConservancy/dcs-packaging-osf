package org.dataconservancy.cos.osf.client.support;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

/**
 * Created by esm on 6/3/16.
 */
public class DateTimeTransform implements Function<DateTime, Calendar> {
    @Override
    public Calendar apply(DateTime dateTime) {
        return dateTime.toGregorianCalendar();
    }
}
