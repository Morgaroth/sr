package pl.morgaroth.checkers.api.exceptions;

import pl.morgaroth.checkers.api.exceptions.GameException;

public class InvalidUserException extends GameException {
    public InvalidUserException(String s) {
        super(s);
    }

    public InvalidUserException() {
        super();
    }
}
