package ro.asalajan.biletmaster.presenters;

import ro.asalajan.biletmaster.view.View;

public interface Presenter<T extends View> {

    void setView(T view);

    void removeView();
}
