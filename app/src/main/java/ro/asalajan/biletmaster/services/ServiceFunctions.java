package ro.asalajan.biletmaster.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;
import ro.asalajan.biletmaster.parser.EventsParser;
import ro.asalajan.biletmaster.parser.EventsParserImpl;
import rx.functions.Func1;

public class ServiceFunctions {

    @NonNull
    public static Func1<Integer, Date> deltaToDate(final int startingYear, final int startingMonth, final int startingDay) {
        return new Func1<Integer, Date>() {
            @Override
            public Date call(Integer dayDelta) {
                Calendar calendar = new GregorianCalendar(startingYear, startingMonth, startingDay);
                calendar.setLenient(true);
                calendar.add(Calendar.DATE, dayDelta);
                return calendar.getTime();
            }
        };
    }

    public static Func1<? super Date, ? extends String> dayToString(final DateFormat formater) {
        return new Func1<Date, String>() {
            @Override
            public String call(Date date) {
                return formater.format(date);
            }
        };
    }

    @NonNull
    public static Func1<String, InputStream> dateToWebPage() {
        final String urlString = "http://biletmaster.ro/ron/Timetable/Idorend";
        return new Func1<String, InputStream>() {

            @Override
            public InputStream call(String date) {
                HttpURLConnection conn;
                InputStream conn1 = downloadWebPage(date, urlString);
                if (conn1 != null) return conn1;
                Log.e("http", "input stream is null!");
                return null;
            }
        };
    }

    @Nullable
    private static InputStream downloadWebPage(String date, String urlString) {
        HttpURLConnection conn;
        try {
            Log.d("http", "building conn...");
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            Log.e("http", "opened conn...");
            conn.setReadTimeout(5_000 /* milliseconds */);
            conn.setConnectTimeout(5_000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            // Starts the query
            Log.d("http", "getting output stream");
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));

            StringBuilder result = new StringBuilder();
            result.append(URLEncoder.encode("day", "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(date, "UTF-8"));
            writer.write(result.toString());
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            return conn.getInputStream();
        } catch (Exception e) {
            Log.e("http", e.toString());
        }
        return null;
    }

    private static EventsParserImpl parser = new EventsParserImpl();

    public static Func1<? super InputStream, List<Event>> parseEvents(final EventsParser parser) {
        return new Func1<InputStream, List<Event>>() {
            @Override
            public List<Event> call(InputStream inputStream) {
                Log.e("parsing", "parsing.....");
                return parser.parseEvents(inputStream);
            }
        };
    }
}
