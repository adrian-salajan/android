package ro.asalajan.biletmaster.parser;

import org.joda.time.LocalDateTime;

public class DateUtils {

    public static int getCurrentYear() {
        return LocalDateTime.now().getYear();
    }
}
