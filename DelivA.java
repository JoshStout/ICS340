import java.io.*;

// Class DelivA does the work for deliverable DelivA of the Prog340

public class DelivA {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivA( File in, Graph gr ) {
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
		
		//System.out.println( "DelivA:  To be implemented");
		//output.println( "DelivA:  To be implemented");
		
		// Report the graph name by only printing the last three characters from baseFileName String.
		String graphName = baseFileName;
		if(baseFileName.length() > 3) {
			graphName = baseFileName.substring(baseFileName.length() - 3);
		}
		System.out.println("Graph " + graphName + ":");
		output.println("Graph " + graphName + ":");
		System.out.println();
		output.println();
		
		
		// Report how many Node are in the graph using the ArrayList size() method. 
		System.out.println("There are " + g.getNodeList().size() + " nodes in the graph\n");
		output.println("There are " + g.getNodeList().size() + " nodes in the graph\n");
		// Report how many Edges are in the graph using the ArrayList size() method. 
		System.out.println("There are " + g.getEdgeList().size() + " edges in the graph\n");
		output.println("There are " + g.getEdgeList().size() + " edges in the graph\n");
		
		// List each outgoing Edge for each Node
		for(Node n : g.getNodeList()) { // Iterate thru each Node in the ArrayList 
			System.out.print("There are outgoing edges from Node " + n.getAbbrev());
			output.print("There are outgoing edges from Node " + n.getAbbrev());
			System.out.print(" to nodes ");
			output.print(" to nodes ");
			int commaCounter = 0;
			for(Edge e : n.getOutgoingEdges()) { // Iterate thru each outgoing Edge
				System.out.print(e.getHead().getAbbrev());
				output.print(e.getHead().getAbbrev());
				if(commaCounter < n.getOutgoingEdges().size() - 1) {
					System.out.print(", ");
					output.print(", ");
					commaCounter++;
				}else {
					System.out.print(".");
					output.print(".");
				}
			}
			System.out.println();
			output.println();
		}
		System.out.println();
		output.println();
		
		// List each incoming Edge for each Node
		for(Node n : g.getNodeList()) { // Iterate thru each Node in the ArrayList
			System.out.print("There are incoming edges to Node " + n.getAbbrev());
			output.print("There are incoming edges to Node " + n.getAbbrev());
			System.out.print(" to nodes ");
			output.print(" to nodes ");
			int commaCounter = 0;
			for(Edge e : n.getIncomingEdges()) { // Iterate thru each incoming Edge
				System.out.print(e.getTail().getAbbrev());
				output.print(e.getTail().getAbbrev());
				if(commaCounter < n.getIncomingEdges().size() - 1) {
					System.out.print(", ");
					output.print(", ");
					commaCounter++;
				}else {
					System.out.print(".");
					output.print(".");
				}
			}
			System.out.println();
			output.println();
		}
		System.out.println();
		output.println();
		
		// Report the mean Edge length for all Edges in the graph.
		int sum = 0;
		int count = 0;
		for(Edge e : g.getEdgeList()) {
			sum += e.getDist();
			count++;
		}
		System.out.print("All edges are integers. ");
		System.out.println("The mean edge length (to the nearest integer) is " + sum/count);
		output.print("All edges are integers. ");
		output.println("The mean edge length (to the nearest integer) is " + sum/count);
		
		output.flush();
	}

}
