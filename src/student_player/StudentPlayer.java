package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import student_player.mytools.MyMove;
import student_player.mytools.MyTools;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {

    MyTools tools = null;

    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260585371"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
        long startTime = System.currentTimeMillis();

        if (tools == null)
            tools = new MyTools(player_id, opponent_id);

        tools.start_state = board_state;
        //tools.max_time = startTime + MyTools.NORMAL_TIME_LIMIT - MyTools.TIME_MARGIN;
        tools.setMaxTime(startTime);

        MyMove best = tools.findBest();

        long endTime = System.currentTimeMillis();
        //System.out.println("Total execution time: " + (endTime-startTime) + "ms, move#" + board_state.getTurnNumber());

        return best.move;
    }
}
