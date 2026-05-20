package server;

import common.ClientCallback;
import common.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameServerImpl extends UnicastRemoteObject
        implements ServerInterface {

    // Store connected players
    private ConcurrentHashMap<String, ClientCallback> players;

    protected GameServerImpl() throws RemoteException {
        super();
        players = new ConcurrentHashMap<>();

        System.out.println("Game Server Started...");
    }

    @Override
    public synchronized boolean registerPlayer(String username,
                                               ClientCallback client)
            throws RemoteException {

        if (players.containsKey(username)) {
            return false;
        }

        players.put(username, client);

        broadcastPlayerList();

        System.out.println(username + " connected.");

        return true;
    }

    @Override
    public List<String> getOnlinePlayers()
            throws RemoteException {

        return new ArrayList<>(players.keySet());
    }

    @Override
    public void invitePlayer(String from,
                             String to)
            throws RemoteException {

        ClientCallback invitedPlayer = players.get(to);

        if (invitedPlayer != null) {

            invitedPlayer.receiveInvitation(from);

            System.out.println(from +
                    " invited " + to);
        }
    }

    @Override
    public void respondInvitation(String from,
                                  String to,
                                  boolean accepted)
            throws RemoteException {

        System.out.println(to +
                (accepted ? " accepted " : " rejected ")
                + from);
    }

    @Override
    public void guessLetter(String player,
                            char letter)
            throws RemoteException {

        System.out.println(player +
                " guessed: " + letter);
    }

    @Override
    public synchronized void disconnectPlayer(String username)
            throws RemoteException {

        players.remove(username);

        broadcastPlayerList();

        System.out.println(username + " disconnected.");
    }

    private void broadcastPlayerList() {

        List<String> onlinePlayers =
                new ArrayList<>(players.keySet());

        for (ClientCallback client : players.values()) {

            try {

                client.updatePlayerList(onlinePlayers);

            } catch (RemoteException e) {

                e.printStackTrace();
            }
        }
    }
}