package pl.morgaroth.checkers.server.core;

import pl.morgaroth.checkers.api.Check;

public class CheckImpl implements Check {
    private final int number;

    public CheckImpl(int number) {

        this.number = number;
    }

    @Override
    public int getNumber() {
        return number;
    }
}
