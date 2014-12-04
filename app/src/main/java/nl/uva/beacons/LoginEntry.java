package nl.uva.beacons;

import java.io.Serializable;

/**
 * Created by sander on 11/20/14.
 */
public class LoginEntry implements Serializable {
    public String uuid;
    public String courseName;
    public String url;
    public String userToken;
    public String userRole;

    @Override
    public boolean equals(Object o) {
        return uuid.equals(((LoginEntry) o).uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

}
