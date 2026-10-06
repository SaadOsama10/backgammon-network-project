package client;

import static client.TestSupport.*;

import java.net.ServerSocket;
import javax.swing.JFrame;
import server.GameServer;

/**
 * Online mode: a real GameServer on a free port and two real clients (GameClient + GamePanel),
 * playing a few moves over TCP. After every step both boards must be identical.
 * Ends with a resignation, which must show the opponent the win dialog.
 */
public class TcpGameTest {

    static GamePanel white;
    static GamePanel black;

    public static void main(String[] args) throws Exception {
        int port;
        try (ServerSocket s = new ServerSocket(0)) {
            port = s.getLocalPort();
        }
        Thread server = new Thread(() -> GameServer.main(new String[]{"--port", String.valueOf(port)}));
        server.setDaemon(true);
        server.start();
        sleep(500);

        ClientConfig.applyArgs(new String[]{"--port", String.valueOf(port)});
        Dialogs dialogs = new Dialogs();
        JFrame[] frames = new JFrame[2];
        GamePanel[] panels = new GamePanel[2];
        GameClient[] clients = new GameClient[2];
        // The server pairs players in connection order; connect them one after the other like two machines would
        for (int i = 0; i < 2; i++) {
            final int n = i;
            onEdt(() -> {
                frames[n] = new JFrame("tcp client " + (n + 1));
                panels[n] = new GamePanel();
                frames[n].add(panels[n]);
                frames[n].setSize(1000, 730);
                frames[n].setLocation(40 + n * 60, 40 + n * 40);
                frames[n].setVisible(true);
            });
            Thread connect = new Thread(() -> {
                clients[n] = new GameClient("localhost", panels[n]);
                panels[n].setPlayerNumber(clients[n].getPlayerNumber());
                panels[n].setGameClient(clients[n]);
            });
            connect.start();
            if (i == 1) {
                connect.join(10000);
            } else {
                sleep(400);
            }
        }
        white = panels[0];
        black = panels[1];
        System.out.println("TCP game test");
        check(clients[0].getPlayerNumber() == 1 && clients[1].getPlayerNumber() == 2, "server assigned PLAYER:1 and PLAYER:2");
        ScriptedBoard wb = scriptDice(white);
        ScriptedBoard bb = scriptDice(black);

        // White rolls 1-1 (doubles: four moves): 24 -> 23, 24 -> 23, then 23 -> 22 twice (indices 23->22, 22->21)
        wb.queue(1, 1);
        roll(white);
        move(white, 23, 22);
        move(white, 23, 22);
        move(white, 22, 21);
        move(white, 22, 21);
        settle();
        check(snapshot(white).equals(snapshot(black)), "boards identical after White's doubles");
        check(board(black).getCurrentPlayer() == 2, "turn passed to Black on the other client");
        check(board(black).getPoint(21) == 2 && board(black).getPoint(23) == 0, "Black's board shows White's moves");

        // Black rolls 4-3 and plays 0 -> 4 and 0 -> 3
        bb.queue(4, 3);
        roll(black);
        move(black, 0, 4);
        move(black, 0, 3);
        settle();
        check(snapshot(white).equals(snapshot(black)), "boards identical after Black's turn");
        check(board(white).getCurrentPlayer() == 1 && board(white).getPoint(4) == -1, "White's board shows Black's moves");

        // Black may not move on White's turn
        roll(black);
        check(dialogs.awaitDialog("not your turn"), "rolling out of turn is refused");

        // Resign: the opponent gets the win dialog
        resign(white);
        check(dialogs.awaitDialog("Opponent resigned! You win!"), "resign shows the opponent the win dialog");

        System.out.println("TCP game test passed (" + checks + " checks)");
        // The resigning online client exits the process by itself; stop here first
        Runtime.getRuntime().halt(0);
    }
}
