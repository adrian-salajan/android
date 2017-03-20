package ro.asalajan.biletmaster.presenters;

import rx.Observable;

public interface Environment {

    public Observable<Object> enviromentChanged();

    public Observable<Boolean> isOnline();
}
