package student_player.mytools;

import hus.HusBoard;
import hus.HusBoardState;
import hus.HusMove;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

public class MyTools implements Runnable {

    public final static int ABSOLUTE_MAX_DEPTH = 100;
    public final static int WEIGHT_TOTAL_SEEDS = 1;
    public final static int WEIGHT_LEGAL_MOVES = 3;

    public final static boolean FEATURE_SORTING = true;
    public final static boolean FEATURE_AB_PRUNING = true;

    public MyMove best_shared = new MyMove();
    public Object best_lock = new Object();

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
    public MyMove minimax(HusBoardState state, int depth, int alphabeta) {
        if (depth == 0)
            return new MyMove(eval(state));
        else {
            ArrayList<HusMove> hus_moves = state.getLegalMoves();
            ArrayList<MyMove> my_moves = new ArrayList<>();

            if (hus_moves.size() == 0) // Cannot play
                return new MyMove(eval(state));

            for (HusMove move : hus_moves) {
                // Create a new MyMove and perform the move
                MyMove mymove = new MyMove(move,state);
                // Evaluate the board
                mymove.eval = eval(mymove.state);
                // Add it to our list for sorting
                my_moves.add(mymove);
            }
            // We maximize when it's our turn
            boolean maximize = state.getTurnPlayer() == self_id;

            if (FEATURE_SORTING) {
                if (maximize) // Highest board evaluations first
                    Collections.sort(my_moves, MyMove.descComparator /*Collections.reverseOrder(new MyMove.EvalComparator())*/);
                else // Lowest board evaluations first
                    Collections.sort(my_moves, MyMove.ascComparator);
            }


            //boolean initialized = false; // Store first move's value as best
            MyMove best = new MyMove(maximize ? Integer.MIN_VALUE : Integer.MAX_VALUE);

            for (MyMove move : my_moves) {
                MyMove result = minimax(move.state, depth - 1, best.score); // Get score
                move.score = result.score; // Bring the score up in the tree

                if (maximize && move.score > best.score
                        || !maximize && move.score < best.score)
                    best = move;

                // If we're minimizing and our current best is less than our parent's max so far
                // then we don't need to go further because our parent wants to maximize
                // and our best will inevitably be lower. The inverse is also true.
                if (FEATURE_AB_PRUNING &&
                        (!maximize && best.score < alphabeta
                      || maximize && best.score > alphabeta)
                      || Thread.currentThread().isInterrupted())
                    break;
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
            // Points for each seed that we have
            score += countTotalSeeds(state, self_id) * WEIGHT_TOTAL_SEEDS;
            // Points for each legal move that we have (more options is usually better)
            score += countLegalMoves(state, self_id) * WEIGHT_LEGAL_MOVES;
            // Points for each pit w/ < 2 seeds (moves they cannot make)
            score += (HusBoardState.BOARD_WIDTH - countLegalMoves(state, opponent_id)) * WEIGHT_LEGAL_MOVES;

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

    public long countLegalMoves(HusBoardState state, int player_id){
        /*int count = 0;
        for(int i = 0; i < 2 * HusBoardState.BOARD_WIDTH; i++){
            if(state.getPits()[player_id][i] >= 2)
                count ++;
        }
        return count;*/

        return Arrays.stream(state.getPits()[player_id])
                .filter(n -> n >= 2).count();
    }

    @Override
    public void run() {
        int depth = 3; // Start with depth 3, which should never take more than 2 seconds
        while (true) {

            // First starts by maximizing. Our "best" is just INT_MAX since we don't want it to think it is useless.
            MyMove best = minimax(start_state, depth, Integer.MAX_VALUE);


            if (Thread.currentThread().isInterrupted()
                    || depth >= ABSOLUTE_MAX_DEPTH
                    || best.score >= Integer.MAX_VALUE)
                break;
            else {
                synchronized (this.best_lock) {
                    this.best_shared = best;
                }
                depth++;
            }
        }
        //System.out.println("Explored to depth " + depth);
        //System.exit(0);
    }
}
