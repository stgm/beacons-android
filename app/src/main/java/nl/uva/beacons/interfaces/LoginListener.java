package nl.uva.beacons.interfaces;

import nl.uva.beacons.login.LoginEntry;

/**
 * Created by sander on 12/9/14.
 */
public interface LoginListener {
    void onLoginSuccess(boolean startUp, LoginEntry loginEntry);

    void onLoginFailure();
}