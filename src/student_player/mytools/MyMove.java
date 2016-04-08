package student_player.mytools;

import hus.HusBoardState;
import hus.HusMove;

import java.util.Collections;
import java.util.Comparator;

public class MyMove implements Comparable<MyMove> {
    public int score = 0;
    public int eval = 0;
    public HusMove move; // The move that lead to this.state
    public HusBoardState state; // The state resulting from this.move

    public boolean incomplete = false; // when search timed out.

    public static Comparator<MyMove> ascComparator = new EvalComparator();
    public static Comparator<MyMove> descComparator = Collections.reverseOrder(ascComparator);

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

    public MyMove(HusMove move, HusBoardState start_state) {
        this.move = move;

        this.state = (HusBoardState) start_state.clone();
        this.state.move(move);

        //this.eval =
    }

    @Override
    public String toString() {
        return move.toTransportable() + " (score=" + score + ")";
    }

    public String toRelativeString(int current_score) {
        return move.toTransportable() + " (score=" + (score-current_score) + ")";
    }

    @Override
    public int compareTo(MyMove o) {
        return new EvalComparator().compare(this, o);
    }

    public static class EvalComparator implements Comparator<MyMove> {
        @Override
        public int compare(MyMove o1, MyMove o2) {
            return Integer.compare(o1.eval, o2.eval);
        }
    }
}
