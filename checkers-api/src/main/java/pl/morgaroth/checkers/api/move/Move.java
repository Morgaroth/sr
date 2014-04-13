package pl.morgaroth.checkers.api.move;

import java.io.Serializable;

public class Move implements Serializable {
    public int number;

    public Move(int number, Direct direct) {
        this.number = number;
        this.direct = direct;
    }

    public Direct direct;
}
