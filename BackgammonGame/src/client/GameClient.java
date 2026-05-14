package client;
import java.io.*;
import java.net.*;

/**
 * GameClient - Handles the network connection between the player and the server.
 * Connects to the GameServer using the server IP and port 6000.
 * Sends moves to the server and receives opponent moves in a background thread.
 */
public class GameClient {
    
    private Socket socket;
    private PrintWriter out;      // output stream to send messages to server
    private BufferedReader in;    // input stream to receive messages from server
    private int playerNumber;     // 1 or 2, assigned by the server
    private GamePanel gamePanel;  // reference to update the board UI
    
    /**
     * Constructor - connects to the server and starts listening for incoming moves.
     * @param serverIP the IP address of the game server
     * @param gamePanel the game panel to apply opponent moves on
     */
    public GameClient(String serverIP, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            // Connect to the server on port 6000
            socket = new Socket(serverIP, 6000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Server connection:" + socket.isConnected());
            
            // Read the first message from server to get player number
            String firstMessage = in.readLine();
            if (firstMessage.startsWith("PLAYER:")) {
                playerNumber = Integer.parseInt(firstMessage.split(":")[1]);
                System.out.println("You are Player " + playerNumber);
            }
            
            // Background thread to continuously listen for opponent moves
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        // Parse move message format: MOVE:from:to:movesLeft
                        if (message.startsWith("MOVE:")) {
                            String[] parts = message.split(":");
                            int from = Integer.parseInt(parts[1]);
                            int to = Integer.parseInt(parts[2]);
                            int opponentMovesLeft = Integer.parseInt(parts[3]);
                            // Apply the opponent's move on the local board
                            gamePanel.applyOpponentMove(from, to, opponentMovesLeft);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server");
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        javax.swing.JOptionPane.showMessageDialog(null, 
                            "Opponent disconnected! Game over.", 
                            "Disconnected", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                        System.exit(0);
                    });
                }
            }).start();
            
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
    
    /**
     * Sends a move to the server in the format MOVE:from:to:movesLeft
     * @param from source point index (0-23)
     * @param to destination point index (0-23)
     * @param movesLeft number of moves remaining after this move
     */
    public void sendMove(int from, int to, int movesLeft) {
        out.println("MOVE:" + from + ":" + to + ":" + movesLeft);
    }
    
    /**
     * Returns the player number assigned by the server (1 or 2)
     */
    public int getPlayerNumber() {
        return playerNumber;
    }
    
    
}