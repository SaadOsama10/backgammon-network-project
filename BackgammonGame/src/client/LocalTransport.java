package client;

/**
 * LocalTransport - an in-memory Transport that connects two players inside the same JVM.
 * It plays the role of GameServer: it hands out "PLAYER:1" / "PLAYER:2" and relays every
 * "MOVE:from:to:movesLeft" line from one player to the other, so each GamePanel runs exactly
 * the same code path as in an online game. Used by the browser demo, where raw TCP sockets
 * are not available.
 */
public class LocalTransport implements Transport {

    /** Notified after a line has been delivered to the opponent. */
    public interface Observer {
        void delivered(int senderPlayer, String line);
    }

    private final GamePanel panel;
    private LocalTransport peer;
    private Observer observer;
    private int playerNumber;

    private LocalTransport(GamePanel panel) {
        this.panel = panel;
    }

    /**
     * Connects two panels, like the server pairing two clients.
     * @param white the panel that becomes Player 1
     * @param black the panel that becomes Player 2
     * @param observer called after each relayed line (may be null)
     */
    public static void connect(GamePanel white, GamePanel black, Observer observer) {
        LocalTransport a = new LocalTransport(white);
        LocalTransport b = new LocalTransport(black);
        a.peer = b;
        b.peer = a;
        a.observer = observer;
        b.observer = observer;
        a.receive("PLAYER:1");
        b.receive("PLAYER:2");
    }

    @Override
    public void sendMove(int from, int to, int movesLeft) {
        String line = "MOVE:" + from + ":" + to + ":" + movesLeft;
        peer.receive(line);
        if (observer != null) {
            observer.delivered(playerNumber, line);
        }
    }

    @Override
    public int getPlayerNumber() {
        return playerNumber;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    /** Handles a line the way GameClient's listener does. */
    private void receive(String line) {
        if (line.startsWith("PLAYER:")) {
            playerNumber = Integer.parseInt(line.split(":")[1]);
            panel.setPlayerNumber(playerNumber);
            panel.setGameClient(this);
        } else if (line.startsWith("MOVE:")) {
            String[] parts = line.split(":");
            panel.applyOpponentMove(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
    }
}
