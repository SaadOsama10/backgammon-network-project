package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageGenerator {
    
    public static void main(String[] args) {
        try {
            for (int i = 0; i <= 5; i++) {
                generateTriangle("red", "white", i, false);
                generateTriangle("red", "black", i, false);
                generateTriangle("white", "white", i, false);
                generateTriangle("white", "black", i, false);
                generateTriangle("red", "white", i, true);
                generateTriangle("red", "black", i, true);
                generateTriangle("white", "white", i, true);
                generateTriangle("white", "black", i, true);
            }
            System.out.println("All 48 images done!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    static void generateTriangle(String triColor, String pieceColor, int pieces, boolean flipped) throws Exception {
        
        BufferedImage img = new BufferedImage(70, 250, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int[] xPoints = {0, 70, 35};
        int[] yPoints;
        
        if (flipped) {
            yPoints = new int[]{250, 250, 0};
        } else {
            yPoints = new int[]{0, 0, 250};
        }
        
        g.setColor(triColor.equals("red") ? new Color(198, 40, 40) : new Color(240, 240, 240));
        g.fillPolygon(xPoints, yPoints, 3);
        
        int pieceSize = 40;
        int x = (70 - pieceSize) / 2;
        
        for (int i = 0; i < pieces; i++) {
            int y;
            if (flipped) {
                y = 250 - pieceSize - 5 - (i * 42);
            } else {
                y = 5 + (i * 42);
            }
            
            g.setColor(pieceColor.equals("white") ? new Color(245, 245, 245) : new Color(30, 30, 30));
            g.fillOval(x, y, pieceSize, pieceSize);
            g.setColor(pieceColor.equals("white") ? new Color(180, 180, 180) : new Color(80, 80, 80));
            g.drawOval(x, y, pieceSize, pieceSize);
        }
        
        g.dispose();
        
        String flippedStr = flipped ? "_flipped" : "";
        String path = System.getProperty("user.home") + "/Desktop/backgammon-network-project/BackgammonGame/images/triangle_" + triColor + "_" + pieceColor + "_" + pieces + flippedStr + ".png";
        ImageIO.write(img, "PNG", new File(path));
        System.out.println("Saved: " + path);
    }
}