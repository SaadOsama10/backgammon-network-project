package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * LocalGame - two players on one device ("pass and play").
 * Two real GamePanels (White = Player 1, Black = Player 2) are connected by a LocalTransport,
 * so every move travels as the usual MOVE message and turn enforcement works as in an online game.
 * Only the panel of the player whose turn it is gets shown, in a single window.
 */
public final class LocalGame {

    private static final int BANNER_HEIGHT = 32;

    private final GamePanel[] panels = new GamePanel[2]; // [0] = White (Player 1), [1] = Black (Player 2)
    private final JPanel cards = new JPanel(new CardLayout());
    private final JLabel banner = new JLabel("", SwingConstants.CENTER);

    private LocalGame() {
    }

    /** Replaces the frame's content with a new local 2-player game. */
    public static LocalGame start(JFrame frame) {
        LocalGame game = new LocalGame();
        game.panels[0] = new GamePanel();
        game.panels[1] = new GamePanel();
        // Both panels stay in the window (so dialogs and "play again" work), but only one card is visible
        game.cards.add(game.panels[0], "1");
        game.cards.add(game.panels[1], "2");
        game.banner.setOpaque(true);
        game.banner.setBackground(new Color(30, 30, 60));
        game.banner.setForeground(new Color(255, 215, 0));
        game.banner.setFont(new Font("Arial", Font.BOLD, 16));
        game.banner.setPreferredSize(new Dimension(10, BANNER_HEIGHT));

        LocalTransport.connect(game.panels[0], game.panels[1], game::delivered);

        frame.getContentPane().removeAll();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(game.banner, BorderLayout.NORTH);
        frame.getContentPane().add(game.cards, BorderLayout.CENTER);
        frame.setSize(frame.getWidth(), Math.max(frame.getHeight(), 730 + BANNER_HEIGHT));
        game.showTurn(1);
        frame.setTitle("Backgammon - White's Turn ⚪");
        frame.revalidate();
        frame.repaint();
        return game;
    }

    /** The panel of the given side (1 = White, 2 = Black). */
    public GamePanel getPanel(int player) {
        return panels[player - 1];
    }

    /** After each relayed message, show the board of whoever moves next. */
    private void delivered(int sender, String line) {
        int from = Integer.parseInt(line.split(":")[1]);
        int receiver = 3 - sender;
        if (from == -3) {
            showTurn(1); // restart: White begins again
        } else if (from == -4) {
            showTurn(receiver); // resign: the opponent sees the win dialog
        } else {
            showTurn(panels[receiver - 1].getCurrentPlayer());
        }
    }

    private void showTurn(int player) {
        ((CardLayout) cards.getLayout()).show(cards, String.valueOf(player));
        banner.setText(player == 1 ? "⚪ White's turn (Player 1)" : "⚫ Black's turn (Player 2)");
    }
}
