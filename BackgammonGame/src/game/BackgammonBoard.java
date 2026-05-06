/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

/**
 * BackgammonBoard - Represents the game logic and state of the Backgammon board.
 * The board is stored as an array of 24 points (triangles).
 * Positive values = Player 1 (white) pieces, Negative values = Player 2 (black) pieces.
 */
public class BackgammonBoard {
    
    // 24 points on the board (index 0 = point 1, index 23 = point 24)
    private int[] points = new int[24];
    
    // Number of pieces each player has on the Bar (eaten pieces)
    private int barPlayer1 = 0;
    private int barPlayer2 = 0;
    
    // Tracks whose turn it is (1 = white, 2 = black)
    private int currentPlayer = 1;
    
    /**
     * Constructor - initializes the board with the standard Backgammon starting position.
     */
    public BackgammonBoard() {
        setupBoard();
    }
    
    /**
     * Sets up the initial piece positions for both players.
     * Player 1 (positive) moves from point 24 toward point 1.
     * Player 2 (negative) moves from point 1 toward point 24.
     */
    private void setupBoard() {
        // Player 1 starting positions
        points[23] = 2;   // 2 pieces on point 24
        points[12] = 5;   // 5 pieces on point 13
        points[7]  = 3;   // 3 pieces on point 8
        points[5]  = 5;   // 5 pieces on point 6
        
        // Player 2 starting positions
        points[0]  = -2;  // 2 pieces on point 1
        points[11] = -5;  // 5 pieces on point 12
        points[16] = -3;  // 3 pieces on point 17
        points[18] = -5;  // 5 pieces on point 19
    }
    
    /**
     * Rolls two dice and returns random values between 1 and 6.
     * @return array of two dice values
     */
    public int[] rollDice() {
        int die1 = (int)(Math.random() * 6) + 1;
        int die2 = (int)(Math.random() * 6) + 1;
        return new int[]{die1, die2};
    }
    
    /**
     * Returns the value at a given point index.
     * Positive = Player 1 pieces, Negative = Player 2 pieces, 0 = empty.
     */
    public int getPoint(int index) {
        return points[index];
    }
    
    /**
     * Returns the current player number (1 or 2).
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    /**
     * Switches the turn to the other player.
     */
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }
    
    /**
     * Checks if a move from one point to another is valid.
     * Rules checked: bar priority, piece ownership, blocking, and capacity.
     */
    public boolean isValidMove(int from, int to, int player) {
        
        // Player must re-enter bar pieces before making any other move
        if (player == 1 && barPlayer1 > 0) return false;
        if (player == 2 && barPlayer2 > 0) return false;
        
        // The piece at 'from' must belong to the current player
        if (player == 1 && points[from] <= 0) return false;
        if (player == 2 && points[from] >= 0) return false;
        
        // Destination is blocked if opponent has 2 or more pieces there
        if (player == 1 && points[to] <= -2) return false;
        if (player == 2 && points[to] >= 2)  return false;
        
        // Destination cannot exceed 5 pieces (maximum per point)
        if (player == 1 && points[to] >= 5) return false;
        if (player == 2 && points[to] <= -5) return false;
        
        return true;
    }
    
    /**
     * Moves a piece from one point to another.
     * If the destination has a single opponent piece (blot), it gets eaten and sent to the Bar.
     */
    public void movePiece(int from, int to, int player) {
        
        // Remove piece from source point
        if (player == 1) {
            points[from]--;
        } else {
            points[from]++;
        }
        
        // Check if destination has a single opponent piece (hit/eat it)
        if (player == 1 && points[to] == -1) {
            points[to] = 0;
            barPlayer2++; // Send opponent's piece to the Bar
        } else if (player == 2 && points[to] == 1) {
            points[to] = 0;
            barPlayer1++;
        }
        
        // Place piece at destination point
        if (player == 1) {
            points[to]++;
        } else {
            points[to]--;
        }
    }
    
    /** Returns the number of Player 1's pieces on the Bar. */
    public int getBarPlayer1() {
        return barPlayer1;
    }
    
    /** Returns the number of Player 2's pieces on the Bar. */
    public int getBarPlayer2() {
        return barPlayer2;
    }
    
    /**
     * Re-enters a piece from the Bar onto the board.
     * Player 1 must enter into points 19-24, Player 2 into points 1-6.
     * If destination has a single opponent piece, it gets eaten.
     */
    public void enterFromBar(int to, int player) {
        
        // Hit opponent's blot if present
        if (player == 1 && points[to] == -1) {
            points[to] = 0;
            barPlayer2++;
        } else if (player == 2 && points[to] == 1) {
            points[to] = 0;
            barPlayer1++;
        }
        
        // Place piece and reduce bar count
        if (player == 1) {
            points[to]++;
            barPlayer1--;
        } else {
            points[to]--;
            barPlayer2--;
        }
    }
    
    /**
     * Checks if the player is allowed to bear off (remove pieces from the board).
     * All pieces must be in the home board: Player 1 in points 1-6, Player 2 in points 19-24.
     */
    public boolean canBearOff(int player) {
        if (player == 1) {
            for (int i = 6; i < 24; i++) {
                if (points[i] > 0) return false;
            }
        } else {
            for (int i = 0; i < 18; i++) {
                if (points[i] < 0) return false;
            }
        }
        return true;
    }
    
    /**
     * Removes a piece from the board (bearing off).
     * @param from the point index to remove the piece from
     */
    public void bearOff(int from, int player) {
        if (player == 1) {
            points[from]--;
        } else {
            points[from]++;
        }
    }
    
    /**
     * Checks if the player has won (all 15 pieces borne off).
     */
    public boolean hasWon(int player) {
        if (player == 1) {
            for (int i = 0; i < 24; i++) {
                if (points[i] > 0) return false;
            }
            return barPlayer1 == 0;
        } else {
            for (int i = 0; i < 24; i++) {
                if (points[i] < 0) return false;
            }
            return barPlayer2 == 0;
        }
    }
    
    /**
     * Checks if a player can enter a piece from the Bar at a given point.
     * Returns false if the point is blocked by 2 or more opponent pieces.
     */
    public boolean canEnterFromBar(int to, int player) {
        if (player == 1 && points[to] <= -2) return false;
        if (player == 2 && points[to] >= 2) return false;
        return true;
    }
}