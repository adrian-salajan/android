package ro.asalajan.biletmaster;

import junit.framework.Assert;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestConstants {

    @Test
    public void dateFormater() {
        SimpleDateFormat formater = Constants.DATE_FORMATER.get();

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.DATE, 25);
        cal.set(Calendar.MONTH, Calendar.JUNE);
        cal.set(Calendar.YEAR, 2016);

        Assert.assertEquals("20160625", formater.format(cal.getTime()));
    }

}
