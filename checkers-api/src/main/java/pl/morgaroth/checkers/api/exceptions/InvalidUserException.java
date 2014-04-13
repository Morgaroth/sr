package pl.morgaroth.checkers.api.exceptions;

public class InvalidUserException extends GameException {
    public InvalidUserException(String s) {
        super(s);
    }

    public InvalidUserException() {
        super();
    }
}
