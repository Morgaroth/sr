package pl.morgaroth.checkers.server;

import pl.morgaroth.checkers.api.Board;
import pl.morgaroth.checkers.api.Check;

public class BoardImpl implements Board {

    private Check[][] checks;

    public BoardImpl(Check[][] checks) {
        this.checks = checks;
    }

    @Override
    public String getStringRepr() {
        StringBuilder builder = new StringBuilder();
        insertLine(builder);
        Check[] checkRow = null;
        for (Check[] check : checks) {
            checkRow = check;
            renderBoardRow(checkRow, builder);
            insertLine(builder);
        }

        return builder.toString();
    }

    private void renderBoardRow(Check[] checkRow, StringBuilder builder) {
        builder.append("|");
        for (Check check : checkRow) {
            builder.append((check == null) ? " " : (check.getNumber() == -1 ? "●" : String.valueOf(check.getNumber()))).append("|");
        }
        builder.append(System.lineSeparator());
    }

    private void insertLine(StringBuilder builder) {
        builder.append("+-+-+-+-+-+-+-+-+").append(System.lineSeparator());
    }
}
