
public class Point {
	
	public int lin, col;
	
	Point()
	{
		lin = col = 0;
	}
	
	Point(int lin, int col)
	{
		this.lin = lin;
		this.col = col;
	}
	
	// Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {
 
        // If the object is compared with itself then return true  
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof Point)) {
            return false;
        }

        Point p = (Point) o;
        return ( (this.lin == p.lin) &&
        		 (this.col == p.col) );
    }
    
    @Override
    public int hashCode() {
       
        return (97 + this.lin) * 97 + this.col;
    }
    
    @Override
    public String toString()
    {
    	return  "("+lin+","+col+")";
    }

    
    
	
	

}
