package pl.morgaroth.checkers.server.core;

import pl.morgaroth.checkers.api.*;
import pl.morgaroth.checkers.api.exceptions.CheckNotExistsException;
import pl.morgaroth.checkers.api.exceptions.GameException;
import pl.morgaroth.checkers.api.exceptions.MoveNotPossiblyException;
import pl.morgaroth.checkers.api.exceptions.NotYourMoveException;
import pl.morgaroth.checkers.api.move.Move;
import pl.morgaroth.checkers.server.BoardImpl;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameImpl implements Game {

    private final Player player;
    private final Player player1;
    private ServerCheck[][] checks;
    private Map<ServerCheck, Position> checkersPositions;
    private Player active, notActive;

    private GameImpl(Player player, Player player1) {
        this.player = player;
        this.player1 = player1;
        checks = new ServerCheck[8][8];
        checkersPositions = new HashMap<ServerCheck, Position>(16);
    }

    public static GameImpl newBoard(String nick1, String nick2, GameListener nick1Listener, GameListener nick2Listener) {
        GameImpl board = new GameImpl(new Player(nick1, nick1Listener), new Player(nick2, nick2Listener));
        board.insertChecks();
        return board;
    }

    private void insertChecks() {
        for (int i = 0; i < 8; ++i) {
            int row = 6 + (i / 4);
            int column = (i % 4) * 2 + i / 4;
            checks[row][column] = new ServerCheck(player.nick, i);
            checkersPositions.put(checks[row][column], new Position(row, column));
        }
        for (int i = 0; i < 8; ++i) {
            int row = 1 - (i / 4);
            int column = 7 - (i % 4) * 2 - i / 4;
            checks[row][column] = new ServerCheck(player1.nick, i);
            checkersPositions.put(checks[row][column], new Position(row, column));
        }
    }

    public Board getBoardViewForUser(String nick) {
        Check[][] result = new Check[8][8];
        for (int r = 0; r < checks.length; ++r) {
            ServerCheck[] checkRow = checks[calc(nick, r)];
            for (int c = 0; c < checkRow.length; ++c) {
                if (checkRow[calc(nick, c)] != null) {
                    result[r][c] = (checkRow[calc(nick, c)].getOwner().equals(nick)) ? new CheckImpl(checkRow[calc(nick, c)].getNumber()) : new CheckImpl(-1);
                }
            }
        }
        return new BoardImpl(result);
    }

    int calc(String user, int i) {
        if (user.equals(player1.nick)) {
            return 7 - i;
        } else {
            return i;
        }
    }


    public void startGame(Game GAME_REMOTE_OBJECT_NAME) {
        boolean end = false;
        active = player;
        notActive = player1;
        try {
            notActive.listener.startGame(GAME_REMOTE_OBJECT_NAME);
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                active.listener.error("remote exception from oponent " + notActive.nick);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            return;
        }
        try {
            active.listener.startGame(GAME_REMOTE_OBJECT_NAME);
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                notActive.listener.error("oponent exception oponent " + active.nick);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
        notifyPlayers();
    }

    void notifyPlayers() {
        try {
            notActive.listener.updateBoard(getBoardViewForUser(notActive.nick));
        } catch (RemoteException e) {
            e.printStackTrace();
            try {
                active.listener.error("remote exception from oponent " + notActive.nick);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            // TODO jak zakonczyc?
        }
        try {
            active.listener.doMove(getBoardViewForUser(active.nick));
        } catch (RemoteException e) {
            try {
                notActive.listener.error("remote exception from oponent " + active.nick);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void switchPlayers() {
        Player tmp = active;
        active = notActive;
        notActive = tmp;
    }

    @Override
    public void doMove(UserToken user, Move move) throws RemoteException, GameException {
        ServerCheck checkToMove = new ServerCheck(user.getUserName(), move.number);
        if (!checkersPositions.containsKey(checkToMove)) {
            throw new CheckNotExistsException();
        }
        if (!user.getUserName().equals(active.nick)) {
            throw new NotYourMoveException();
        }
        executeMove(checkToMove, move);
        switchPlayers();
        notifyPlayers();
    }

    private void executeMove(ServerCheck check, Move move) throws MoveNotPossiblyException {
        Position checkPosition = checkersPositions.get(check);
        Position out = null;
        if (check.getOwner().equals(player1.nick)) {
            out = calculateOutPositionFromUp(move, checkPosition, 1);
            ServerCheck oneFieldMove = checks[out.row][out.column];
            if (oneFieldMove == null) {
                moveCheck(check, out);
            } else {
                if (oneFieldMove.getOwner().equals(player1.nick)) {
                    notPossibly();
                }
                out = calculateOutPositionFromUp(move, checkPosition, 2);
                if (checks[out.row][out.column] != null) {
                    notPossibly();
                }
                hitCheck(oneFieldMove);
                moveCheck(check, out);
            }
        } else {
            out = calculateOutPositionFromDown(move, checkPosition, 1);
            ServerCheck oneFieldMove = checks[out.row][out.column];
            if (oneFieldMove == null) {
                moveCheck(check, out);
            } else {
                if (oneFieldMove.getOwner().equals(player.nick)) {
                    notPossibly();
                }
                out = calculateOutPositionFromDown(move, checkPosition, 2);
                if (checks[out.row][out.column] != null) {
                    notPossibly();
                }
                System.out.println("check=" + check + "\nto=" + out);
                hitCheck(oneFieldMove);
                moveCheck(check, out);
            }
        }
        if (checkersPositions.size() == 1) {
            fireEndOfGame();
        }
    }

    private void fireEndOfGame() {
        Set<ServerCheck> checkers = checkersPositions.keySet();
        assert checkers.size() == 1 : "fire end but more checks";
        ServerCheck next = checkers.iterator().next();
        try {
            if (next.getOwner().equals(player.nick)) {
                player.listener.winner();
                player1.listener.gameOver();
            } else {
                player1.listener.winner();
                player.listener.gameOver();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void hitCheck(ServerCheck oneFieldMove) {
        Position delPosition = checkersPositions.remove(oneFieldMove);
        checks[delPosition.row][delPosition.column] = null;
    }

    private void moveCheck(ServerCheck check, Position out) {
        Position in = checkersPositions.put(check, out);
        checks[in.row][in.column] = null;
        checks[out.row][out.column] = check;
    }

    private Position calculateOutPositionFromUp(Move move, Position checkPosition, int howFar) throws MoveNotPossiblyException {
        return calculateOutPosition(true, move, checkPosition, howFar);
    }

    private Position calculateOutPositionFromDown(Move move, Position checkPosition, int howFar) throws MoveNotPossiblyException {
        return calculateOutPosition(false, move, checkPosition, howFar);
    }

    private Position calculateOutPosition(boolean userUp, Move move, Position checkPosition, int howFar) throws MoveNotPossiblyException {
        Position out = null;
        int r = checkPosition.row + (userUp ? 1 : -1) * howFar;
        int c;
        switch (move.direct) {
            case Left:
                c = checkPosition.column + (userUp ? 1 : -1) * howFar;
                break;
            case Right:
                c = checkPosition.column + (userUp ? -1 : 1) * howFar;
                break;
            default:
                throw new RuntimeException("impossible or extended enum");
        }
        if (r < 8 && r >= 0 && c < 8 && c >= 0) {
            out = new Position(r, c);
        } else {
            notPossibly();
        }
        return out;
    }

    private void notPossibly() throws MoveNotPossiblyException {
        throw new MoveNotPossiblyException();
    }

    @Override
    public Board getBoard(UserToken user) throws RemoteException {
        return getBoardViewForUser(user.getUserName());
    }
}
