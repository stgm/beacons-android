package nl.uva.beacons.interfaces;

import nl.uva.beacons.login.LoginEntry;

/**
 * Created by sander on 12/9/14.
 */
public interface OnLoginClickListener {
    void onLoginRemoveClicked(LoginEntry loginEntry, int position);
}
