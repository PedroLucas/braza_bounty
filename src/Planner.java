import java.awt.*;
import java.lang.String;
import java.lang.System;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.LinkedList;
import java.util.HashSet;


public class Planner{

    MapModel mapModel;
    public Planner(MapModel map){
        this.mapModel = map;
    }
    
    
    public LinkedList<Point> astar(Point p) {
    	return astar(p,false);
    }

    public LinkedList<Point> astar(Point p, boolean useBombs) {
        CellComparator cmp = new CellComparator(p);
        PriorityQueue<Cell> pq = new PriorityQueue<Cell>(20, cmp);
        HashSet<Cell> visited = new HashSet<Cell>();
        Point pAgent = mapModel.agentPos();
        Cell destCel = null;
        Cell aCell = new Cell(pAgent, 0);
        aCell.hasAxe = mapModel.hasAxe();
        aCell.iniBombs = mapModel.numberTNT();        		
        aCell.inBoat = mapModel.inBoat();
        aCell.boats.addAll(mapModel.boatsList());
        System.out.println("Starting bombs in A*:"+ aCell.bombs()  );
        pq.add(aCell);
        
        
        int count = 0;
        
        while (!pq.isEmpty()) {
            Cell cell = pq.poll();
            Point pc = cell.p;
            if (pc.equals(p)) {
                destCel = cell;
                break;
            }
            //visited.add(cell);
            boolean inBoatNow = cell.inBoat;
            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 && j != 0) continue;
                    Point paux = new Point(pc.lin + i, pc.col + j);
                    char ch = mapModel.map(pc.lin + i, pc.col + j);
                    if(ch == MapModel.BOAT && !cell.isBoat(paux))
                    	ch = MapModel.WATER;
                    else if(ch == MapModel.WATER && cell.isBoat(paux))
                    	ch = MapModel.BOAT;
                    
                    String forbiddenTerrains = "?"+ MapModel.END;
                    if(!useBombs)
                    	forbiddenTerrains += MapModel.WALL;
                    if(!cell.hasAxe)
                    	forbiddenTerrains += MapModel.TREE;
                    if(!inBoatNow)
                    	forbiddenTerrains += MapModel.WATER;
                    
                    
                    if (forbiddenTerrains.indexOf(ch) == -1) //Can we go there?
                    {
                        
                        Cell tempCell = new Cell(paux, cell.cost + 1, cell);
                        if(visited.contains(tempCell)) continue;
                        if(ch == MapModel.WALL && !tempCell.wallsDestroyed.contains(paux))
                        { 
                        	if(tempCell.bombs() > 0 /*&& !tempCell.wallsDestroyed.contains(paux)*/)
                        		tempCell.useBomb(paux);
                        	else continue;

                        }
                        else if(ch == MapModel.TNT && !tempCell.gotBomb(paux)) tempCell.getBomb(paux);
                        tempCell.inBoat = inBoatNow;
                        if(ch == MapModel.BOAT) tempCell.getInBoat(paux);
                        else if( ch != MapModel.WATER && inBoatNow) tempCell.getOutBoat(pc);                        
                        if(ch == MapModel.AXE) tempCell.hasAxe = true;
                        pq.add(tempCell);
                        visited.add(tempCell);
                        count++;
                    }

                }
        }
        System.out.println("A* states:"+count);
        
        if (destCel == null) {
            System.out.println("It was not able to find a path!");
            return null;
        }
        
        if(destCel.bombs() < 0)
        {
        	int bombs = destCel.bombs()*-1;
        	System.out.println("Path found needs additional bombs:" + bombs);
        	return null;
        }
        else System.out.println ("A* bombs left:" + destCel.bombs());

        Stack<Point> stAux;
        LinkedList<Point> path;
        stAux = new Stack<Point>();
        path = new LinkedList<Point>();
        Cell auxCell = destCel;
        System.out.println("=====A*=====");
        stAux.push(destCel.p);
        System.out.println(destCel.p.toString() + "Tipo:" + mapModel.map(destCel.p));
        while (auxCell.parent != auxCell)
        {
            auxCell = auxCell.parent;
            stAux.push(auxCell.p);
            System.out.println(auxCell.p.toString() + "Tipo:" + mapModel.map(auxCell.p));
        }
        System.out.println("============");
        while(!stAux.isEmpty()) path.add(stAux.pop());

        return path;



    }
    
    public String goHome()
    {
    	return getStringPath(astar(mapModel.home()));
    }
    
    
    private boolean addsVisibility(Point p)
    {
    	for(int i = -2; i <= 2; i++)
    		for(int j = -2; j <= 2; j++)
    				if(mapModel.map(p.lin + i, p.col + j) == '?')
    					return true;
    	return false;
    }
    
    public LinkedList<Point> explore() {
        LinkedList<Point> queue = new LinkedList<Point>();
        HashSet<Point> visited = new HashSet<Point>();
        Point pAgent = mapModel.agentPos();
        visited.add(pAgent);
        Point destP = null;
        queue.add(pAgent);
        while (!queue.isEmpty()) {
            Point pc = queue.remove();
            if (addsVisibility(pc)) {
            	//System.out.println("DESTINO:"+pc.toString()+" Tipo: " + mapModel.map(pc));
                destP = pc;
                break;
            }
            visited.add(pc);
            char currentTerrain = mapModel.map(pc);
            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 && j != 0) continue;
                    Point paux = new Point(pc.lin + i, pc.col + j);
                    if(visited.contains(paux)) continue;
                    String forbiddenTerrains = "?"+ MapModel.WALL+MapModel.END;
                    if(!mapModel.hasAxe())
                    	forbiddenTerrains += MapModel.TREE;
                    char ch = mapModel.map(paux);
                    if(currentTerrain != MapModel.WATER && currentTerrain != MapModel.BOAT)
                    	forbiddenTerrains += MapModel.WATER;
                    if (forbiddenTerrains.indexOf(ch) == -1) // Can we go there?
                    {
                    	//System.out.println(paux.toString()+" Tipo: " + ch);
                        queue.add(paux);
                    }

                }
        }
        if (destP == null) {
            System.out.println("It was not able to find more room to explore");
            return null;
        }

        
        return astar(destP);



    }
    
    
    

    private int h(Point i, Point d)
    {
        //Manhattan distance
        int dx = Math.abs(i.col - d.col);
        int dy = Math.abs(i.lin - d.lin);
        int dist = dx + dy;
        return dist;
    }


    
    private class CellComparator implements Comparator {
    	
    	Point p;
    	public CellComparator(Point p)
    	{
    		this.p = p;
    	}
    	
        @Override
        public int compare(Object o1, Object o2) {



            if (!(o1 instanceof Cell) || !(o2 instanceof Cell)) {
                return 0;
            }

            Cell c1 = (Cell) o1; Cell c2 = (Cell) o2;
            int w1 = c1.getTotalCost() + h(c1.p, p);
            int w2 = c2.getTotalCost() + h(c2.p, p);
            // descending order (ascending order would be:
            // o1.getGrade()-o2.getGrade())

            return w1 - w2;
        }
    }

    


    private int getOrientation(Point p1, Point p2)
    {
        int dLin = (p1.lin - p2.lin);
        int dCol = (p1.col - p2.col);
       // System.out.println("("+p1.lin+","+p1.col+")->("+p2.lin+","+p2.col+")");
        if(dLin == 1)
            return MapModel.NORTH;
        else if(dLin == -1)
            return MapModel.SOUTH;
        else if(dCol == 1)
            return MapModel.WEST;
        else if(dCol == -1)
            return MapModel.EAST;

        System.out.println("This should not have happened");
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
        
        if(mapModel.map(p2) == MapModel.TREE) cmd += 'c';
        if(mapModel.map(p2) == MapModel.WALL) cmd += 'b';

        cmd += "f";

        return cmd;

    }

    public String getStringPath(LinkedList<Point> pathStk)
    {
    	if(pathStk == null)
    		return "";
        Point p1 = pathStk.remove();
        int orient = mapModel.getDir();
        String cmd = "";
        while(!pathStk.isEmpty()){
            Point p2 = pathStk.remove();
            cmd += getCommands(p1,p2, orient);
            orient = getOrientation(p1,p2);
            p1 = p2;
        }
        return cmd;
    }


}