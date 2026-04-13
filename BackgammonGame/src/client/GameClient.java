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
    public static void main(String[] args) {
        
        try {
            Socket socket = new Socket("localhost", 6000);
            System.out.println("Connected to server!");
            
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            
            String message = in.readLine();
            System.out.println("Server says: " + message);
            
            socket.close();
            
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
