package student_player.mytools;

import hus.HusBoard;
import hus.HusBoardState;
import hus.HusMove;

import java.util.ArrayList;

public class MyTools {

    public static int TOTAL_SEEDS_WEIGHT = 2;
    public static int MAX_DEPTH = 5; // constant max depth for minimax

    private int self_id;
    private int opponent_id;

    public MyTools(int self_id, int opponent_id) {
        this.self_id = self_id;
        this.opponent_id = opponent_id;
    }

    public int eval(HusBoard board) {
        return eval((HusBoardState) board.getBoardState());
    }

    /* Returns the best move following the minimax algorithm
    @param state Initial state of the board
    @param current depth at which we are searching the tree
     */
    public MyMove minimax(HusBoardState state, int depth) {
        if (depth >= MAX_DEPTH)
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
                MyMove result = minimax(candidate_board, depth + 1); // Get score
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
}
