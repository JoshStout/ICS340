//import java.util.*;

// Edge between two nodes
public class Edge implements Comparable<Edge> {
	
	String label;
	int dist;
	Node tail;
	Node head;
	String type; //added for DelivC
	//String prerequ; //added for DelivD
	
	
	public Edge( Node tailNode, Node headNode, String theLabel ) {
		setLabel( theLabel);
		try {
			setDist(Integer.parseInt(theLabel));
		}
		catch(NumberFormatException nfe) {
			setDist(Integer.MIN_VALUE);
		}
		setTail( tailNode );
		setHead( headNode );
	}
		
	public Node getTail() {
		return tail;
	}
	
	public Node getHead() {
		return head;
	}
	
	public int getDist() {
		return dist;
	}
	
	public void setTail( Node n ) {
		tail = n;
	}
	
	public void setHead( Node n ) {
		head = n;
	}
	
	public void setDist( int i ) {
		dist = i;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String s) {
		label = s;
	}
	
	//setter added for DelivC
	public void setType(String t) {
		type = t;
	}
	//getter added for DelivC
	public String getType() {
		return type;
	}
		
	//added for DelivC
	//compare distances between edges
	@Override
	public int compareTo(Edge e) {
		if(this.dist < e.dist) {
			return -1;
		}else if(this.dist > e.dist) {
			return 1;
		}else {
			return 0;
		}
	}
	
	
}
