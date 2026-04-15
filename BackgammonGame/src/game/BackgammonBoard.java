/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game;

/**
 *
 * @author saadrady
 */
public class BackgammonBoard {
    
    
    private int[] points = new int[24];
    
    private int barPlayer1 = 0;
    private int barPlayer2 = 0;
    
    private int currentPlayer = 1;
    
    public BackgammonBoard() {
        setupBoard();
    }
    
    private void setupBoard() {
        // اللاعب 1 (موجب)
        points[23] = 2;   // مثلث 24
        points[12] = 5;   // مثلث 13
        points[7]  = 3;   // مثلث 8
        points[5]  = 5;   // مثلث 6
        
        // اللاعب 2 (سالب)
        points[0]  = -2;  // مثلث 1
        points[11] = -5;  // مثلث 12
        points[16] = -3;  // مثلث 17
        points[18] = -5;  // مثلث 19
    }
    
    
    // رمي النرد - بيرجع رقمين بين 1 و6
    public int[] rollDice() {
        int die1 = (int)(Math.random() * 6) + 1;
        int die2 = (int)(Math.random() * 6) + 1;
        return new int[]{die1, die2};
    }
    
    // بيرجع محتوى المثلث
    public int getPoint(int index) {
        return points[index];
    }
    
    // بيرجع دور مين
    public int getCurrentPlayer() {
        return currentPlayer;
    }
    
    // بيبدل الدور
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }
    
    // هل الحركة صح؟
public boolean isValidMove(int from, int to, int player) {
    
    
    // لو عند اللاعب قطعة على البار، لازم يدخلها أول
    if (player == 1 && barPlayer1 > 0) {
        return false;
    }
    if (player == 2 && barPlayer2 > 0) {
        return false;
    }
    
    // القطعة في المثلث لازم تكون بتاعت اللاعب
    if (player == 1 && points[from] <= 0) return false;
    if (player == 2 && points[from] >= 0) return false;
    
    // المثلث الوجهة - لو عند الخصم 2 أو أكتر = مقفول
    if (player == 1 && points[to] <= -2) return false;
    if (player == 2 && points[to] >= 2)  return false;
    
    return true;
}
// تحريك القطعة
public void movePiece(int from, int to, int player) {
    
    // شيل القطعة من المثلث القديم
    if (player == 1) {
        points[from]--;
    } else {
        points[from]++;
    }
    
    // لو في قطعة خصم وحيدة → اكلها!
    if (player == 1 && points[to] == -1) {
        points[to] = 0;
        barPlayer2++; // القطعة المأكولة تروح البار
    } else if (player == 2 && points[to] == 1) {
        points[to] = 0;
        barPlayer1++;
    }
    
    // حط القطعة في المثلث الجديد
    if (player == 1) {
        points[to]++;
    } else {
        points[to]--;
    }
}


}