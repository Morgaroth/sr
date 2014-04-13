package pl.morgaroth.checkers.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import pl.morgaroth.checkers.api.Agora;
import pl.morgaroth.checkers.server.core.AgoraImpl;
import org.apache.log4j.Logger;

public class CheckersServer {

    private static final Logger logger = Logger.getLogger(CheckersServer.class);

    private static String RMI_REGISTRY_ADDRESS = "rmi://127.0.0.1:1099";
    private static final String GAME_REMOTE_OBJECT_NAME = "checkers";

    public static void main(String[] args) {

        try {
            if (args.length > 0) {
                RMI_REGISTRY_ADDRESS = args[0];
            }
            AgoraImpl impl = new AgoraImpl();
            Agora agora = (Agora) UnicastRemoteObject.exportObject(impl, 0);
            Registry createRegistry = LocateRegistry.createRegistry(1099);
            Naming.rebind(RMI_REGISTRY_ADDRESS + "/" + GAME_REMOTE_OBJECT_NAME, agora);
            logger.debug("Tablica uruchomiona!");
        } catch (Exception e) {
            logger.error(e);
            System.exit(-1);
        }
    }
}
