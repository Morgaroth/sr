package pl.morgaroth.checkers.server.core;

import pl.morgaroth.checkers.api.*;
import pl.morgaroth.checkers.api.exceptions.GameException;
import pl.morgaroth.checkers.api.move.Direct;
import pl.morgaroth.checkers.server.exceptions.InvalidUserException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class AgoraImpl implements Agora {

    private static final String RMI_REGISTRY_ADDRESS = "rmI";
    private HashSet<UserToken> registeredUsers;
    private Map<String, Map<String, GameListener>> pendingGames;

    private int maxId = 0;

    public AgoraImpl() {
        registeredUsers = new HashSet<UserToken>();
        pendingGames = new HashMap<String, Map<String, GameListener>>();
    }

    @Override
    public UserToken login(String nick) throws RemoteException, GameException {
        UserTokenImpl newToken = new UserTokenImpl(nick, ++maxId);
        if (registeredUsers.contains(newToken)) {
            throw new InvalidUserException("user nick registered!");
        }
        registeredUsers.add(newToken);
        if (!pendingGames.containsKey(nick)) {
            pendingGames.put(nick, new HashMap<String, GameListener>());
        }
        return newToken;
    }

    @Override
    public void logout(UserToken token) throws RemoteException, GameException {
        if (registeredUsers.contains(token)) {
            registeredUsers.remove(token);
            for (Map.Entry<String, GameListener> pendingForUser : pendingGames.remove(token.getUserName()).entrySet()) {
                pendingForUser.getValue().oponentLogout(token.getUserName());
            }
        } else {
            throw new InvalidUserException();
        }
    }

    @Override
    public void newGame(UserToken token, String oponentNick, GameListener listener) throws RemoteException, GameException {
        if (registeredUsers.contains(token)) {
            if (pendingGames.get(token.getUserName()).containsKey(oponentNick)) {
                throw new GameException("user is vaiting for game");
            }
            if (!pendingGames.containsKey(oponentNick)) {
                pendingGames.put(oponentNick, new HashMap<String, GameListener>());
            }
            pendingGames.get(oponentNick).put(token.getUserName(), listener);
        } else {
            throw new InvalidUserException();
        }
    }

    @Override
    public Collection<String> usersWaitingForMe(UserToken token) throws RemoteException, GameException {
        Map<String, GameListener> pending = pendingGames.get(token.getUserName());
        return (pending == null) ? Collections.<String>emptyList() : pending.keySet();
    }

    @Override
    public void joinGame(UserToken token, GameListener listener, String oponentNick) throws RemoteException, GameException {
        Map<String, GameListener> pending = pendingGames.get(token.getUserName());
        if (pending.containsKey(oponentNick)) {
            GameListener oponentListener = pending.get(oponentNick);
            GameImpl gameImpl = GameImpl.newBoard(oponentNick, token.getUserName(), oponentListener, listener);
            Game game = (Game) UnicastRemoteObject.exportObject(gameImpl, 0);
            gameImpl.startGame(game);
        } else {
            throw new GameException();
        }
    }

    @Override
    public void move(Check check, Direct direct) throws RemoteException, GameException {
        throw new NotImplementedException();
    }

}
