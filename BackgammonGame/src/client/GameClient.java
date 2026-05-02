/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.*;
import java.net.*;

/**
 *
 * @author saadrady
 */

public class GameClient {
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerNumber;
    private GamePanel gamePanel;
    
    public GameClient(String serverIP, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            socket = new Socket(serverIP, 6000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // استقبل رقم اللاعب
            String firstMessage = in.readLine();
            if (firstMessage.startsWith("PLAYER:")) {
                playerNumber = Integer.parseInt(firstMessage.split(":")[1]);
                System.out.println("You are Player " + playerNumber);
            }
            
            // Thread لاستقبال الحركات من السيرفر
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("MOVE:")) {
                            String[] parts = message.split(":");
                            int from = Integer.parseInt(parts[1]);
                            int to = Integer.parseInt(parts[2]);
                            gamePanel.applyOpponentMove(from, to);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server");
                }
            }).start();
            
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
    
    public void sendMove(int from, int to) {
        out.println("MOVE:" + from + ":" + to);
    }
    
    public int getPlayerNumber() {
        return playerNumber;
    }
}