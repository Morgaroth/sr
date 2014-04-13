package pl.morgaroth.checkers.api;

import java.io.Serializable;

public interface UserToken extends Serializable {
    String getUserName();
    Integer getId();
}
