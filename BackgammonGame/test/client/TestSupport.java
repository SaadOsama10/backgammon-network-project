package client;

import game.BackgammonBoard;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Helpers for the automated game tests: they drive the real GamePanel (clicks on the real
 * labels and buttons), script the dice and auto-answer the JOptionPane dialogs while recording them.
 * Needs a display (windows are shown), so run it on a desktop or under xvfb.
 */
final class TestSupport {

    private TestSupport() {
    }

    /** Dice that return scripted values instead of random ones; everything else is the real board. */
    static class ScriptedBoard extends BackgammonBoard {
        private final List<int[]> rolls = new ArrayList<>();

        void queue(int a, int b) {
            rolls.add(new int[]{a, b});
        }

        @Override
        public int[] rollDice() {
            if (rolls.isEmpty()) {
                throw new IllegalStateException("no scripted dice left");
            }
            return rolls.remove(0);
        }
    }

    /** Records every JOptionPane dialog that appears and closes it (YES for questions, OK otherwise). */
    static final class Dialogs {
        final List<String> seen = Collections.synchronizedList(new ArrayList<String>());
        private volatile boolean running = true;

        Dialogs() {
            Thread t = new Thread(() -> {
                while (running) {
                    for (Window w : Window.getWindows()) {
                        if (w instanceof JDialog && w.isShowing()) {
                            JOptionPane pane = findPane(w);
                            if (pane != null && pane.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
                                seen.add(String.valueOf(pane.getMessage()));
                                final boolean question = pane.getOptionType() == JOptionPane.YES_NO_OPTION;
                                SwingUtilities.invokeLater(() -> pane.setValue(question ? JOptionPane.YES_OPTION : JOptionPane.OK_OPTION));
                            }
                        }
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }, "dialog-dismisser");
            t.setDaemon(true);
            t.start();
        }

        void stop() {
            running = false;
        }

        /** Whether a recorded dialog contains the text. */
        boolean saw(String text) {
            synchronized (seen) {
                for (String s : seen) {
                    if (s.contains(text)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /** Waits up to 3 seconds for a dialog containing the text. */
        boolean awaitDialog(String text) {
            for (int i = 0; i < 100; i++) {
                if (saw(text)) {
                    return true;
                }
                sleep(30);
            }
            return false;
        }

        private static JOptionPane findPane(Component c) {
            if (c instanceof JOptionPane) {
                return (JOptionPane) c;
            }
            if (c instanceof Container) {
                for (Component child : ((Container) c).getComponents()) {
                    JOptionPane p = findPane(child);
                    if (p != null) {
                        return p;
                    }
                }
            }
            return null;
        }
    }

    // ---- reflection helpers ----

    /** Finds a (private) field, also in superclasses. */
    private static Field findField(Object target, String name) throws NoSuchFieldException {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                // try the superclass
            }
        }
        throw new NoSuchFieldException(name);
    }

    static Object field(Object target, String name) {
        try {
            return findField(target, name).get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void setField(Object target, String name, Object value) {
        try {
            findField(target, name).set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Replaces the panel's board with a scripted one (call before any move, and again after a restart). */
    static ScriptedBoard scriptDice(GamePanel panel) {
        ScriptedBoard board = new ScriptedBoard();
        setField(panel, "board", board);
        return board;
    }

    static BackgammonBoard board(GamePanel panel) {
        return (BackgammonBoard) field(panel, "board");
    }

    /** Sets a board position directly (used to reach bar / bear-off situations quickly). */
    static void setPosition(GamePanel panel, int[] points, int barWhite, int barBlack, int currentPlayer) {
        BackgammonBoard b = board(panel);
        setField(b, "points", points.clone());
        setField(b, "barPlayer1", barWhite);
        setField(b, "barPlayer2", barBlack);
        setField(b, "currentPlayer", currentPlayer);
    }

    /** A board snapshot: 24 points, both bar counts and whose turn it is. */
    static String snapshot(GamePanel panel) {
        BackgammonBoard b = board(panel);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 24; i++) {
            sb.append(b.getPoint(i)).append(',');
        }
        return sb + "|bar " + b.getBarPlayer1() + "/" + b.getBarPlayer2() + "|turn " + b.getCurrentPlayer();
    }

    // ---- UI actions (all on the Swing event thread) ----

    static void onEdt(Runnable r) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                SwingUtilities.invokeAndWait(r);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void roll(GamePanel panel) {
        onEdt(() -> ((JButton) field(panel, "jButton2")).doClick());
    }

    static void bearOff(GamePanel panel) {
        // BO listens for mouse clicks (its action listener is empty)
        click((JButton) field(panel, "jButton3"));
    }

    static void restart(GamePanel panel) {
        onEdt(() -> ((JButton) field(panel, "jButton1")).doClick());
    }

    static void resign(GamePanel panel) {
        onEdt(() -> ((JButton) field(panel, "jButton4")).doClick());
    }

    /** Clicks a board point by index 0-23 (index i is the label "point{i+1}"). */
    static void clickPoint(GamePanel panel, int index) {
        click((JLabel) field(panel, "point" + (index + 1)));
    }

    /** Clicks the bar label of a side (1 = White's "W:", 2 = Black's "B:"). */
    static void clickBar(GamePanel panel, int side) {
        click((JLabel) field(panel, "barLabel" + side));
    }

    /** One move: click the source, then the destination. */
    static void move(GamePanel panel, int from, int to) {
        clickPoint(panel, from);
        clickPoint(panel, to);
    }

    private static void click(Component label) {
        onEdt(() -> {
            MouseEvent e = new MouseEvent(label, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 5, 5, 1, false);
            for (MouseListener l : label.getMouseListeners()) {
                l.mouseClicked(e);
            }
        });
    }

    // ---- assertions ----

    static int checks = 0;

    static void check(boolean condition, String what) {
        checks++;
        if (!condition) {
            throw new AssertionError("FAILED: " + what);
        }
        System.out.println("  ok: " + what);
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Lets pending invokeLater work (image updates, turn titles) finish. */
    static void settle() {
        sleep(150);
        onEdt(() -> { });
    }
}
