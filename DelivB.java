import java.io.*;
import java.util.*;


// Class DelivB does the work for deliverable DelivB of the Prog340

public class DelivB {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	
	private static Node nodeArray[];
	private static int dp[][];
	private static int edgeArray[][];
	private static int nodeAmount;
	private static int tempArray[][];
	

	
	public DelivB( File in, Graph gr ) {
		inputFile = in;
		g = gr;
		
		// Get output file name.
		String inputFileName = inputFile.toString();
		String baseFileName = inputFileName.substring( 0, inputFileName.length()-4 ); // Strip off ".txt"
		String outputFileName = baseFileName.concat( "_out.txt" );
		outputFile = new File( outputFileName );
		if ( outputFile.exists() ) {    // For retests
			outputFile.delete();
		}
		
		try {
			output = new PrintWriter(outputFile);			
		}
		catch (Exception x ) { 
			System.err.format("Exception: %s%n", x);
			System.exit(0);
		}
		//System.out.println( "DelivB:  To be implemented");
		//output.println( "DelivB:  To be implemented");
		
		
		nodeAmount = g.getNodeList().size();
		nodeArray = new Node[nodeAmount];
		dp = new int [nodeAmount][nodeAmount];
		edgeArray = new int [nodeAmount][nodeAmount];
		HashMap<String, Integer> map = new HashMap<>();
		tempArray = new int[nodeAmount][nodeAmount];
		
		//add each node to an array
		int i = 0;
		boolean valNumeric = true;
		for(Node n : g.getNodeList()) {
			nodeArray[i] = n;
			i++;
			
			//check if the test file is DelivB test file (val = latitude)
			if(!isNumeric(n.getVal())){
				valNumeric = false;
			}
		}
		
		if(valNumeric) {
			//sort the nodes starting at the lowest val
			Arrays.sort(nodeArray);
			
			//map index to abbrev for new order of columns 
			for(int k = 0; k < nodeAmount; k++) {
				map.put(nodeArray[k].getAbbrev(),k);
			}
			
			//store each edge distance in the array[start node][destination node] 
			for(int j = 0; j < nodeAmount; j++) {
				for(Edge e : nodeArray[j].getIncomingEdges()) {
					edgeArray[j][map.get(e.getTail().getAbbrev())] = e.getDist();
				}
			}
			
			//remove start == destination distances and move forward the rest of the elements
			for(int y = 0; y < edgeArray.length; y++) {
				int q = 0;
				for(int z = 0; z < edgeArray.length; z++) {
					if(edgeArray[y][z] != 0) {
						tempArray[y][q] = edgeArray[z][y];
						q++;
					}
				}
			}
			edgeArray = tempArray;
			
			bitonicTSP(nodeAmount - 2);
			
		}else {
			System.out.println("\nThe bitonic tour requires latitude values. Please use test file containing latitude values.");
		}
		
	}
	
	//modified method from https://www.geeksforgeeks.org/bitonic-traveling-salesman-problem/
	public static int findTourDistance(int i, int j) {
		if(dp[i][j] > 0) {
			return dp[i][j];
		}
		dp[i][j] = Math.min(findTourDistance(i + 1, j) + edgeArray[i][i+1], findTourDistance(i + 1, i) + edgeArray[j][i+1]);
		return dp[i][j];
	}
	
	//modified method from https://www.geeksforgeeks.org/bitonic-traveling-salesman-problem/ 
	public void bitonicTSP(int N) {
		//base case
		for(int j = 0; j < N - 1; j++) {
			dp[N - 1][j] = edgeArray[N - 1][N] + edgeArray[j][N];
		}
		System.out.println("Shortest bitonic tour has distance " + findTourDistance(0, 0));
		System.out.println("Tour is ");
	}
	
	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
}
