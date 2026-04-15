/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageGenerator {
    
    public static void main(String[] args) {
        try {
            for (int i = 0; i <= 5; i++) {
                generateTriangle("red", "white", i);
                generateTriangle("red", "black", i);
                generateTriangle("white", "white", i);
                generateTriangle("white", "black", i);
            }
            System.out.println("All 24 images done!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    static void generateTriangle(String triColor, String pieceColor, int pieces) throws Exception {
        
        BufferedImage img = new BufferedImage(55, 250, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // ارسم المثلث
        int[] xPoints = {0, 55, 27};
        int[] yPoints = {0, 0, 250};
        g.setColor(triColor.equals("red") ? new Color(198, 40, 40) : new Color(240, 240, 240));
        g.fillPolygon(xPoints, yPoints, 3);
        
        // ارسم القطع
        int pieceSize = 38;
        int x = (55 - pieceSize) / 2;
        
        for (int i = 0; i < pieces; i++) {
            // لون القطعة
            g.setColor(pieceColor.equals("white") ? new Color(245, 245, 245) : new Color(30, 30, 30));
            g.fillOval(x, 5 + (i * 44), pieceSize, pieceSize);
            
            // حدود القطعة
            g.setColor(pieceColor.equals("white") ? new Color(180, 180, 180) : new Color(80, 80, 80));
            g.drawOval(x, 5 + (i * 44), pieceSize, pieceSize);
        }
        
        g.dispose();
        
        String path = System.getProperty("user.home") + "/Desktop/backgammon-network-project/BackgammonGame/images/triangle_" + triColor + "_" + pieceColor + "_" + pieces + ".png";
        ImageIO.write(img, "PNG", new File(path));
        System.out.println("Saved: " + path);
    }
}