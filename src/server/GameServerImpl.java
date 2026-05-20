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

    private final String[] words = {
            "JAVA",
            "NETWORK",
            "THREAD",
            "OBJECT",
            "SERVER",
            "CLIENT",
            "REMOTE",
            "SOCKET"
    };

    // Store connected players
    private ConcurrentHashMap<String, ClientCallback> players;
    private ConcurrentHashMap<String, GameSession> activeGames;

    protected GameServerImpl() throws RemoteException {
        super();
        players = new ConcurrentHashMap<>();
        activeGames = new ConcurrentHashMap<>();
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

        ClientCallback inviter = players.get(from);
        ClientCallback receiver = players.get(to);

        if (inviter == null || receiver == null) {
            return;
        }

        if (accepted) {

            System.out.println(to +
                    " accepted invitation from " + from);

            // RANDOM WORD
            String randomWord =
                    words[(int)(Math.random() * words.length)];

            // CREATE SESSION
            GameSession session =
                    new GameSession(from, to, randomWord);

            // STORE SESSION
            activeGames.put(from, session);
            activeGames.put(to, session);

            // SEND MESSAGES
            inviter.receiveMessage(
                    to + " accepted your invitation."
            );

            receiver.receiveMessage(
                    "Game starting with " + from
            );

            // SEND GAME STATE
            inviter.updateGameState(
                    session.currentWord,
                    session.attemptsLeft,
                    session.currentTurn
            );

            receiver.updateGameState(
                    session.currentWord,
                    session.attemptsLeft,
                    session.currentTurn
            );

        } else {

            System.out.println(to +
                    " rejected invitation from " + from);

            inviter.receiveMessage(
                    to + " rejected your invitation."
            );
        }
    }
    @Override
    public synchronized void guessLetter(String player,
                                         char letter)
            throws RemoteException {

        GameSession session = activeGames.get(player);

        if (session == null || session.gameOver) {
            return;
        }

        if (!session.currentTurn.equals(player)) {

            ClientCallback currentPlayer =
                    players.get(player);

            if (currentPlayer != null) {

                currentPlayer.receiveMessage(
                        "It is not your turn."
                );
            }

            return;
        }

        letter = Character.toUpperCase(letter);

        if (session.guessedLetters.contains(letter)) {

            ClientCallback currentPlayer =
                    players.get(player);

            if (currentPlayer != null) {

                currentPlayer.receiveMessage(
                        "Letter already guessed."
                );
            }

            return;
        }

        session.guessedLetters.add(letter);

        boolean correct = false;

        StringBuilder updatedWord =
                new StringBuilder();

        for (int i = 0; i < session.secretWord.length(); i++) {

            char c = session.secretWord.charAt(i);

            if (c == letter ||
                    session.guessedLetters.contains(c)) {

                updatedWord.append(c).append(" ");

                if (c == letter) {
                    correct = true;
                }

            } else {

                updatedWord.append("_ ");
            }
        }

        session.currentWord = updatedWord.toString();

        if (!correct) {

            session.attemptsLeft--;

            session.currentTurn =
                    player.equals(session.player1)
                            ? session.player2
                            : session.player1;
        }

        ClientCallback p1 =
                players.get(session.player1);

        ClientCallback p2 =
                players.get(session.player2);

        p1.updateGameState(
                session.currentWord,
                session.attemptsLeft,
                session.currentTurn
        );

        p2.updateGameState(
                session.currentWord,
                session.attemptsLeft,
                session.currentTurn
        );

        // WIN
        if (!session.currentWord.contains("_")) {

            session.gameOver = true;

            p1.gameOver("Word guessed!");
            p2.gameOver("Word guessed!");
        }

        // LOSE
        if (session.attemptsLeft <= 0) {

            session.gameOver = true;

            p1.gameOver(
                    "Game Over! Word was: "
                            + session.secretWord
            );

            p2.gameOver(
                    "Game Over! Word was: "
                            + session.secretWord
            );
        }
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