

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Board {

    private Type[][] board;
    public final int SIZE = 8;

    private int numW;
    private int numB;
    private int numBK;
    private int numWK;

    private enum Type {
        EMPTY, WHITE, BLACK, WHITE_KING, BLACK_KING
    }

    public enum Decision {
        COMPLETED,
        FAILED_MOVING_INVALID_PIECE,
        FAILED_INVALID_DESTINATION,
        ADDITIONAL_MOVE,
        GAME_ENDED
    }


    //sets up board
    public Board() {
        setUpBoard();
    }

    public Board(Type[][] board)
    {
        numW = 0;
        numB = 0;
        numBK = 0;
        numWK = 0;

        this.board = board;
        for(int i = 0; i < SIZE; i++)
        {
            for(int j = 0; j< SIZE; j++)
            {
                Type piece = getPiece(i, j);
                if(piece == Type.BLACK)
                    numB++;
                else if(piece == Type.BLACK_KING)
                    numBK++;
                else if(piece == Type.WHITE)
                    numW++;
                else if(piece == Type.WHITE_KING)
                    numWK++;
            }
        }
    }

    //sets up beginning checker board
    private void setUpBoard() {
        numW = 12;
        numB = 12;
        numBK = 0;
        numWK = 0;
        board = new Type[SIZE][SIZE];
        for (int i = 0; i < board.length; i++) {
            int start = 0;
            if (i % 2 == 0)
                start = 1;

            Type pieceType = Type.EMPTY;
            if (i <= 2)
                pieceType = Type.WHITE;
            else if (i >= 5)
                pieceType = Type.BLACK;

            for (int j = start; j < board[i].length; j += 2) {
                board[i][j] = pieceType;
            }
        }
        populateEmptyOnBoard();
    }
    private void populateEmptyOnBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == null)
                    board[i][j] = Type.EMPTY;
            }
        }
    }
    public Type getPiece(int row, int col) {
        return board[row][col];
    }

    public Type getPiece(Point point) {
        return board[point.x][point.y];
    }

    public Type[][] getBoard()
    {
        return board;
    }

    public int getNumTW() {
        return numWK + numW;
    }

    public int getNumTB() {
        return numBK + numB;
    }

    public int getnumWK()
    {
        return numWK;
    }
    public int getnumBK()
    {
        return numBK;
    }
    public int getnumW()
    {
        return numW;
    }
    public int getnumB()
    {
        return numB;
    }

    // returns true if move successful
    public Decision makeMove(Move move, Player.Side side) {
        //if no possible moves available, game is over
        if(move == null) {
            return Decision.GAME_ENDED;
        }
        //get moves starting position
        Point start = move.getStart();
        int startRow = start.x;
        int startCol = start.y;
        //get moves ending position
        Point end = move.getEnd();
        int endRow = end.x;
        int endCol = end.y;
        //can't move an empty square or an opponent's piece
        if (!isMovingOwnPiece(startRow, startCol, side) || getPiece(startRow, startCol) == Type.EMPTY)
            return Decision.FAILED_MOVING_INVALID_PIECE;
        //get list of possible moves
        List<Move> moves = possMovess(startRow, startCol, side);
        //get Type of board piece
        Type currType = getPiece(startRow, startCol);
        if (moves.contains(move)) {
            boolean jumpMove = false;
            //if it contains move then it is either 1 move or 1 jump
            if (startRow + 1 == endRow || startRow - 1 == endRow) {
                board[startRow][startCol] = Type.EMPTY;
                board[endRow][endCol] = currType;
            } 
            //if move is more than one move, a jump is occurring
            else {
                jumpMove = true;
                board[startRow][startCol] = Type.EMPTY;
                board[endRow][endCol] = currType;
                //finding where the middle square was
                Point mid = findMidSquare(move);
                //find what type of piece was taken
                Type middle = getPiece(mid);
                if (middle == Type.BLACK)
                    numB--;
                else if(middle == Type.BLACK_KING)
                    numBK--;
                else if(middle == Type.WHITE)
                    numW--;
                else if(middle == Type.WHITE_KING)
                    numWK--;
                board[mid.x][mid.y] = Type.EMPTY;
            }
            //when black piece turns into a king
            if (endRow == 0 && side == Player.Side.BLACK) {
                board[endRow][endCol] = Type.BLACK_KING;
                numB--;
                numBK++;
            }
            //when white piece turns into a king
            else if (endRow == SIZE - 1 && side == Player.Side.WHITE) {
                board[endRow][endCol] = Type.WHITE_KING;
                numW--;
                numWK++;
            }
            //find any skip moves
            if (jumpMove) {
                List<Move> more = getSkips(endRow, endCol, side);
                //no possible skips
                if (more.isEmpty())
                    return Decision.COMPLETED;
                //another skip is possible
                return Decision.ADDITIONAL_MOVE;
            }
            return Decision.COMPLETED;
        } else
            return Decision.FAILED_INVALID_DESTINATION;
    }
    //get all possible moves for a player
    public List<Move> goodMoves(Player.Side side)
    {
        Type normal = null;
        Type king = null;
        if(side == Player.Side.BLACK){
            normal = Type.BLACK;
        }
        else{
            normal = Type.BLACK;
        }

        if(side == Player.Side.BLACK)
        {
            king = Type.BLACK_KING;
        }
        else
        {
            king = Type.WHITE_KING;
        }
        List<Move> moves = new ArrayList<>();
        for(int i = 0; i < SIZE; i++)
        {
            for(int j = 0; j < SIZE; j++)
            {
                Type t = getPiece(i, j);
                if(t == normal || t == king)
                    moves.addAll(possMovess(i, j, side));
            }
        }
        return moves;
    }
    // requires there to actually be a mid square
    private Point findMidSquare(Move move) {

        Point mid = new Point((move.getStart().x + move.getEnd().x) / 2,
                (move.getStart().y + move.getEnd().y) / 2);

        return mid;
    }

    private boolean isMovingOwnPiece(int row, int col, Player.Side side) {
        Type pieceType = getPiece(row, col);
        if (side == Player.Side.BLACK && pieceType != Type.BLACK && pieceType != Type.BLACK_KING)
            return false;
        else if (side == Player.Side.WHITE && pieceType != Type.WHITE && pieceType != Type.WHITE_KING)
            return false;
        return true;
    }

    public List<Move> possMovess(int row, int col, Player.Side side) {
        Type type = board[row][col];
        Point startPoint = new Point(row, col);
        if (type == Type.EMPTY)
            throw new IllegalArgumentException();
        List<Move> moves = new ArrayList<>();
        //4 possible moves, 2 if not king
        if (type == Type.WHITE || type == Type.BLACK) {
            //2 possible moves
            int rowChange = 0;
            if(type == Type.WHITE)
            {
                rowChange = 1;
            }
            else{
                rowChange = -1;
            }
            int newRow = row + rowChange;
            if (newRow >= 0 || newRow < SIZE) {
                int newCol = col + 1;
                if (newCol < SIZE && getPiece(newRow, newCol) == Type.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
                newCol = col - 1;
                if (newCol >= 0 && getPiece(newRow, newCol) == Type.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
            }
        }
        else {
            int newRow = row + 1;
            if (newRow < SIZE) {
                int newCol = col + 1;
                if (newCol < SIZE && getPiece(newRow, newCol) == Type.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
                newCol = col - 1;
                if (newCol >= 0 && getPiece(newRow, newCol) == Type.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
            }
            newRow = row - 1;
            if (newRow >= 0) {
                int newCol = col + 1;
                if (newCol < SIZE && getPiece(newRow, newCol) == Type.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
                newCol = col - 1;
                if (newCol >= 0 && getPiece(newRow, newCol) == Type.EMPTY)
                    moves.add(new Move(startPoint, new Point(newRow, newCol)));
            }


        }
        moves.addAll(getSkips(row, col, side));
        return moves;
    }

    public List<Move> getSkips(int row, int col, Player.Side side) {
        List<Move> move = new ArrayList<>();
        Point start = new Point(row, col);
        List<Point> skips = new ArrayList<>();
        if(side == Player.Side.WHITE && getPiece(row, col) == Type.WHITE)
        {
            skips.add(new Point(row + 2, col + 2));
            skips.add(new Point(row + 2, col - 2));
        }
        else if(side == Player.Side.BLACK && getPiece(row, col) == Type.BLACK)
        {
            skips.add(new Point(row - 2, col + 2));
            skips.add(new Point(row - 2, col - 2));
        }
        else if(getPiece(row, col) == Type.BLACK_KING || getPiece(row, col) == Type.WHITE_KING)
        {
            skips.add(new Point(row + 2, col + 2));
            skips.add(new Point(row + 2, col - 2));
            skips.add(new Point(row - 2, col + 2));
            skips.add(new Point(row - 2, col - 2));
        }

        for (int i = 0; i < skips.size(); i++) {
            Point temp = skips.get(i);
            Move m = new Move(start, temp);
            if (temp.x < SIZE && temp.x >= 0 && temp.y < SIZE && temp.y >= 0 && getPiece(temp.x, temp.y) == Type.EMPTY
                    && isOpponentPiece(side, getPiece(findMidSquare(m)))) {
                move.add(m);
            }
        }
        return move;
    }

    // return true if the piece is opponents
    private boolean isOpponentPiece(Player.Side current, Type opponentPiece) {
        if (current == Player.Side.BLACK && (opponentPiece == Type.WHITE || opponentPiece == Type.WHITE_KING))
            return true;
        if (current == Player.Side.WHITE && (opponentPiece == Type.BLACK || opponentPiece == Type.BLACK_KING))
            return true;
        return false;
    }
    public Board clone()
    {
        Type[][] newBoard = new Type[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++)
        {
            for(int j = 0; j< SIZE; j++)
            {
                newBoard[i][j] = board[i][j];
            }
        }
        Board b = new Board(newBoard);
        return b;
    }
}
