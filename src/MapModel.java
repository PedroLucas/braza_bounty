/**
* Class responsible for storage and maintenence of the environmental state
*
 */
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;


public class MapModel {
   final static int EAST   = 0;
   final static int NORTH  = 1;
   final static int WEST   = 2;
   final static int SOUTH  = 3;	
   
   final static char FLAT = ' ';
   final static char END = '.';
   final static char WATER = '~';
   final static char WALL = '*';
   final static char TREE = 'T';
   final static char AXE = 'a';
   final static char TNT = 'd';
   final static char BOAT = 'B';
   final static char GOLD = 'g';
   
   private char[][] map;
   private int lin,col, dir;
   private int minLin, minCol, maxLin, maxCol;
   private boolean inBoat;
   private boolean hasAxe;
   private boolean hasGold;
   private int numberTNT;
   private ArrayList<Point> listTNTs;
   private ArrayList<Point> listBoats;
   private ArrayList<Point> listAxes;
   private ArrayList<Point> listNewTools; // Points of new tools seen in last sight
   private ArrayList<Point> listSeenTools; // Points of tools seen in last sight
   Point goldPos;
   
   
/* ********* CONSTRUCTOR ************* */
   
   public MapModel()
   {
	   //Creates a totally unknown map
	   map = new char[160][160];
	   for(int i = 0; i < 160; i++)
		   for(int j = 0; j < 160; j++)
			   map[i][j] = '?'; 
	
	   //Our agent will start at the center of the map
	   lin = col = 79;
	   map[lin][col] = FLAT;
	   
	   // Variable for print control
	   minLin = minCol = maxLin = maxCol = 79;
	   
	   //It starts outside the boat
	   inBoat = false;
	   
	   //Starts with no tools
	   numberTNT = 0; hasAxe = false;
	   
	   //Initialize the lists of tools with empty lists
	   listTNTs = new ArrayList<Point>();
	   listBoats = new ArrayList<Point>();
	   listAxes = new ArrayList<Point>();
	   listNewTools = new ArrayList<Point>();
	   listSeenTools= new ArrayList<Point>();
	   goldPos = null;
	   
	   
	   // We adopt the north as the start position direction
	   dir = NORTH;
	   
	   
	   
   }
   
   /* ********** PUBLIC SETTERS AND GETTERS ************ */
   
   public char map(int l, int c)
   {
	   if(l > 159 || c > 159 || l < 0 || c < 0) return END;
	   return map[l][c];
   }
   
   public char map(Point p)
   {
	   
	   return this.map(p.lin, p.col);

   }
   
   public boolean hasAxe()
   {
	   return hasAxe;
   }
   
   public int numberTNT()
   {
	   return numberTNT;
   }
   
   public int minLinVisible()
   {
	   return minLin;
   }
   
   public int minColVisible()
   {
	   return minCol;
   }
   
   public int maxLinVisible()
   {
	   return maxLin;
   }
   
   public int maxColVisible()
   {
	   return maxCol;
   }

	public Point agentPos()
	{
		return new Point(this.lin,this.col);
	}

	public int getDir()
	{
		return dir;
	}

	public Point getGoldPos()
	{
		return goldPos;
	}
	
	public boolean sawGold()
	{
		return (goldPos != null);
	}
	public boolean hasGold()
	{
		return hasGold;
	}
	
	public boolean inBoat()
	{
		return inBoat;
	}
	
	public Point home()
	{
		return new Point(79,79);
	}
	
	public ArrayList<Point> newToolsList()
	{
		return listNewTools;
	}
	
	public ArrayList<Point> boatsList()
	{
		return listBoats;
	}
	
	public ArrayList<Point> seenToolsList()
	{
		return listSeenTools;
	}
	
	public LinkedList<Point> getAllTools()
	{
		LinkedList<Point> tools = new LinkedList<Point>();
		tools.addAll(this.listAxes);
		tools.addAll(this.listTNTs);
		
		
		return tools;
		
	}
   
   
   /*****************************************************/


	/**
	 * Add newly found objects in respective lists to further assistance in strategical model
	 * @param lin
	 * @param col
	 * @param ch
	 */
   private void setPos(int lin, int col, char ch)
   {
	   if(lin < 0 || col < 0) return;
	   if(lin >= 160 || col >= 160) return;
	   
	   if(lin < minLin) minLin = lin;
	   if(col < minCol) minCol = col;
	   if(lin > maxLin) maxLin = lin;
	   if(col > maxCol) maxCol = col;
	   
	   
	   
	   if(ch == AXE || ch == TNT)
		   listSeenTools.add(new Point(lin,col));
	   
	   
	   //If this position was unknown, we may have
	   //found something interesting (A tool or gold)
	   Point p;
	   if(map[lin][col] == '?')
	   {
		   switch(ch)
		   {
		   	case AXE:
		   		listAxes.add(p = new Point(lin,col));
		   		listNewTools.add(p);
		   		break;
		   	case TNT:
		   		listTNTs.add(p = new Point(lin,col));
		   		listNewTools.add(p);
		   		break;
		   	case BOAT:
		   		listBoats.add(new Point(lin,col));
		   		break;
		   	case GOLD:
		   		goldPos = new Point(lin,col);
		   		break;
		   		
		   }
	   }
	   
	   
	   map[lin][col] = ch;
   }


	/**
	 * Rotates matrix to standardization of the recorded map
	 * @param mat
	 * @return
	 */
   private char[][] rotateMatrixRight(char[][] mat)
   {
	   int n = mat.length;
	   char[][] rotated = new char[n][n];
	   for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++){
	            rotated[i][j] = mat[n - j - 1][i];
	        }
	    }
	   return rotated;
   }

	/**
	 * Analog to rotateMatrixRight
	 * @param mat
	 * @return
	 */
   private char[][] rotateMatrixLeft(char[][] mat)
   {
	   int n = mat.length;
	   char[][] rotated = new char[n][n];
	   for (int i = 0; i < n; i++) {
	        for (int j = 0; j < n; j++){
	            rotated[i][j] = mat[j][n - i - 1];
	        }
	    }
	   return rotated;
   }

	/**
	 * Creates a copy of a square matrix
	 *
	 * @param mat
	 * @return
	 */
   
   private char[][] copyMatrix(char[][] mat)
   {
	   int n = mat.length;
	   char[][] copy = new char[n][n];
	   for(int i = 0; i < n; i++)
		   for(int j = 0; j < n; j++)
			   copy[i][j] = mat[i][j];
	   
	   return copy;
   }

	/**
	 * Rotates the agent's field of view to update the recorded map
	 * @param view
	 */
   
   public void updateMap(char[][] view)
   {
	   switch(dir)
	   {
	   	case EAST:
	   		view = rotateMatrixRight(view);
	   	break;
	   	
	   	case WEST:
	   		view = rotateMatrixLeft(view);
	   	break;
	   	
	   	case SOUTH:
	   		view = rotateMatrixLeft(view);
	   		view = rotateMatrixLeft(view);
	   	break;
	   }
	   
	   listNewTools.clear();
	   listSeenTools.clear();
	   
	   for(int i = 0; i < 5; i++)
		   for(int j = 0; j < 5; j++)
			   if(i != 2 || j != 2) 
				   setPos(lin + i -2, col + j -2, view[i][j]);
   }


   public void printMap()
   {
	   for(int i = minLin; i <= maxLin; i++)
	   {
		   for(int j = minCol; j <= maxCol; j++)
			   if(i == lin && j ==  col)
			   {
				   char ch = 'E';
				   switch( dir ) {
	                case NORTH: ch = '^'; break;
	                case EAST:  ch = '>'; break;
	                case SOUTH: ch = 'v'; break;
	                case WEST:  ch = '<'; break;
	               }
				   System.out.print(ch);
			   }
			   else System.out.print(map[i][j]);
		   System.out.print('\n');
	   }
	   System.out.println("Direction:"+dir);
   }

	/**
	 * Updates map and number of TNT according to agent's decision
	 * @param ac
	 */
   public void doAction(char ac)
   {
	   switch(ac)
	   {
	   	case 'F':
	   	case 'f':
	   		
	   	moveFoward();
	   		
	   	break;
		case 'L':
		case 'l':
			
		dir = (dir+1)%4;
			
		break;
		case 'r': 
		case 'R':
		
		dir--;
		if(dir < 0) dir += 4;	
		
		break;
		
		case 'B':
		case 'b':
			if(numberTNT > 0) numberTNT--;
			break;
	   }
   }

	/**
	 * Updates the agent's state according to the tale it is going to.
	 */
   private void moveFoward()
   {
	   int nextLin = lin, nextCol = col;
	   switch(dir)
	   {
		   case NORTH: nextLin--; break;
	       case EAST:  nextCol++; break;
	       case SOUTH: nextLin++; break;
	       case WEST:  nextCol--; break;
	   }
	   char ch = map[nextLin][nextCol];
	   Point p = new Point(nextLin, nextCol);
	  // System.out.println("Going to "+p.toString()+" | Tipo:"+this.map(p));
	   if(ch == WALL || ch == TREE) return;
	  // if(ch == WATER && !inBoat) return;
	   if(ch != WALL && ch != TREE && ch != WATER && inBoat)
	   {
		   inBoat = false;
		   listBoats.add(new Point(lin,col));
	   }
	   else if(ch == BOAT && !inBoat)
	   {
		   inBoat = true;
		   listBoats.remove(new Point(nextLin,nextCol));
	   }
	   else if(ch == AXE)
	   {		   
		   hasAxe = true;
		   this.setPos(nextLin, nextCol, FLAT);
		   listAxes.remove(new Point(nextLin,nextCol));
	   }
	   else if(ch == TNT)
	   {
		   numberTNT++;
		   this.setPos(nextLin, nextCol, FLAT);
		   listTNTs.remove(new Point(nextLin,nextCol)); 
	   }
	   else if(ch == GOLD) hasGold = true;
		   
		   
		   
	   lin = nextLin; col = nextCol;
	   
	   
	   
   }
   
   
   
   

}
