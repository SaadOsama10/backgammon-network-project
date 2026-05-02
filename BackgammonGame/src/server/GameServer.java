/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.*;
import java.net.*;

/**
 *
 * @author saadrady
 */

public class GameServer {
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6000);
            System.out.println("Server started! Waiting for players...");
            
            Socket player1 = serverSocket.accept();
            System.out.println("Player 1 connected!");
            
            Socket player2 = serverSocket.accept();
            System.out.println("Player 2 connected!");
            
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(), true);
            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
            
            out1.println("PLAYER:1");
            out2.println("PLAYER:2");
            
            System.out.println("Game is ready!");
            
            // Thread للاعب 1 — يستقبل حركاته ويبعتها للاعب 2
            BufferedReader in1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));
            BufferedReader in2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));
            
            // Thread لاستقبال حركات اللاعب 1
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in1.readLine()) != null) {
                        System.out.println("Player 1: " + message);
                        out2.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Player 1 disconnected");
                }
            }).start();
            
            // Thread لاستقبال حركات اللاعب 2
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in2.readLine()) != null) {
                        System.out.println("Player 2: " + message);
                        out1.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Player 2 disconnected");
                }
            }).start();
            
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}