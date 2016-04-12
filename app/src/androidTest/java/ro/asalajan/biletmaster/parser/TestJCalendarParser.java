package ro.asalajan.biletmaster.parser;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import ro.asalajan.biletmaster.model.Event;

@RunWith(AndroidJUnit4.class)
public class TestJCalendarParser {

    BiletMasterParserImpl parser = new BiletMasterParserImpl();


    @Test
    public void emptyInputStreamReturnsEmptyList() throws IOException, XmlPullParserException {
        InputStream inputStream = readResource("empty.html");
        List<Event> events = parser.parse(inputStream);

        Assert.assertEquals(Collections.emptyList(), events);
    }

    @Test
    public void parserReturnsEventsForEveryStacktitle() throws IOException, XmlPullParserException, URISyntaxException {
        InputStream inputStream = readResource("calendar.html");
        List<Event> events = parser.parse(inputStream);

        Assert.assertEquals(10, events.size());
    }

    private InputStream readResource(String res) {
        return this.getClass().getClassLoader().getResourceAsStream(res);
    }
}
