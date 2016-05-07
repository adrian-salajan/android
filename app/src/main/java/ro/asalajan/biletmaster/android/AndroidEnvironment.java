package ro.asalajan.biletmaster.android;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import ro.asalajan.biletmaster.presenters.Environment;
import rx.Observable;

public class AndroidEnvironment implements Environment {

    private static final Object object = new Object();
    ConnectivityManager connectivityManager;

    public AndroidEnvironment(Context context) {
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    @Override
    public Observable<Object> enviromentChanged() {
        return isOnline().map(b -> object);
    }

    @Override
    public Observable<Boolean> isOnline() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        Log.d(name, "is online: " + (networkInfo != null && networkInfo.isConnected()));
        return Observable.just(networkInfo != null && networkInfo.isConnected());
    }
}
