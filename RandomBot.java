
import java.util.List;
import java.util.Random;

public class RandomBot extends Player implements AI {
    public RandomBot(String name, Side s)
    {
        super(name, s);
    }
    public RandomBot(Side s)
    {
        super("RandomBot", s);
    }


    public Board.Decision makeMove(Board board)
    {
        Random r = new Random();
        //get list of all valid moves
        List<Move> moves = board.goodMoves(getSide());
        if(moves.size() == 0)
            return Board.Decision.GAME_ENDED;
        //returns a random move from the list of possible moves
        Move m = moves.get(r.nextInt(moves.size()));
        return board.makeMove(m, getSide());
    }

}
