package pl.morgaroth.checkers.server.exceptions;

import pl.morgaroth.checkers.api.exceptions.GameException;

public class InvalidCheckException extends GameException {
    public InvalidCheckException(String s) {
        super(s);
    }

    public InvalidCheckException() {
        super();
    }
}
