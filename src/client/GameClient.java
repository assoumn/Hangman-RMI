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
            ClientCallbackImpl callback =
                    new ClientCallbackImpl();

            // Ask username
            Scanner scanner = new Scanner(System.in);

            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            String[] pendingInvitation = new String[1];

            // Register player
            boolean success =
                    server.registerPlayer(username, callback);

            if (success) {

                System.out.println("Connected to server!");

                // Show online players
                System.out.println("Online Players:");
                System.out.println(server.getOnlinePlayers());

                while (true) {

                    System.out.println("\nType username to invite OR yes/no:");
                    String input = scanner.nextLine();

                    // ACCEPT INVITATION
                    if (input.equalsIgnoreCase("yes")) {

                        String inviter = callback.getPendingInviter();

                        if (inviter != null) {

                            server.respondInvitation(
                                    inviter,
                                    username,
                                    true
                            );

                        } else {

                            System.out.println("No pending invitation.");
                        }
                    }

                    // REJECT INVITATION
                    else if (input.equalsIgnoreCase("no")) {

                        String inviter = callback.getPendingInviter();

                        if (inviter != null) {

                            server.respondInvitation(
                                    inviter,
                                    username,
                                    false
                            );

                        } else {

                            System.out.println("No pending invitation.");
                        }
                    }

                    // SEND INVITATION
                    else {

                        if (!input.equals(username)) {

                            server.invitePlayer(username, input);

                        } else {

                            System.out.println("You cannot invite yourself.");
                        }
                    }
                }

            } else {

                System.out.println("Username already exists.");
            }

        } catch (Exception e) {

            System.out.println("Client Error:");
            e.printStackTrace();
        }
    }
}