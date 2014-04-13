package pl.morgaroth.checkers.client;

import pl.morgaroth.checkers.api.Agora;
import pl.morgaroth.checkers.api.GameListener;
import pl.morgaroth.checkers.api.UserToken;
import org.apache.log4j.Logger;
import pl.morgaroth.checkers.api.exceptions.InvalidUserException;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.String.format;
import static java.lang.Thread.sleep;


public class CheckersClient {

    private static final Logger logger = Logger.getLogger(CheckersClient.class);

    private static final String GAME_REMOTE_OBJECT_NAME = "checkers";

    public static void main(String[] args) {

        if (args.length < 5) {
            System.out.println("arguments required:\nRMI_HOSTNAME RMI_PORT CLIENT_HOSTNAME You [START|JOIN Oponent] or AUTO");
            return;
        }
        if (!args[4].toLowerCase().equals("auto") && args.length < 6) {
            System.out.println("arguments required:\nRMI_HOSTNAME RMI_PORT CLIENT_HOSTNAME You [START|JOIN Oponent] or AUTO");
            return;
        }
        System.setProperty("java.rmi.server.hostname", args[2]);
        String RMI_REGISTRY_ADDRESS = format("rmi://%s:%s", args[0], args[1]);

        try {

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            IddleThread iddleThread = new IddleThread();
            iddleThread.start();

            Agora agora = (Agora) Naming.lookup(RMI_REGISTRY_ADDRESS + "/" + GAME_REMOTE_OBJECT_NAME);

            try {
                UserToken token = agora.login(args[3]);
                MyGameListener listenerImpl = new MyGameListener(token, iddleThread);
                GameListener listener = (GameListener) UnicastRemoteObject.exportObject(listenerImpl, 0);

                if (args[4].toLowerCase().equals("start")) {
                    agora.newGame(token, args[5], listener);
                } else if (args[4].toLowerCase().equals("join")) {
                    agora.joinGame(token, listener, args[5]);
                } else if (args[4].toLowerCase().equals("auto")) {
                    agora.newGameWithBot(token,listener);
                } else {
                    logger.error("only join or start");
                    iddleThread.interruptMe();
                }
            } catch (InvalidUserException e) {
                logger.error(e.getMessage());
                System.exit(-1);
            }
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static class IddleThread extends Thread {
        private final Object monitor = new Object();
        private boolean inter = true;

        public void interruptMe() {
            synchronized (monitor) {
                inter = false;
                monitor.notifyAll();
            }
        }

        @Override
        public void run() {
            while (inter) {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
