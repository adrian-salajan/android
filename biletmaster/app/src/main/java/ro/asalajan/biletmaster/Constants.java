package ro.asalajan.biletmaster;

import java.text.SimpleDateFormat;

public class Constants {

    public static final String DATE_FORMAT = "yyyyMMdd"; // 20151230

    public static final ThreadLocal<SimpleDateFormat> DATE_FORMATER = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_FORMAT);
        }
    };
}
