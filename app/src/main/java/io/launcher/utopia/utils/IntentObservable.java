package io.launcher.utopia.utils;

import android.content.Intent;

import java.util.Observable;

public class IntentObservable extends Observable {
    private Intent i = null;

    public Intent getI() {
        return i;
    }

    public void setI(Intent i) {
        this.i = i;
        setChanged();
        notifyObservers();
    }
}
