package pl.morgaroth.checkers.client;

import pl.morgaroth.checkers.api.*;
import pl.morgaroth.checkers.api.exceptions.GameException;
import pl.morgaroth.checkers.api.move.Direct;
import pl.morgaroth.checkers.api.move.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import static java.lang.String.format;

public class MyGameListener implements GameListener {

    private Game game = null;
    private UserToken userToken;
    private CheckersClient.IddleThread iddle;
    private String RMI_REGISTRY_ADDRESS;

    public MyGameListener(UserToken userToken, CheckersClient.IddleThread iddle, String RMI_REGISTRY_ADDRESS) {
        this.userToken = userToken;
        this.iddle = iddle;
        this.RMI_REGISTRY_ADDRESS = RMI_REGISTRY_ADDRESS;
    }

    class MoveListener implements Runnable {
        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                System.out.print("Type your move:\n\tchecknumber: ");
                System.out.flush();
                int number = Integer.parseInt(reader.readLine());
                System.out.print("direct 1-left, 2-right: ");
                System.out.flush();
                int dir = Integer.parseInt(reader.readLine());
                Move move = new Move(number, dir == 1 ? Direct.Left : Direct.Right);
                try {
                    game.doMove(userToken, move);
                } catch (GameException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void oponentLogout(String userName) throws RemoteException {
        System.out.println(format("oponent %s logout", userName));
        iddle.interruptMe();
    }

    @Override
    public void doMove(Board board) throws RemoteException {
        printBoard(board);
        startResponse();
    }

    private void startResponse() {
        new Thread(new MoveListener()).start();
    }

    @Override
    public void updateBoard(Board board) throws RemoteException {
        printBoard(board);
    }

    private void printBoard(Board board) {
        System.out.println("\n--------------------------------------------------------------");
        System.out.print(board.getStringRepr());
    }

    @Override
    public void error(String s) throws RemoteException {
        System.out.println("error " + s);
        iddle.interruptMe();
    }

    @Override
    public void startGame(Game boardRemoteName) throws RemoteException {
        game = boardRemoteName;
        System.out.println("game started");
    }

    @Override
    public void winner() {
        System.out.println("YOU WIN!!!!");
        iddle.interruptMe();
    }

    @Override
    public void gameOver() {
        System.out.println("GAME OVER");
        iddle.interruptMe();
    }
}