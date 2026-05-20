package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {

    boolean registerPlayer(String username,
                           ClientCallback client)
            throws RemoteException;

    List<String> getOnlinePlayers()
            throws RemoteException;

    void invitePlayer(String from,
                      String to)
            throws RemoteException;

    void respondInvitation(String from,
                           String to,
                           boolean accepted)
            throws RemoteException;

    void guessLetter(String player,
                     char letter)
            throws RemoteException;

    void disconnectPlayer(String username)
            throws RemoteException;
}