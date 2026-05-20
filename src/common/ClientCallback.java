package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientCallback extends Remote {

    void receiveMessage(String message)
            throws RemoteException;

    void receiveInvitation(String fromPlayer)
            throws RemoteException;

    void updatePlayerList(List<String> players)
            throws RemoteException;

    void updateGameState(String currentWord,
                         int attemptsLeft,
                         String currentTurn)
            throws RemoteException;

    void gameOver(String result)
            throws RemoteException;
}