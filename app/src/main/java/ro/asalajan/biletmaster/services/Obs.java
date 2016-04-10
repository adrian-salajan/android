package ro.asalajan.biletmaster.services;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func2;

public class Obs {

    public static <T> Observable<Integer> obsToSequence(final Observable<T> anyObservable) {
      return sequence().zipWith(anyObservable, new Func2<Integer, T, Integer>() {
          @Override
          public Integer call(Integer integer, T t) {
              return integer;
          }
      });
    }

    @NonNull
    public static Observable<Integer> sequence() {
        return Observable.from(new Iterable<Integer>() {
            final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public Integer next() {
                        return counter.getAndIncrement();
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        });
    }

    @NonNull
    public static Observable<Object> Observable(final TextView text) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(final Subscriber<? super Object> subscriber) {
                final Object event = new Object();
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("click", "click");
                        subscriber.onNext(event);
                    }
                });
            }
        });
    }


}
