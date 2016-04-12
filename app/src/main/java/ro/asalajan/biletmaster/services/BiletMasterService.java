package ro.asalajan.biletmaster.services;

import android.util.Log;

import java.io.InputStream;
import java.util.List;

import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.parser.BiletMasterParser;
import rx.Observable;
import rx.functions.Func1;

public class BiletMasterService {

    private static final String LOCATIONS_URL = "http://biletmaster.ro/ron/AllPlaces/Minden_helyszin";

    private final BiletMasterParser parser;
    private final HttpGateway httpGateway;

    public BiletMasterService(BiletMasterParser parser, HttpGateway httpGateway) {
        this.parser = parser;
        this.httpGateway = httpGateway;
    }

    public Observable<List<Location>> getLocations() {
        return httpGateway.downloadWebPage(LOCATIONS_URL).map(parseLocations);
    }

    private Func1<? super InputStream, List<Location>> parseLocations =
        new Func1<InputStream, List<Location>>() {
            @Override
            public List<Location> call(InputStream inputStream) {
                return parser.parseLocations(inputStream);
            }
        };
}
