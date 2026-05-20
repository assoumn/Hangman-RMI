package client;

import common.ClientCallback;
import common.ServerInterface;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class GameClient {

    public static void main(String[] args) {

        try {

            // Connect to registry
            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            // Lookup server
            ServerInterface server =
                    (ServerInterface) registry.lookup("HangmanServer");

            // Create callback object
            ClientCallback callback =
                    new ClientCallbackImpl();

            // Ask username
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            // Register player
            boolean success =
                    server.registerPlayer(username, callback);

            if (success) {

                System.out.println("Connected to server!");

                // Show online players
                System.out.println("Online Players:");
                System.out.println(server.getOnlinePlayers());

            } else {

                System.out.println("Username already exists.");
            }

        } catch (Exception e) {

            System.out.println("Client Error:");
            e.printStackTrace();
        }
    }
}