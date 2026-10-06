package client;

/**
 * Transport - how a GamePanel sends its moves to the opponent.
 * Both implementations speak the same line protocol (PLAYER:n, MOVE:from:to:movesLeft,
 * with the special codes -1 to -5), so the game logic is identical:
 * GameClient (TCP, online play) and LocalTransport (in-memory, same-device play).
 */
public interface Transport {

    /** Sends MOVE:from:to:movesLeft to the opponent. */
    void sendMove(int from, int to, int movesLeft);

    /** This player's side: 1 = White, 2 = Black (0 if not assigned yet). */
    int getPlayerNumber();

    /** True when the opponent runs in the same JVM (local mode), so closing this player's side must not exit the program. */
    default boolean isLocal() {
        return false;
    }
}
