package pl.morgaroth.checkers.api;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GameListener extends Remote {
    void oponentLogout(String userName) throws RemoteException;

    void doMove(Board board) throws RemoteException;

    void updateBoard(Board board) throws RemoteException;

    void error(String s) throws RemoteException;

    void startGame(Game boardRemoteName) throws RemoteException;

    void winner() throws RemoteException;

    void gameOver() throws RemoteException;

    String getInfo() throws RemoteException;
}
