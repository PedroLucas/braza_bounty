/**
 * Representation of agent's state during the a-star search
 *
 */
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;



public class Cell {
	
	public static final int wallCost = 1;
	
	public int cost;
	public int iniBombs;
	public int bombsUsed;
	public boolean inBoat;
	public boolean hasAxe;
	public boolean useWallHistory;
	BitSet wallsDestroyedBits;
	int nWallsDestroyed;
	//HashSet<Point> wallsDestroyed;
	HashSet<Point> bombsCaught;
	HashSet <Point> boats;
	
	public Point p;
	public Cell parent;
	public Cell(Point p, int cost, Cell parent) {            
		this(p,cost);
		this.parent = parent;
		wallsDestroyedBits = (BitSet) parent.wallsDestroyedBits.clone();
		bombsCaught = (HashSet<Point>) parent.bombsCaught.clone();
		boats = (HashSet<Point>) parent.boats.clone();
		this.inBoat = parent.inBoat;
		this.hasAxe = parent.hasAxe;
		this.iniBombs = parent.iniBombs;
		this.bombsUsed = parent.bombsUsed;
		this.nWallsDestroyed = parent.nWallsDestroyed;
		this.useWallHistory = parent.useWallHistory;
		
	}
	
	public Cell(Point p, int cost)
	{
	    this.cost = cost;
	    this.p = p;
	    this.parent = this;
	    this.hasAxe = this.inBoat = false;
	    this.iniBombs = 0;
	    this.bombsUsed = 0;
	    
	    wallsDestroyedBits = new BitSet(6400);
	    wallsDestroyedBits.clear();
	    bombsCaught = new HashSet<Point>();
	    boats = new HashSet<Point>();
	    this.nWallsDestroyed = 0;
	    this.useWallHistory = false;
	}

	// Auxiliar function for comparation between cells
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
	        if(this.useWallHistory){
		        BitSet tempBits = (BitSet) wallsDestroyedBits.clone();
		        tempBits.xor(c.wallsDestroyedBits);
		        if(!tempBits.isEmpty()) return false;
	        }

	            
	        
	       
	        
	        
	        
	        return ( this.p.equals(c.p) &&
	        		(this.inBoat == c.inBoat) &&
	        		(this.hasAxe == c.hasAxe) &&
	        		(this.bombsUsed ==  c.bombsUsed) &&
	        		(this.bombs() == c.bombs()) );
	    }

	/**
	 * Simple override of hash function
	 * @return
	 */
	@Override
	public int hashCode() {
		int axe = hasAxe? 1 : 0;
		int boat = inBoat? 1 : 0;
	   
	    return ((51 + this.bombs())*51 + axe + boat )*51 + p.hashCode();// + wallsDestroyedBits.hashCode();
	}

	/**
	 * Register bomb explosion in a-star search
	 * @param p
	 */
	public void useBomb(Point p)
	{
		
		wallsDestroyedBits.set(p.lin*80 + p.col);
		nWallsDestroyed++;
		bombsUsed++;
	}

// ==================================================================
// Getters   ========================================================
// ==================================================================

	public int bombs()
	{
		return iniBombs - bombsUsed + bombsCaught.size();
	}
	
	public void destroyWall(Point p)
	{
		wallsDestroyedBits.set(p.lin*80 + p.col);
		nWallsDestroyed++;
	}
	
	public boolean isWallDestroyed(Point p)
	{
		return wallsDestroyedBits.get(p.lin*80 + p.col);
	}

	// Heuristic + Cost to sort elements for a-star search
	public int getTotalCost()
	{
		return (nWallsDestroyed - this.bombs())*wallCost + cost -bombsCaught.size()*100;
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