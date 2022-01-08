
public class Main {

    public static double rounds = 100;
    public static void main(String[] args) throws InterruptedException {

        MinimaxBot one = new MinimaxBot(Player.Side.WHITE, 5);
        MinimaxBot two = new MinimaxBot(Player.Side.BLACK, 7);
        boolean turn = true;
        int bW = 0;
        int wW = 0;
        for(int t = 0; t< rounds; t++)
        {
            Board board = new Board();
            Player current = one;
            if(!turn)
                current = two;
            int c = 0;
            while (c < 1000)
            {
                c++;
                Board.Decision decision = null;
                decision = ((AI) current).makeMove(board);
                if(decision == Board.Decision.COMPLETED)
                {
                    if(board.getNumTB() == 0)
                    {
                        wW++;
                        break;
                    }
                    if(board.getNumTW() == 0)
                    {
                        bW++;
                        break;
                    }
                    if(turn)
                        current = two;
                    else
                        current = one;
                    turn = !turn;

                }
                else if(decision == Board.Decision.GAME_ENDED)
                {
                    if(current.getSide() == Player.Side.BLACK)
                    {
                        wW++;
                    }
                    else {
                        bW++;
                    }
                    break;
                }
            }
        }
        System.out.println("Black won " + bW + ", White won " + wW + "and total rounds: "  + rounds);

    }
}
