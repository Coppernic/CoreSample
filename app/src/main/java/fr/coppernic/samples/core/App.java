package fr.coppernic.samples.core;

import android.app.Application;

import timber.log.Timber;

/**
 * <p>Created on 15/11/17
 *
 * @author bastien
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());
    }
}
