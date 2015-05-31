/*********************************************
 *  Agent.java 
 *  Sample Agent for Text-Based Adventure Game
 *  COMP3411 Artificial Intelligence
 *  UNSW Session 1, 2012
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
   
   
   
   static String getTool(LinkedList<Point> seenTools, Planner planner)
   {
	   	String tempPath = "";
	   	System.out.println("Going to get a tool!");
	   	while(tempPath == "" && seenTools.size() != 0)
	   	{
	   		System.out.println("Penis");
	   		Point pTool = seenTools.removeLast();
	   		tempPath = planner.getStringPath(planner.astar(pTool));
	   		System.out.println("Fim--------------------Penis");
	   	}
	   	return tempPath;
   }
   
   
   static String tryGetGold(MapModel mapm, Planner planner)
   {
	   String goldPath;
	   LinkedList<Point> seenTools = mapm.getAllTools();
		goldPath = getTool(seenTools, planner);
		if(goldPath.equals("")){
	       	goldPath = planner.getStringPath(planner.astar(mapm.getGoldPos(), true,false));
	       	if(goldPath.equals(""))
	       	{
	       		
	       		goldPath = planner.getStringPath(planner.explore());
	       		if(goldPath.equals(""))
	       			goldPath = planner.getStringPath(planner.astar(mapm.getGoldPos(), true,true) );
	       		if(goldPath.equals(""))
	       		{System.out.println("Impossible map");
	       		System.exit(0);}            		
	       		
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
		            if( !(goldPath = planner.getStringPath(planner.explore()) ).equals("") )         	  
		            	System.out.println("Exploring!" + goldPath);
		            else if(mapm.sawGold())
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
