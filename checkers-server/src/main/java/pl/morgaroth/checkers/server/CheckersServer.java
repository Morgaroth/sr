package pl.morgaroth.checkers.server;

import org.apache.log4j.Logger;
import pl.morgaroth.checkers.api.Agora;
import pl.morgaroth.checkers.server.core.AgoraImpl;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import static java.lang.String.format;

public class CheckersServer {

    private static final Logger logger = Logger.getLogger(CheckersServer.class);

    private static final String GAME_REMOTE_OBJECT_NAME = "checkers";

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("arguments required:\nRMI_IP RMI_PORT");
            return;
        }
        System.setProperty("java.rmi.server.hostname", args[0]);
        String RMI_REGISTRY_ADDRESS = format("rmi://%s:%s", args[0], args[1]);
        try {
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
