package ro.asalajan.biletmaster.android.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import javax.security.auth.Subject;

import ro.asalajan.biletmaster.R;
import ro.asalajan.biletmaster.view.NoInternetView;
import rx.Observable;
import rx.subjects.PublishSubject;

public class NoInternetFragment extends Fragment implements NoInternetView {

    private static Object CLICK_EVENT = new Object();
    private Button retryButton;

    private PublishSubject<Object> clicks = PublishSubject.create();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.no_internet_frag, container, false);
        retryButton = (Button) view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(click -> clicks.onNext(CLICK_EVENT));
        return view;
    }

    @Override
    public Observable<Object> retry() {
        return clicks.asObservable();
    }


    @Override
    public void onViewCreate() {

    }

    @Override
    public void onBackground() {

    }

    @Override
    public void onForeground() {

    }

    @Override
    public void onViewDestroy() {

    }
}
