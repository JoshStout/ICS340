import java.util.*;

// A node of a graph for the Spring 2018 ICS 340 program

public class Node implements Comparable <Node> {

	String name;
	String val;  // The value of the Node
	String abbrev;  // The abbreviation for the Node
	ArrayList<Edge> outgoingEdges;  
	ArrayList<Edge> incomingEdges;
	
	//added for DelivC
	int discovery; //discovery time of the node
	int finish; //finish time of the node 
	String color; //node color for DFS
	
	//added for DelivD
	String day;
	int semester;
	char cDay;
	
	public Node( String theAbbrev ) {
		setAbbrev( theAbbrev );
		val = null;
		name = null;
		outgoingEdges = new ArrayList<Edge>();
		incomingEdges = new ArrayList<Edge>();
		color = "white"; //added for DelivC
	}
	
	
	public String getAbbrev() {
		return abbrev;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVal() {
		return val;
	}
	
	public ArrayList<Edge> getOutgoingEdges() {
		return outgoingEdges;
	}
	
	public ArrayList<Edge> getIncomingEdges() {
		return incomingEdges;
	}
	
	public void setAbbrev( String theAbbrev ) {
		abbrev = theAbbrev;
	}
	
	public void setName( String theName ) {
		name = theName;
	}
	
	public void setVal( String theVal ) {
		val = theVal;
	}
	
	public void addOutgoingEdge( Edge e ) {
		outgoingEdges.add( e );
	}
	
	public void addIncomingEdge( Edge e ) {
		incomingEdges.add( e );
	}
	
	@Override
	public int compareTo(Node n) {
		Node node = (Node) n;
		double doubleVal = Double.parseDouble(this.val);
		double doubleNodeVal = Double.parseDouble(node.val);
		if(doubleVal > doubleNodeVal) {
			return 1;
		}else if(doubleVal == doubleNodeVal){
			return 0;
		}else {
			return -1;
		}
		
	}
	
	//getters and setters added for DelivC
	public void setDiscovery(int t) {
		discovery = t;
	}
	public int getDiscovery() {
		return discovery;
	}
	public void setFinish(int t) {
		finish = t;
	}
	public int getFinish() {
		return finish;
	}
	public void setColor(String c) {
		color = c;
	}
	public String getColor() {
		return color;
	}
	
	//getters and setters added for DelivD
	public void setDay(String d) {
		day = d;
	}
	public String getDay() {
		return day;
	}
	public void setSemester(int s) {
		semester = s;
	}
	public int getSemester() {
		return semester;
	}
	public void setDay(char d) {
		cDay = d;
	}
	public char getCharDay() {
		return cDay;
	}
	
}
