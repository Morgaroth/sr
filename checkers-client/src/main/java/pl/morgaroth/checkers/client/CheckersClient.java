package pl.morgaroth.checkers.client;

import pl.morgaroth.checkers.api.Agora;
import pl.morgaroth.checkers.api.GameListener;
import pl.morgaroth.checkers.api.UserToken;
import org.apache.log4j.Logger;

import java.rmi.Naming;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.Thread.sleep;


public class CheckersClient {

    private static final Logger logger = Logger.getLogger(CheckersClient.class);

    public static final String RMI_REGISTRY_ADDRESS = "rmi://127.0.0.1:1099";
    private static final String GAME_REMOTE_OBJECT_NAME = "checkers";

    public static void main(String[] args) {

        try {

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            IddleThread iddleThread = new IddleThread();
            iddleThread.start();

            Agora agora = (Agora) Naming.lookup(RMI_REGISTRY_ADDRESS + "/" + GAME_REMOTE_OBJECT_NAME);

            logger.debug("Mam referencje do obiektu zdalnego!");

            UserToken token = agora.login(args[1]);

            MyGameListener listenerImpl = new MyGameListener(token, iddleThread, RMI_REGISTRY_ADDRESS);
            GameListener listener = (GameListener) UnicastRemoteObject.exportObject(listenerImpl, 0);

            if (args[0].equals("1")) {
                agora.newGame(token, args[2], listener);
            } else {
                agora.joinGame(token, listener, args[2]);
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
