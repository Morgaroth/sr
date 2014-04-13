package pl.morgaroth.checkers.api;

import pl.morgaroth.checkers.api.exceptions.GameException;
import pl.morgaroth.checkers.api.move.Move;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Game extends Remote {
    void doMove(UserToken user, Move move) throws RemoteException, GameException;

    Board getBoard(UserToken user) throws RemoteException;
}
