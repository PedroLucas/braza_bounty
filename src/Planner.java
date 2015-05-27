import java.awt.*;
import java.lang.String;
import java.lang.System;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;


public class Planner{

    MapModel mapModel;
    public Planner(MapModel map){
        this.mapModel = map;
    }

    public Stack<Point> astar(Point p) {
        CellComparator cmp = new CellComparator();
        PriorityQueue<Cell> pq = new PriorityQueue<Cell>(20, cmp);
        Point pAgent = mapModel.agentPos();
        Cell destCel = null;
        pq.add(new Cell(pAgent, 0 + h(pAgent, p)));
        while (!pq.isEmpty()) {
            Cell cell = pq.poll();
            Point pc = cell.p;
            if (pc.equals(p)) {
                destCel = cell;
                break;
            }
            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 && j != 0) continue;
                    char ch = mapModel.map(pc.lin + i, pc.col + j);
                    if (ch != MapModel.WATER && ch != MapModel.WALL && ch != MapModel.TREE && ch != '?')
                    {
                        Point paux = new Point(pc.lin + i, pc.col + j);
                        pq.add(new Cell(paux, cell.cost + h(paux, p) + 1, cell));
                    }

                }
        }
        if (destCel == null) {
            System.out.println("It was not able to find a path!");
            return null;
        }

        Stack<Point> stAux, path;
        stAux = new Stack<Point>();
        path = new Stack<Point>();
        Cell auxCell = destCel;
        stAux.push(destCel.p);
        while (auxCell.parent != auxCell)
        {
            auxCell = auxCell.parent;
            stAux.push(auxCell.p);
        }
        while(!stAux.isEmpty()) path.push(stAux.pop());

        return path;



    }

    private int h(Point i, Point d)
    {
        //Manhattan distance
        int dx = Math.abs(i.col - d.col);
        int dy = Math.abs(i.lin - d.lin);
        return dx + dy;
    }



    private class CellComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {



            if (!(o1 instanceof Cell) || !(o2 instanceof Cell)) {
                return 0;
            }

            Cell c1 = (Cell) o1; Cell c2 = (Cell) o2;
            // descending order (ascending order would be:
            // o1.getGrade()-o2.getGrade())

            return c1.cost - c2.cost;
        }
    }

    private class Cell {
        public int cost;
        public Point p;
        public Cell parent;
        public Cell(Point p, int cost, Cell parent) {
            this.cost = cost;
            this.p = p;
            this.parent = parent;
        }

        public Cell(Point p, int cost)
        {
            this.cost = cost;
            this.p = p;
            this.parent = this;
        }

    }


    private int getOrientation(Point p1, Point p2)
    {
        int dLin = (p1.lin - p2.lin);
        int dCol = (p1.lin - p2.lin);
        if(dLin == 1)
            return MapModel.SOUTH;
        else if(dLin == -1)
            return MapModel.NORTH;
        else if(dCol == 1)
            return MapModel.EAST;
        else if(dCol == -1)
            return MapModel.WEST;

        System.out.println("Deu ruim heim");
        return MapModel.WEST;

    }

    private String getCommands(Point p1, Point p2, int orient)
    {
        int orient2;
        String cmd = "";
        orient2 = getOrientation(p1,p2);
        int difOrient = orient2 - orient;

        if(difOrient > 0)
        {
            if(difOrient <=2)
                for(int i = 0; i < difOrient; i++) cmd += "l";
            else cmd+= "r";
        }
        else
        {
            difOrient *= -1;
            if(difOrient <=2)
                for(int i = 0; i < difOrient; i++) cmd += "r";
            else cmd+= "l";
        }

        cmd += "f";

        return cmd;

    }

    public String getStringPath(Stack<Point> pathStk)
    {
        Point p1 = pathStk.pop();
        int orient = mapModel.getDir();
        String cmd = "";
        while(!pathStk.isEmpty()){
            Point p2 = pathStk.pop();
            cmd += getCommands(p1,p2, orient);
            orient = getOrientation(p1,p2);
            p1 = p2;
        }
        return cmd;
    }


}