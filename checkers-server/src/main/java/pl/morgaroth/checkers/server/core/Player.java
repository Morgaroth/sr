package pl.morgaroth.checkers.server.core;

import pl.morgaroth.checkers.api.GameListener;

public class Player {
    public String nick;
    public GameListener listener;

    public Player(String nick, GameListener listener) {
        this.nick = nick;
        this.listener = listener;
    }

}
