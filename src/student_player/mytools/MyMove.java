package student_player.mytools;

import hus.HusMove;

public class MyMove {
    public int score = 0;
    public HusMove move;

    public MyMove() {
        move = new HusMove();
    }

    public MyMove(int score) {
        this.move = new HusMove();
        this.score = score;
    }

    public MyMove(HusMove move, int score) {
        this.move = move;
        this.score = score;
    }
}
