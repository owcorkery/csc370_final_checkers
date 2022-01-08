

import java.util.List;

public class CenterBot extends Player implements AI {
    public CenterBot(String name, Side s)
    {
        super(name, s);
    }
    public CenterBot(Side s)
    {
        super("CenterBot", s);
    }
    public Board.Decision makeMove(Board board)
    {
        List<Move> moves = board.goodMoves(getSide());
        if(moves.size() == 0)
            return Board.Decision.GAME_ENDED;
        int smallest = 1000;
        Move m = null;
        for(int i = 0; i < moves.size(); i++)
        {
            int x = Math.abs(3 - moves.get(i).getEnd().x);
            int y = Math.abs(3 - moves.get(i).getEnd().y);
            int close = x + y;
            if(close < smallest)
            {
                smallest = close;
                m = moves.get(i);
            }
        }

        return board.makeMove(m, getSide());
    }

}
