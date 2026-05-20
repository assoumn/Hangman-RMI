package client;

import common.ClientCallback;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ClientCallbackImpl extends UnicastRemoteObject
        implements ClientCallback {

    private String pendingInviter;

    protected ClientCallbackImpl() throws RemoteException {
        super();
    }

    @Override
    public void receiveMessage(String message)
            throws RemoteException {

        System.out.println(message);
    }

    @Override
    public void receiveInvitation(String fromPlayer)
            throws RemoteException {

        pendingInviter = fromPlayer;

        System.out.println("\n========================");
        System.out.println("Invitation from: " + fromPlayer);
        System.out.println("Type yes/no");
        System.out.println("========================\n");
    }

    @Override
    public void updatePlayerList(List<String> players)
            throws RemoteException {

        System.out.println("\n=== ONLINE PLAYERS ===");

        for (String player : players) {
            System.out.println(player);
        }

        System.out.println("======================\n");
    }

    @Override
    public void updateGameState(String currentWord,
                                int attemptsLeft,
                                String currentTurn)
            throws RemoteException {

        System.out.println("Word: " + currentWord);
        System.out.println("Attempts Left: " + attemptsLeft);
        System.out.println("Current Turn: " + currentTurn);
    }

    @Override
    public void gameOver(String result)
            throws RemoteException {

        System.out.println("Game Over: " + result);
    }

    public String getPendingInviter() {
        return pendingInviter;
    }
}