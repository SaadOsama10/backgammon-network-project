package client;

import static client.TestSupport.*;

import java.awt.CardLayout;
import javax.swing.JFrame;

/**
 * Local 2-player mode: LocalGame connects two real GamePanels through LocalTransport (same MOVE
 * protocol as online). Covers moves and turn handover, dice rules, hitting + bar entry, bear-off,
 * winning, restart and resign (which must show the win dialog and keep the program alive).
 */
public class LocalModeTest {

    static GamePanel white;
    static GamePanel black;
    static ScriptedBoard wb;
    static ScriptedBoard bb;
    static Dialogs dialogs;

    static void freshGame(LocalGame[] holder, JFrame frame) {
        onEdt(() -> holder[0] = LocalGame.start(frame));
        white = holder[0].getPanel(1);
        black = holder[0].getPanel(2);
        wb = scriptDice(white);
        bb = scriptDice(black);
    }

    static boolean synced() {
        return snapshot(white).equals(snapshot(black));
    }

    public static void main(String[] args) throws Exception {
        dialogs = new Dialogs();
        JFrame[] frameHolder = new JFrame[1];
        onEdt(() -> {
            frameHolder[0] = new JFrame("local mode test");
            frameHolder[0].setSize(1000, 730);
            frameHolder[0].setVisible(true);
        });
        JFrame frame = frameHolder[0];
        LocalGame[] game = new LocalGame[1];
        freshGame(game, frame);
        System.out.println("Local mode test");

        // --- Handshake and turn handover ---
        check(white.getCurrentPlayer() == 1 && black.getCurrentPlayer() == 1, "both boards start with White to move");
        check(visibleCard(frame).equals("1"), "White's board is shown first");

        // White 6-2 (distinct dice)
        wb.queue(6, 2);
        roll(white);
        // wrong direction (White moves toward index 0) is rejected, board unchanged
        String before = snapshot(white);
        move(white, 12, 14);
        check(snapshot(white).equals(before) && dialogs.saw("White moves from 24 to 1"), "White cannot move backwards");
        move(white, 12, 6);  // die 6: 13 -> 7
        check(board(white).getPoint(6) == 1 && board(white).getPoint(12) == 4, "White plays the 6 (13 -> 7)");
        // the 6 is spent: another distance-6 move is rejected
        before = snapshot(white);
        move(white, 12, 6);
        check(snapshot(white).equals(before) && dialogs.saw("Invalid move! Use dice numbers"), "a spent die cannot be reused");
        move(white, 12, 10);  // die 2: 13 -> 11
        settle();
        check(synced() && black.getCurrentPlayer() == 2 && white.getCurrentPlayer() == 2, "both boards synced, turn passed to Black");
        check(visibleCard(frame).equals("2"), "Black's board is shown after White's turn");

        // Not White's turn any more
        roll(white);
        check(dialogs.saw("not your turn"), "White cannot roll on Black's turn");

        // Black plays doubles 3-3: four moves
        bb.queue(3, 3);
        roll(black);
        move(black, 11, 14);
        move(black, 11, 14);
        move(black, 11, 14);
        check(black.getCurrentPlayer() == 2, "still Black's turn after three of four doubles moves");
        move(black, 11, 14);
        settle();
        check(synced() && white.getCurrentPlayer() == 1 && board(white).getPoint(14) == -4, "doubles give four moves, then turn passes");
        check(visibleCard(frame).equals("1"), "White's board is shown again");

        // --- Hit and bar entry ---
        int[] pos = new int[24];
        pos[10] = 1;            // White blot on point 11
        pos[23] = 2;            // White back checkers
        pos[6] = -1;            // Black piece on point 7 will hit the blot with a 4
        pos[15] = -2;           // Black elsewhere
        freshGame(game, frame);
        setPosition(white, pos, 0, 0, 2);
        setPosition(black, pos, 0, 0, 2);
        bb.queue(4, 1);
        roll(black);
        move(black, 6, 10);     // die 4 hits
        settle();
        check(board(black).getBarPlayer1() == 1 && board(white).getBarPlayer1() == 1, "hit sends the White blot to the bar on both boards");
        move(black, 15, 16);    // die 1
        settle();
        check(synced() && white.getCurrentPlayer() == 1, "turn passes to White");
        wb.queue(5, 2);
        roll(white);
        // moving another piece while on the bar is refused
        before = snapshot(white);
        move(white, 23, 21);
        check(snapshot(white).equals(before) && dialogs.saw("You have pieces on the Bar"), "pieces on the bar must enter first");
        // wrong entry point for the dice (die 3 is not available)
        clickBar(white, 1);
        clickPoint(white, 21);  // point 22 = entry for die 3
        check(snapshot(white).equals(before) && dialogs.saw("Use dice numbers"), "bar entry needs a rolled die");
        clickBar(white, 1);
        clickPoint(white, 19);  // point 20 = 25 - 5
        settle();
        check(board(white).getBarPlayer1() == 0 && board(white).getPoint(19) == 1 && synced(), "White re-enters with the 5 (point 20)");
        move(white, 23, 21);    // die 2
        settle();
        check(synced() && black.getCurrentPlayer() == 2, "turn passes after the bar entry and the next move");

        // --- Bear-off and win ---
        int[] home = new int[24];
        home[0] = 1;            // point 1
        home[2] = 1;            // point 3
        home[1] = 1;            // point 2
        home[20] = -2;
        freshGame(game, frame);
        setPosition(white, home, 0, 0, 1);
        setPosition(black, home, 0, 0, 1);
        wb.queue(3, 6);
        roll(white);
        // not allowed with a distance no die can bear off? point 2 (index 1) needs 2; a 6 may only bear off the furthest piece
        clickPoint(white, 1);
        bearOff(white);
        check(board(white).getPoint(1) == 1 && dialogs.saw("Invalid bear off"), "bearing off the point-2 piece with 3/6 is refused while point 3 is occupied");
        clickPoint(white, 2);
        bearOff(white);     // exact die 3
        settle();
        check(board(white).getPoint(2) == 0 && synced(), "exact die bears off the point-3 piece");
        clickPoint(white, 1);
        bearOff(white);     // higher die 6, nothing further back
        settle();
        check(board(white).getPoint(1) == 0 && synced(), "a higher die bears off the furthest piece");
        check(black.getCurrentPlayer() == 2, "turn passes after both dice are used");
        // Black has to move something; skip to White's final piece
        bb.queue(2, 1);
        roll(black);
        move(black, 20, 22);
        move(black, 20, 21);
        settle();
        wb.queue(1, 4);
        roll(white);
        clickPoint(white, 0);
        bearOff(white);     // last piece, die 1
        check(dialogs.awaitDialog("White ⚪ wins!"), "bearing off the last piece shows the win dialog");
        settle();
        check(frame.getContentPane().getComponentCount() == 1 && frame.getContentPane().getComponent(0) instanceof BoardPanel, "\"Play again\" returns to the start screen");

        // --- Restart ---
        freshGame(game, frame);
        wb.queue(1, 2);
        roll(white);
        move(white, 5, 4);
        restart(white);
        settle();
        check(synced() && white.getCurrentPlayer() == 1 && board(black).getPoint(5) == 5, "restart resets both boards together");
        wb = scriptDice(white);
        bb = scriptDice(black);

        // --- Resign ---
        resign(black);
        check(dialogs.awaitDialog("Opponent resigned! You win!"), "resign shows the opponent the win dialog");
        sleep(1500);
        check(true, "the program is still running after resigning (no exit in local mode)");

        System.out.println("Local mode test passed (" + checks + " checks)");
        System.exit(0);
    }

    /** Name of the visible card ("1" = White's board, "2" = Black's). */
    static String visibleCard(JFrame frame) {
        String[] name = {"?"};
        onEdt(() -> {
            for (java.awt.Component c : frame.getContentPane().getComponents()) {
                if (c instanceof javax.swing.JPanel && ((javax.swing.JPanel) c).getLayout() instanceof CardLayout) {
                    javax.swing.JPanel cards = (javax.swing.JPanel) c;
                    for (java.awt.Component card : cards.getComponents()) {
                        if (card.isVisible()) {
                            name[0] = card == white ? "1" : "2";
                        }
                    }
                }
            }
        });
        return name[0];
    }
}
