/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;
import java.io.*;
import java.net.*;

/**
 * GameServer - Central server for the Backgammon multiplayer game.
 * Supports multiple simultaneous games by handling each pair of players
 * in a separate thread. Listens on port 6000 for incoming connections.
 */
public class GameServer {
    
    // Port number the server listens on
    private static final int PORT = 6000;
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new java.net.InetSocketAddress(PORT));
            System.out.println("Server started! Waiting for players...");
            
            // Continuously accept new player connections
            while (true) {
                // Wait for Player 1 to connect
                Socket player1 = serverSocket.accept();
                System.out.println("Player 1 connected!");
                
                // Wait for Player 2 to connect
                Socket player2 = serverSocket.accept();
                System.out.println("Player 2 connected! Starting game...");
                
                // Start a new game session in a separate thread
                // This allows multiple games to run simultaneously
                new Thread(() -> {
                    try {
                        handleGame(player1, player2);
                    } catch (IOException e) {
                        System.out.println("Game error: " + e.getMessage());
                    }
                }).start();
            }
            
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
    
    /**
     * Handles a single game session between two players.
     * Acts as a relay — forwards each player's moves to the other.
     * Notifies the remaining player if the opponent disconnects.
     * Does not send DISCONNECT if the opponent resigned.
     *
     * @param player1 socket connection of Player 1
     * @param player2 socket connection of Player 2
     */
    private static void handleGame(Socket player1, Socket player2) throws IOException {
        
        // Set up output streams to send messages to each player
        PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
        
        // Assign player numbers
        out1.println("PLAYER:1");
        out2.println("PLAYER:2");
        
        // Set up input streams to receive messages from each player
        BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
        
        // Track if a player resigned (no need to send DISCONNECT in that case)
        boolean[] resigned = {false};

        // Thread to receive moves from Player 1 and forward to Player 2
        // If Player 1 disconnects, notify Player 2
        new Thread(() -> {
            try {
                String message;
                while ((message = in1.readLine()) != null) {
                    System.out.println("Player 1: " + message);
                    out2.println(message);
                    if (message.equals("MOVE:-4:-4:0")) {
                        resigned[0] = true;
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("Player 1 disconnected");
            }
            if (!resigned[0]) out2.println("DISCONNECT:");
        }).start();
        
        // Receive moves from Player 2 and forward to Player 1
        // If Player 2 disconnects, notify Player 1
        try {
            String message;
            while ((message = in2.readLine()) != null) {
                System.out.println("Player 2: " + message);
                out1.println(message);
                if (message.equals("MOVE:-4:-4:0")) {
                    resigned[0] = true;
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("Player 2 disconnected");
        }
        if (!resigned[0]) out1.println("DISCONNECT:");
    }
}