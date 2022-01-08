

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinimaxBot extends Player implements AI{

    private Point skipP;
    private int depth;

    public MinimaxBot(Side s, int depth)
    {
        super("MinimaxBot", s);
        this.depth = depth;
    }
    public Board.Decision makeMove(Board board)
    {
        Move move  = miniMove(board, depth, getSide(), true);
        Board.Decision decision = board.makeMove(move, getSide());
        if(decision == Board.Decision.ADDITIONAL_MOVE)
            skipP = move.getEnd();
        return decision;
    }
    private Side flip(Side side)
    {
        if(side == Side.BLACK)
            return Side.WHITE;
        return Side.BLACK;
    }
    private double heuristic(Board b)
    {
        double kingWeight = 1.5;
        double result = 0;
        if(getSide() == Side.WHITE)
            result = b.getnumWK() * kingWeight + b.getnumW() - b.getnumBK() *
                    kingWeight -
                    b.getnumB();
        else
            result = b.getnumBK() * kingWeight + b.getnumB() - b.getnumWK() *
                    kingWeight -
                    b.getnumW();
        return result;
    }
    private Move miniMove(Board board, int depth, Side side, boolean maxP)
    {
        List<Move> moves;
        //sets values of alpha and beta
        double alpha = -100000000000.0;
        double beta = 100000000000.0;
        if(skipP == null)
            //find all possible moves at given position
            moves = board.goodMoves(side);
        else
        {
            //if there was a skip, it will find all valid skip moves
            moves = board.getSkips(skipP.x, skipP.y, side);
            skipP = null;
        }
        //list of possible heuristics
        //if there are not possible moves, return null
        if(moves.isEmpty())
            return null;
        //temp board for looking ahead
        Board tboard = null;
        List<Double> heuristics = new ArrayList<>();
        //for every possible move, create temp board, make possible move, analyze heuristics
        for(int i = 0; i < moves.size(); i++)
        {
            tboard = board.clone();
            tboard.makeMove(moves.get(i), side);
            heuristics.add(minimax(tboard, depth - 1, flip(side), !maxP, alpha, beta));
        }
        //var to keep track of highest heuristic
        double max = -100000000.0;
        Random rand = new Random();
        //iterates through each heuristic, logs which one is the highest
        for(int i = heuristics.size() - 1; i >= 0; i--) {
            if (heuristics.get(i) >= max) {
                max = heuristics.get(i);
            }
        }
        //iterates through each heuristic, if heuristic is less than max,
        //it is removed from heuristics list and possible moves list,
        //leaving only the best possible move in both lists, unless multiple
        //moves have the same heuristic values
        for(int i = 0; i < heuristics.size(); i++)
        {
            if(heuristics.get(i) != max)
            {
                heuristics.remove(i);
                moves.remove(i);
                i-= 1;
            }
        }
        //returns best move, or a random move out of list of best possible moves
        return moves.get(rand.nextInt(moves.size()));
    }


    private double minimax(Board board, int depth, Side side, boolean maxP, double alpha, double beta)
    {
        if(depth == 0) {
            return heuristic(board);
        }
        //list of moves of all possible moves
        List<Move> moves = board.goodMoves(side);
        double x = 0;
        Board tempBoard = null;
        //ALPHA BETA PRUNING
        ///////////////////////////
        //if maximizing
        if(maxP)
        {
            x = -1000000000.0;
            for(Move m: moves)
            {
                tempBoard = board.clone();
                tempBoard.makeMove(m, side);
                double result = minimax(tempBoard, depth - 1, flip(side), !maxP, alpha, beta);
                x = Math.max(result, x);
                alpha = Math.max(alpha, x);
                if(alpha >= beta)
                    break;
            }
        }
        //minimizing
        else
        {
            x = 1000000000000.;
            for(Move m: moves)
            {
                tempBoard = board.clone();
                tempBoard.makeMove(m, side);
                double result = minimax(tempBoard, depth - 1, flip(side), !maxP, alpha, beta);
                x = Math.min(result, x);
                alpha = Math.min(alpha, x);
                if(alpha >= beta)
                    break;
            }
        }

        return x;
    }
}
