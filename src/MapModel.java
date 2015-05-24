
public class MapModel {

	
   final static int EAST   = 0;
   final static int NORTH  = 1;
   final static int WEST   = 2;
   final static int SOUTH  = 3;	
   
   private char[][] map;
   private int lin,col, dir;
   private int minLin, minCol, maxLin, maxCol;
   
   public MapModel()
   {
	   //Creates a totally unknown map
	   map = new char[160][160];
	   for(int i = 0; i < 160; i++)
		   for(int j = 0; j < 160; j++)
			   map[i][j] = '?'; 
	
	   //Our agent will start at the center of the map
	   lin = col = 79;
	   map[lin][col] = ' ';
	   
	   // Variable for print control
	   minLin = minCol = maxLin = maxCol = 79;
	   
	   
	   // We adopt the north as the start position direction
	   dir = NORTH;
   }
   
   private void setPos(int lin, int col, char ch)
   {
	   if(lin < 0 || col < 0) return;
	   if(lin >= 160 || col >= 160) return;
	   
	   if(lin < minLin) minLin = lin;
	   if(col < minCol) minCol = col;
	   if(lin > maxLin) maxLin = lin;
	   if(col > maxCol) maxCol = col;
	   
	   
	   map[lin][col] = ch;
   }
   
   
   
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
   
   /* Creates a copy of a square matrix */
   
   private char[][] copyMatrix(char[][] mat)
   {
	   int n = mat.length;
	   char[][] copy = new char[n][n];
	   for(int i = 0; i < n; i++)
		   for(int j = 0; j < n; j++)
			   copy[i][j] = mat[i][j];
	   
	   return copy;
   }
   
   
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
	   }
   }
   
   private void moveFoward()
   {
	   // It still needs to treat many cases that it actually can't move
	   switch(dir)
	   {
		   case NORTH: lin--; break;
	       case EAST:  col++; break;
	       case SOUTH: lin++; break;
	       case WEST:  col--; break;
	   }
   }
   
   
   

}
