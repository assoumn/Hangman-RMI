package server;

import common.ServerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class GameServer {

    public static void main(String[] args) {

        try {

            // Create server object
            ServerInterface server =
                    new GameServerImpl();

            // Create registry on port 1099
            Registry registry =
                    LocateRegistry.createRegistry(1099);

            // Bind server
            registry.rebind("HangmanServer", server);

            System.out.println("RMI Server is running...");

        } catch (Exception e) {

            System.out.println("Server Error:");
            e.printStackTrace();
        }
    }
}