

import java.awt.*;

public class Move {

    private Point start;
    private Point end;

    public Move(int startRow, int startCol, int endRow, int endCol)
    {
        start = new Point(startRow, startCol);
        end = new Point(endRow, endCol);
    }

    public Move(Point start, Point end)
    {
        this.start = start;
        this.end = end;
    }

    public Point getStart()
    {
        return start;
    }

    public Point getEnd()
    {
        return end;
    }

    public String toString()
    {
        return "Start: " + start.x + ", " + start.y + " End: " + end.x + ", " + end.y;
    }


}
