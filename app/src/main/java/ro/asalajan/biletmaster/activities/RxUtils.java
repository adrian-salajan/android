package ro.asalajan.biletmaster.activities;

import java.util.concurrent.Executors;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//@SuppressWarnings("unchecked")
public class RxUtils {

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T,T> io() {
        return (Observable.Transformer<T,T>) ioThread;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T,T> computation() {
        return (Observable.Transformer<T,T>) computationThread;
    }

    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T,T> ui() {
        return (Observable.Transformer<T,T>) uiThread;
    }

    private static final Observable.Transformer ioThread = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ( (Observable) observable).observeOn(Schedulers.io());
        }
    };

    private static final Observable.Transformer uiThread = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ( (Observable) observable).observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final Observable.Transformer computationThread = new Observable.Transformer() {
        @Override
        public Object call(Object observable) {
            return ( (Observable) observable).observeOn(Schedulers.computation());
        }
    };

    public static Scheduler customScheduler = Schedulers.from(Executors.newFixedThreadPool(2));

}
