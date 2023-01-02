import java.io.File;
import java.io.PrintWriter;
import java.util.*;

// Class DelivC does the work for deliverable DelivC of the Prog340

public class DelivC {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	
	static int t; //added for DelivC
	static Stack<Node> finishStack = new Stack<Node>(); //added for DelivC
	static Queue<Node> discoveryQueue = new LinkedList<Node>(); //added for DelivC
	
	
	public DelivC( File in, Graph gr ) {
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
//		System.out.println( "DelivC:  To be implemented");
//		output.println( "DelivC:  To be implemented");
		
		//reset queue and time
		resetTime();
		clearDiscoveryQueue();
		
		//get start node: find val == S
		Node startNode = null;
		for(Node n : g.getNodeList()) {
			if(n.getVal().equalsIgnoreCase("S")) {
				startNode = n;
			}
		}
		
		//do DFS from start node
		if(startNode != null) {
			DFS(startNode);
		}
		
		//do DFS on non-reachable nodes
		generalDFS(g);
		
		//output DFS of graph
		System.out.println("DFS of graph:");
		output.println("DFS of graph: ");
		System.out.println("Node\tDisc\tFinish");
		output.println("Node\tDisc\tFinish");
		Queue<Node> discoveryQueue = new LinkedList<Node>(); //added for DelivC
		discoveryQueue = getDiscoveryQueue();
		while(!discoveryQueue.isEmpty()) {
			Node n = discoveryQueue.remove();
			System.out.println(n.getAbbrev() + "\t" + (n.getDiscovery()+1) + "\t" + (n.getFinish()+1));
			output.println(n.getAbbrev() + "\t" + (n.getDiscovery()+1) + "\t" + (n.getFinish()+1));
		}
		
		//set edge type for all non-tree edges 
		//classification logic from https://www.geeksforgeeks.org/tree-back-edge-and-cross-edges-in-dfs-of-graph/
		for(Node n : g.getNodeList()) {
			for(Edge e : n.getOutgoingEdges()) {
				if(e.getType() == null) {
					if(n.getDiscovery() > e.getHead().getDiscovery() && n.getFinish() < e.getHead().getFinish()) {
						e.setType("Back");
					}else if(n.getDiscovery() < e.getHead().getDiscovery() && n.getFinish() > e.getHead().getFinish()) {
						e.setType("Foward");
					}else {
						e.setType("Cross");
					}
				}
			}
		}
		
		//output edge classification
		System.out.println();
		output.println();
		System.out.println("Edge Classification:");
		output.println("Edge Classification:");
		System.out.println("Edge\tType");
		output.println("Edge\tType");
		for(Node n : g.getNodeList()) {
			for(Edge e : n.getOutgoingEdges()) {
				System.out.println(n.getAbbrev() + "->" + e.getHead().getAbbrev() + "\t" + e.getType());
				output.println(n.getAbbrev() + "->" + e.getHead().getAbbrev() + "\t" + e.getType());
			}
		}
		System.out.println();
		output.println();
		
		//create complementary graph by reverse original graph
		for(Node n : g.getNodeList()) {
			for(Edge e : n.getIncomingEdges()) {
				e.setHead(e.getTail());
				e.setTail(n);
				if(e.getHead() != n) {
					n.addOutgoingEdge(e);
				}
			}
		}
		
		//set all node to white
		for(Node n : g.getNodeList()) {
			n.setColor("white");
		}
		
		//print the complementary graph
		System.out.println("DFS of complementary graph:");
		output.println("DFS of complementary graph:");
		System.out.println("Node\tDisc\tFinish");
		output.println("Node\tDisc\tFinish");
		Queue<Node> reverseDiscoveryQueue = new LinkedList<Node>(); //added for DelivC
		reverseDiscoveryQueue = getDiscoveryQueue();
		Queue<Node> componentQueue = new LinkedList<Node>(); //added for DelivC
		
		//DFS on reverse graph
		resetTime();
		Stack<Node> finishStack = new Stack<Node>();
		finishStack.addAll(getFinishStack());
		Node stackNode = null;
		while(!finishStack.isEmpty()){
			stackNode = finishStack.pop();
			if(stackNode.getColor() == "white") {
				DFS(stackNode);		
				while(!reverseDiscoveryQueue.isEmpty()) { //output DFS of complementary graph
					Node reverseNode = reverseDiscoveryQueue.remove();
					componentQueue.add(reverseNode);
					System.out.println(reverseNode.getAbbrev() + "\t" + (reverseNode.getDiscovery()+1) + "\t" + (reverseNode.getFinish()+1));
					output.println(reverseNode.getAbbrev() + "\t" + (reverseNode.getDiscovery()+1) + "\t" + (reverseNode.getFinish()+1));
				}
				componentQueue.add(null);
			}
		}
		
		//output strongly connected components
		System.out.println();
		output.println();
		System.out.print("Strongly Connected Components: ");
		output.print("Strongly Connected Components: ");
		int x = 0;
		String str = "";
		while(!componentQueue.isEmpty()) {
			Node y = componentQueue.remove();
			if(y != null) {
				str += y.getAbbrev();
			}else {
				x++;
				str += "\n";
			}
		}
		System.out.println(x);
		output.println(x);
		System.out.println(str);
		output.println(str);
		
		output.flush();
	}
	
	
	//loop thru all nodes in graph and do DFS 
	public static void generalDFS(Graph g) {
		for(Node n : g.getNodeList()) {
			if(n.getColor() == "white") {
				DFS(n);
			}
		}
	}
	
	//DFS on specific node passed 
	public static void DFS(Node n) {
		ArrayList<Edge> sortedEdges = new ArrayList<Edge>();
		n.setColor("gray");
		discoveryQueue.add(n);
		n.setDiscovery(t);
		t++;
		for(Edge e : n.getOutgoingEdges()) {
			sortedEdges.add(e);
		}
		Collections.sort(sortedEdges);
		for(Edge e : sortedEdges) {
			if(e.getHead().getColor().equals("white")) {
				e.setType("Tree");
				DFS(e.getHead());
			}
		}
		n.setColor("black");
		n.setFinish(t);
		finishStack.push(n);
		t++;
	}
	
	//returns the order of node discovery
	public Queue<Node> getDiscoveryQueue(){
		return discoveryQueue;
	}
	
	//returns the stack of node finishing times
	public Stack<Node> getFinishStack() {
		return finishStack;
	}
	
	public static void clearDiscoveryQueue() {
		while(!discoveryQueue.isEmpty()) {
			discoveryQueue.remove();
		}
	}
	
	public void resetTime() {
		t = 0;
	}
			
}
