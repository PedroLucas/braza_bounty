/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2012
 *  
 * 	To solve this problem only planning algorithms were used, and
 * a map model, constructed as the agent moves around. For the
 * map exploration, the agent uses a BFS to look for a near-by place
 * to explore, and then uses a simple a-star search to find a path
 * to that place. In the main routine we have a ordering of our
 * priorities, which is:
 * - Get near tools
 * - Explore map until there is no way to explore more
 * - Try to get gold
 * 
 * 	For the a-star search we have the class Cell, that represents
 * the state of the search in, which includes which walls were broken
 * during the path, number of bombs used, etc. In that way, the a-star avoids
 * going to the same tile if it doesn't have any new tools or improvements,
 * but in the case of new tools, the astar can visit that point again, allowing
 * a powerful planning ability. More information in the a-star's comment.
 *  
 * 
 */

import java.lang.Exception;
import java.util.*;
import java.io.*;
import java.net.*;

public class Agent {

   public char get_action( char view[][] ) {

      // REPLACE THIS CODE WITH AI TO CHOOSE ACTION

      int ch=0;

      System.out.print("Enter Action(s): ");

      try {
         while ( ch != -1 ) {
            // read character from keyboard
            ch  = System.in.read();

            switch( ch ) { // if character is a valid action, return it
               case 'F': case 'L': case 'R': case 'C': case 'B':
               case 'f': case 'l': case 'r': case 'c': case 'b': case 'W':case 'w':
                  return((char) ch );
            }
         }
      }
      catch (IOException e) {
         System.out.println ("IO error:" + e );
      }

      return 0;
   }
   
 

   void print_view( char view[][] )
   {
      int i,j;

      System.out.println("\n+-----+");
      for( i=0; i < 5; i++ ) {
         System.out.print("|");
         for( j=0; j < 5; j++ ) {
            if(( i == 2 )&&( j == 2 )) {
               System.out.print('^');
            }
            else {
               System.out.print( view[i][j] );
            }
         }
         System.out.println("|");
      }
      System.out.println("+-----+");
   }
   
   
   /* ************** My functions************* */
   
   
   /**
    * @param seenTools -List of tools seen by agent
    * @param planner -Planner objects of the agent
    * @return -Path to a tool in seenTools
    */
   static String getTool(LinkedList<Point> seenTools, Planner planner)
   {
	   	String tempPath = "";
	   	System.out.println("Going to get a tool!");
	   	while(tempPath == "" && seenTools.size() != 0)
	   	{
	   		Point pTool = seenTools.removeLast();
	   		tempPath = planner.getStringPath(planner.astar(pTool));
	   	}
	   	return tempPath;
   }
   
   /**
    *  Try to get gold, following a specific ordering of actions in a way to
    *  exhaust the options to get gold
    * @param mapm
    * @param planner
    * @return
    */
   
   static String tryGetGold(MapModel mapm, Planner planner)
   {
	   String goldPath;
	   LinkedList<Point> seenTools = mapm.getAllTools();
		goldPath = getTool(seenTools, planner);
		if(goldPath.equals("")){
			//Light search
	       	goldPath = planner.getStringPath(planner.astar(mapm.getGoldPos(), true,false));
	       	if(goldPath.equals(""))
	       	{
	       		//Is there somewhere we didn't explore?
	       		goldPath = planner.getStringPath(planner.explore());
	       		if(goldPath.equals(""))
	       			//Heavy search
	       			goldPath = planner.getStringPath(planner.astar(mapm.getGoldPos(), true,true) );
	       		if(goldPath.equals(""))
	       			System.exit(0); //"Impossible" map       		
	       		
	       	}
		}
		return goldPath;
   }
   
   
    
   /* ****************************************** */
   
   
   

   public static void main( String[] args )
   {
      InputStream in  = null;
      OutputStream out= null;
      Socket socket   = null;
      MapModel mapm = new MapModel();
      LinkedList<Point> seenTools = new LinkedList<Point>();
      Planner planner = new Planner(mapm);
      Agent  agent    = new Agent();
      char   view[][] = new char[5][5];
      char   action   = 'F';
      int port;
      int ch;
      int i,j;

      if( args.length < 2 ) {
         System.out.println("Usage: java Agent -p <port>\n");
         System.exit(-1);
      }

      port = Integer.parseInt( args[1] );

      try { // open socket to Game Engine
         socket = new Socket( "localhost", port );
         in  = socket.getInputStream();
         out = socket.getOutputStream();
      }
      catch( IOException e ) {
         System.out.println("Could not bind to port: "+port);
         System.exit(-1);
      }


      String goldPath = "";

      try { // scan 5-by-5 window around current location
         while( true ) {
            for( i=0; i < 5; i++ ) {
               for( j=0; j < 5; j++ ) {
                  if( !(( i == 2 )&&( j == 2 ))) {
                     ch = in.read();
                     if( ch == -1 ) {
                        System.exit(-1);
                     }
                     view[i][j] = (char) ch;
                  }
               }
            }
            action = 'c';
            mapm.updateMap( view );
            mapm.printMap();
            agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
            seenTools.addAll(mapm.seenToolsList());
            if(!goldPath.equals(""))
            {
               action = goldPath.charAt(0);
               goldPath = goldPath.substring(1);
               /*try{
                  Thread.sleep(100);
               }catch(Exception e){System.out.println("bunda");}*/
            }
            else if(mapm.hasGold())
            	goldPath = planner.goHome();
            else if(seenTools.size() != 0){
            	if((goldPath = getTool(seenTools, planner)).equals("")){
                   System.out.println("Foeee");
                   goldPath = planner.getStringPath(planner.explore());
		           if(goldPath.equals("") && mapm.sawGold())
		            {
                       goldPath = tryGetGold(mapm, planner);
		            }
            	}
            }
            else if( !(goldPath = planner.getStringPath(planner.explore()) ).equals("") )         	  
            	System.out.println("Exploring!" + goldPath);
            else if(mapm.sawGold())
            {
            	goldPath = tryGetGold(mapm, planner);
            }      
            mapm.doAction( action );
            out.write( action );
         }
      }
      catch( IOException e ) {
         System.out.println("Lost connection to port: "+ port );
         System.exit(-1);
      }
      finally {
    	  
         try {
            socket.close();
         }
         catch( IOException e ) {}
      }
   }
}
