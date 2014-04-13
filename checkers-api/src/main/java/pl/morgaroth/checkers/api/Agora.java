package pl.morgaroth.checkers.api;

import pl.morgaroth.checkers.api.exceptions.GameException;
import pl.morgaroth.checkers.api.move.Direct;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface Agora extends Remote {

    UserToken login(String nick) throws RemoteException, GameException;

    void logout(UserToken token) throws RemoteException, GameException;

    void newGame(UserToken token, String oponentNick, GameListener listener) throws RemoteException, GameException;

    Collection<String> usersWaitingForMe(UserToken token) throws RemoteException, GameException;

    void joinGame(UserToken token, GameListener listener, String oponentNick) throws RemoteException, GameException;

    void move(Check check, Direct direct) throws RemoteException, GameException;
}
