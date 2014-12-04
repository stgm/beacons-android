package nl.uva.beacons.api;

import android.app.Fragment;
import android.util.Log;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by sander on 11/6/14.
 */
public abstract class CancelableCallback<T> implements Callback<T> {
    private static final String TAG = "CancelableCallback";
    private boolean isCanceled = false;
    private boolean hasFragment = false;
    private WeakReference<Fragment> fragment;

    protected CancelableCallback(Fragment fragment) {
        if (fragment != null) {
            hasFragment = true;
            this.fragment = new WeakReference<Fragment>(fragment);
        }
    }

    protected CancelableCallback() {
    }

    public abstract void onSuccess(T t, Response response);

    public abstract void onFailure(RetrofitError error);

    @Override
    public void success(T t, Response response) {
        if (isOk() && t != null) {
            onSuccess(t, response);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        if (isOk()) {
            Log.d(TAG, "Error: " + error.getMessage() + ", url: " + error.getUrl());
            onFailure(error);
        }
    }

    private boolean isOk() {
        if (isCanceled) {
            return false;
        }
        if (hasFragment) {
            if (fragment == null || fragment.get() == null || !fragment.get().isAdded()) {
                return false;
            }
        }
        return true;
    }

    public void cancel() {
        isCanceled = true;
    }
}
