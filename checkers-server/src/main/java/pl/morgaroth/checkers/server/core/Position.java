package pl.morgaroth.checkers.server.core;

public class Position {
    public int row, column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Position{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
