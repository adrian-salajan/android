package ro.asalajan.biletmaster.services;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import ro.asalajan.biletmaster.model.Location;
import ro.asalajan.biletmaster.parser.EventsParser;
import ro.asalajan.biletmaster.parser.EventsParserImpl;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class BiletMasterService {

    private static final String LOCATIONS_URL = "http://biletmaster.ro/ron/AllPlaces/Minden_helyszin";

    private static EventsParserImpl parser = new EventsParserImpl();

    public Observable<List<Location>> getLocations() {
        return downloadLocations().map(parseLocations(parser));
    }


    private Observable<InputStream> downloadLocations() {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url(LOCATIONS_URL)
                .build();

        final Call call = client.newCall(request);

        return Observable.create(new Observable.OnSubscribe<InputStream>() {
                                     @Override
                                     public void call(final Subscriber<? super InputStream> subscriber) {
                                         call.enqueue(new Callback() {
                                             @Override
                                             public void onFailure(Call call, IOException e) {
                                                subscriber.onError(e);
                                             }

                                             @Override
                                             public void onResponse(Call call, Response response) throws IOException {
                                                subscriber.onNext(response.body().byteStream());
                                             }
                                         });
                                     }
                                 }
        );
    }




    public static Func1<? super InputStream, List<Location>> parseLocations(final EventsParser parser) {
        return new Func1<InputStream, List<Location>>() {
            @Override
            public List<Location> call(InputStream inputStream) {
                Log.e("parsing", "parsing.....");
                return parser.parseLocations(inputStream);
            }
        };
    }

}
