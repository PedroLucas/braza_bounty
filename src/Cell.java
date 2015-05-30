import java.util.HashSet;
import java.util.Iterator;



public class Cell {
	
	public static final int wallCost = 1;
	
	public int cost;
	public int iniBombs;
	public int bombsUsed;
	public boolean inBoat;
	public boolean hasAxe;
	HashSet<Point> wallsDestroyed;
	HashSet<Point> bombsCaught;
	HashSet <Point> boats;
	
	public Point p;
	public Cell parent;
	public Cell(Point p, int cost, Cell parent) {            
		this(p,cost);
		this.parent = parent;
		wallsDestroyed = (HashSet<Point>) parent.wallsDestroyed.clone();
		bombsCaught = (HashSet<Point>) parent.bombsCaught.clone();
		boats = (HashSet<Point>) parent.boats.clone();
		this.inBoat = parent.inBoat;
		this.hasAxe = parent.hasAxe;
		this.iniBombs = parent.iniBombs;
		this.bombsUsed = parent.bombsUsed;
		
	}
	
	public Cell(Point p, int cost)
	{
	    this.cost = cost;
	    this.p = p;
	    this.parent = this;
	    this.hasAxe = this.inBoat = false;
	    this.iniBombs = 0;
	    this.bombsUsed = 0;
	    wallsDestroyed = new HashSet<Point>();
	    bombsCaught = new HashSet<Point>();
	    boats = new HashSet<Point>();
	}
	
	 @Override
	    public boolean equals(Object o) {
	 
	        // If the object is compared with itself then return true  
	        if (o == this) {
	            return true;
	        }
	        
	        if (!(o instanceof Cell)) {
	            return false;
	        }
	        
	        Cell c = (Cell) o;
	        if(this.wallsDestroyed.size() != c.wallsDestroyed.size()) return false;
	        else
	        {
	        	Iterator<Point> iterator = this.wallsDestroyed.iterator(); 
	        	while (iterator.hasNext())
	        		if(!c.wallsDestroyed.contains(iterator.next())) return false;	    	        
	        	
	        }

	        
	        
	        
	        // check values
	        
	        
	        
	        return ( this.p.equals(c.p) &&
	        		(this.inBoat == c.inBoat) &&
	        		(this.hasAxe == c.hasAxe) &&
	        		(this.bombsUsed ==  c.bombsUsed) &&
	        		(this.bombs() == c.bombs()) );
	    }
	
	@Override
	public int hashCode() {
		int axe = hasAxe? 1 : 0;
		int boat = inBoat? 1 : 0;
	   
	    return ((51 + this.bombs())*51 + axe + boat )*51 + p.hashCode();
	}
	
	public void useBomb(Point p)
	{
		wallsDestroyed.add(p);
		bombsUsed++;
	}
	
	public int bombs()
	{
		return iniBombs - bombsUsed + bombsCaught.size();
	}
	
	public void destroyWall(Point p)
	{
		wallsDestroyed.add(p);
	}
	
	public boolean isWallDestroyed(Point p)
	{
		return wallsDestroyed.contains(p);
	}
	
	public int getTotalCost()
	{
		return (wallsDestroyed.size() - this.bombs())*wallCost + cost;
	}
	
	public void getBomb(Point p)
	{
		bombsCaught.add(p);
	}
	
	public boolean gotBomb(Point p)
	{
		return bombsCaught.contains(p);
	}
	
	public void getInBoat(Point p)
	{
		inBoat = true;
		boats.remove(p);
	}
	
	public void getOutBoat(Point p)
	{
		inBoat = false;
		boats.add(p);
	}
	
	public boolean isBoat(Point p)
	{
		return boats.contains(p);
	}

}