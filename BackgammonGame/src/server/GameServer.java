/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.io.*;
import java.net.*;

public class GameServer {
    
    public static void main(String[] args){
        
        try{
            ServerSocket serversocket = new ServerSocket(6000);
            System.out.println("Server started! Waiting for players...");
            
            Socket player1 = serversocket.accept();
            System.out.println("Player 1 connected!");
            
            Socket player2 = serversocket.accept();
            System.out.println("Player 2 connected!");
            
            PrintWriter out1 = new PrintWriter(player1.getOutputStream(),true);
            PrintWriter out2 = new PrintWriter(player2.getOutputStream(), true);
            
            out1.println("Welcome Player 1!");
            out2.println("Welcome Player 2!");
            
            System.out.println("Game is ready!");
            
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }}