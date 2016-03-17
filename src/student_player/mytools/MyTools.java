package student_player.mytools;

import hus.HusBoard;
import hus.HusBoardState;
public class MyTools {

    public static int TOTAL_SEEDS_WEIGHT = 2;

    public int eval(HusBoard board) {
        return eval((HusBoardState) board.getBoardState());
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
