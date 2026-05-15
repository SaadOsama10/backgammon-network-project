package client;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * ImageGenerator - Generates all PNG images used for the Backgammon board triangles.
 * Creates 48 images covering all combinations of:
 * - Triangle color: red or white
 * - Piece color: white or black
 * - Piece count: 0 to 5
 * - Orientation: normal (top) or flipped (bottom)
 */
public class ImageGenerator {
    
    /**
     * Entry point - generates all 48 triangle images and saves them to the images folder.
     */
    public static void main(String[] args) {
        try {
            // Generate all combinations for piece counts 0 to 5
            for (int i = 0; i <= 5; i++) {
                generateTriangle("red",   "white", i, false); // red triangle, white pieces, normal
                generateTriangle("red",   "black", i, false); // red triangle, black pieces, normal
                generateTriangle("white", "white", i, false); // white triangle, white pieces, normal
                generateTriangle("white", "black", i, false); // white triangle, black pieces, normal
                generateTriangle("red",   "white", i, true);  // red triangle, white pieces, flipped
                generateTriangle("red",   "black", i, true);  // red triangle, black pieces, flipped
                generateTriangle("white", "white", i, true);  // white triangle, white pieces, flipped
                generateTriangle("white", "black", i, true);  // white triangle, black pieces, flipped
            }
            System.out.println("All 48 images done!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Generates a single triangle PNG image with the specified properties.
     *
     * @param triColor   color of the triangle ("red" or "white")
     * @param pieceColor color of the pieces on the triangle ("white" or "black")
     * @param pieces     number of pieces to draw on the triangle (0-5)
     * @param flipped    if true, the triangle points upward (used for bottom triangles)
     */
    static void generateTriangle(String triColor, String pieceColor, int pieces, boolean flipped) throws Exception {
        
        // Create a 70x250 transparent image
        BufferedImage img = new BufferedImage(70, 250, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        
        // Enable anti-aliasing for smoother edges
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Define triangle vertices
        int[] xPoints = {0, 70, 35};
        int[] yPoints;
        
        // Flip the triangle vertically for bottom row points
        if (flipped) {
            yPoints = new int[]{250, 250, 0}; // points upward
        } else {
            yPoints = new int[]{0, 0, 250};   // points downward
        }
        
        // Fill the triangle with the appropriate color
        g.setColor(triColor.equals("red") ? new Color(198, 40, 40) : new Color(240, 240, 240));
        g.fillPolygon(xPoints, yPoints, 3);
        
        // Draw pieces on the triangle
        int pieceSize = 40;
        int x = (70 - pieceSize) / 2; // center the piece horizontally
        
        for (int i = 0; i < pieces; i++) {
            int y;
            // Stack pieces from the wide end toward the tip
            if (flipped) {
                y = 250 - pieceSize - 5 - (i * 42); // stack from bottom up
            } else {
                y = 5 + (i * 42); // stack from top down
            }
            
            // Fill the piece circle
            g.setColor(pieceColor.equals("white") ? new Color(245, 245, 245) : new Color(30, 30, 30));
            g.fillOval(x, y, pieceSize, pieceSize);
            
            // Draw piece border
            g.setColor(pieceColor.equals("white") ? new Color(180, 180, 180) : new Color(80, 80, 80));
            g.drawOval(x, y, pieceSize, pieceSize);
        }
        
        g.dispose();
        
        // Build the output file path and save the image
        String flippedStr = flipped ? "_flipped" : "";
        String path = System.getProperty("user.home") + "/Desktop/backgammon-network-project/BackgammonGame/images/triangle_"
                + triColor + "_" + pieceColor + "_" + pieces + flippedStr + ".png";
        ImageIO.write(img, "PNG", new File(path));
        System.out.println("Saved: " + path);
    }
}