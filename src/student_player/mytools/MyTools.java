package student_player.mytools;

import hus.HusBoard;
import hus.HusBoardState;
import hus.HusMove;

import java.util.ArrayList;

public class MyTools implements Runnable {

    public static int TOTAL_SEEDS_WEIGHT = 2;

    public MyMove best_move = new MyMove();
    public HusBoardState start_state;

    private int self_id;
    private int opponent_id;

    public MyTools(int self_id, int opponent_id, HusBoardState state) {
        this.self_id = self_id;
        this.opponent_id = opponent_id;
        this.start_state = state;
    }

    public int eval(HusBoard board) {
        return eval((HusBoardState) board.getBoardState());
    }

    /* Returns the best move following the minimax algorithm
    @param state Initial state of the board
    @param current depth at which we are searching the tree
     */
    public MyMove minimax(HusBoardState state, int depth) {
        if (depth == 0)
            return new MyMove(eval(state));
        else {
            ArrayList<HusMove> moves = state.getLegalMoves();

            if (moves.size() == 0) // Cannot play
                return new MyMove(eval(state));

            // We maximize when it's our turn
            boolean maximize = state.getTurnPlayer() == self_id;
            boolean initialized = false; // Store first move's value as best
            MyMove best = null;

            for (HusMove move : moves) {
                HusBoardState candidate_board = (HusBoardState) state.clone();
                candidate_board.move(move);
                MyMove result = minimax(candidate_board, depth - 1); // Get score
                result.move = move; // Store the move

                if (!initialized
                    || maximize && result.score > best.score
                    || !maximize && result.score < best.score) {
                    initialized = true;
                    best = result;
                }
            }
            return best;
        }
    }

    /* Evaluate the board's likeliness to lead to a win for player #self_id */
    public int eval(HusBoardState state) {

        if (state.getWinner() == self_id) // self win!
            return Integer.MAX_VALUE;
        else if (state.getWinner() == opponent_id // We lose
                || state.getWinner() == HusBoardState.CANCELLED0 && self_id == 0 // self cancelled the game
                || state.getWinner() == HusBoardState.CANCELLED1 && self_id == 1) // due to infinite move
            return Integer.MIN_VALUE;
        else { // Includes the case where opponent cancelled the game
            int score = 0;
            score += countTotalSeeds(state, self_id) * 2;
            return score;
        }
    }

    public static int countTotalSeeds(HusBoardState state, int player) {
        //int[][] board = ;
        int sum = 0;
        for (int i=0; i<2*HusBoardState.BOARD_WIDTH; i++)
            sum += state.getPits()[player][i];
        return sum;
    }

    @Override
    public void run() {
        int depth = 3; // Start with depth 3, which should never take more than 2 seconds
        while (! Thread.currentThread().isInterrupted()) {

            MyMove best = minimax(start_state, depth);

            if (! Thread.currentThread().isInterrupted())
                synchronized (this.best_move) {
                    this.best_move = best;
                }
            System.out.println("Explored to depth " + depth);

            depth ++;
        }
        //System.exit(0);
    }
}
