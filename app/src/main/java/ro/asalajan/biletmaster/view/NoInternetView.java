package ro.asalajan.biletmaster.view;

import rx.Observable;

public interface NoInternetView extends View {

    Observable<Object> retry();

}
