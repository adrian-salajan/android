package ro.asalajan.biletmaster.services;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Iterator;

import ro.asalajan.biletmaster.Constants;
import rx.Observable;
import rx.observables.BlockingObservable;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;

public class TestObs {

    @Test
  public void sequence() {
        BlockingObservable<Integer> seq = Obs.sequence().toBlocking();

        assertEquals(Integer.valueOf(0), seq.first());

        Iterator<Integer> ite = seq.getIterator();

        assertEquals(Integer.valueOf(1), ite.next());
        assertEquals(Integer.valueOf(2), ite.next());
        assertEquals(Integer.valueOf(3), ite.next());
    }

    @Test
    public void anyObsToSequence() {
        BlockingObservable<Integer> seq = Obs.obsToSequence(Observable.just("a", "b", "c")).toBlocking();

        TestSubscriber<Integer> probe = new TestSubscriber<>();
        seq.subscribe(probe);

        probe.assertValues(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2));
        probe.assertCompleted();
        probe.assertNoErrors();
    }

//    @Test
//    public void advanceFromDate() {
//        Observable<Integer> deltas = Observable.just(0, 1, 2, 4);
//
//        Observable<String> dates = Obs.advanceFromDate(2016, Calendar.APRIL, 28, deltas, Constants.DATE_FORMATER.get());
//
//
//        TestSubscriber<String> probe = new TestSubscriber<>();
//        dates.subscribe(probe);
//
//        probe.assertValues(
//                "20160428",
//                "20160429",
//                "20160430",
//                "20160502"
//        );
//        probe.assertCompleted();
//        probe.assertNoErrors();
//    }
}
