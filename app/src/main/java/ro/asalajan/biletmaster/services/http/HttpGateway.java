package ro.asalajan.biletmaster.services.http;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;

public class HttpGateway {

    private OkHttpClient httpClient = new OkHttpClient();

    public Observable<InputStream> downloadWebPage(String url) {
        return download(url);
    }

    private Observable<InputStream> download(String url) {
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        final Call call = httpClient.newCall(request);
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
}
