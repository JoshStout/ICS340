import java.io.File;
import java.io.PrintWriter;
import java.util.*; //added for Random class

// Class DelivD does the work for deliverable DelivD of the Prog340

public class DelivD {

	File inputFile;
	File outputFile;
	PrintWriter output;
	Graph g;
	
	public DelivD( File in, Graph gr ) {
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
//		System.out.println( "DelivD:  To be implemented");
//		output.println( "DelivD:  To be implemented");
		
		
		//check correct test file is being used 
		String testFileName;
		String testFile = baseFileName;
		testFileName = testFile.substring(testFile.length() - 3);
		if(!testFileName.equals("F4b")) {
			System.out.println("Please use test file F4b.txt");
			System.exit(0);
		}
				
		//create schedule by entering classes in order of test file
		Node[] originalSchedule = new Node[g.getNodeList().size()];
		Node[] schedule = new Node[g.getNodeList().size()];
		int k = 0;
		for(Node n : g.getNodeList()) {
			originalSchedule[k] = n;
			schedule[k] = n;
			k++;
		}
		
		String[] semesterNames = new String[8];
		semesterNames[0] = "Fa-20:";
		semesterNames[1] = "Sp-21:";
		semesterNames[2] = "Su-21:";
		semesterNames[3] = "Fa-21:";
		semesterNames[4] = "Sp-22:";
		semesterNames[5] = "Su-22:";
		semesterNames[6] = "Fa-22:";
		semesterNames[7] = "Sp-23:";
				
		//print out original schedule
		System.out.println("Original schedule:");
		printSchedule(schedule);
		
		//map classes to schedule index where class is not available 
		HashMap<Node, Integer> map = new HashMap<>();
		
		System.out.println("random schedule:");
		schedule = createRandomSchedule(schedule, map);
		printSchedule(schedule);
		
		//get conflicts: index of conflict = index of class in schedule array
		int[] conflicts = getPrerequisiteConflicts(schedule);
		printSchedule(schedule);
		int totalConflicts = totalConflicts(conflicts);
		System.out.println("Total prerequisite conflicts = " + totalConflicts);
		
		
		//one main loop containing two inner loops - 
		//inner loop 1: loop until there is no more conflicts with prerequisites using IBI.
		//if all prerequisites are fulfilled then move to next loop.
		//inner loop 2: loop until all classes for each semester have unique days using random day assignments.
		
		int mainLoopCounter = 0;
		boolean restart = true;
		while(restart == true) { ///////////////////////////////////////start of main loop/////////////////////////////////////////////////////
			System.out.println("Main Loop " + mainLoopCounter + "\n\n");
			
			//loop until no prerequisite conflicts
			///////////////////////////////////start of prerequisite conflicts loop////////////////////////////////////////////
			int prerequLoopCounter = 0;
			while(totalConflicts != 0) {
				
				//create a random schedule after X amount of loops
				if(prerequLoopCounter < 20000) {
					System.out.println("\nMain Loop Counter: " + mainLoopCounter);
					System.out.println("\nPrerequisite Loop counter = " + prerequLoopCounter);
					
					//get index of class with most conflicts 
					int mostConflicts = conflicts[0];
					int mostConflictsIndex = 0;
					for(int i = 1; i < conflicts.length; i++) {
						if(conflicts[i] > mostConflicts) {
							mostConflicts = conflicts[i];
							mostConflictsIndex = i;
						}
					}
					
					//move class with the most conflicts to a random index
					schedule = shiftPrerequisiteClasses(schedule, mostConflictsIndex);
					
					//print out schedule
					conflicts = getPrerequisiteConflicts(schedule);
					printSchedule(schedule);
					System.out.println();
					totalConflicts = totalConflicts(conflicts);
					System.out.println("Total prerequisite conflicts = " + totalConflicts);
					
					prerequLoopCounter++;
					
				}else {
					schedule = createRandomSchedule(schedule, map);
					conflicts = getPrerequisiteConflicts(schedule);
					totalConflicts = totalConflicts(conflicts);
					prerequLoopCounter = 0;
					
					System.out.println("HashMap:");
					map.entrySet().forEach(entry -> {
						System.out.print(entry.getKey().getAbbrev() + " " + entry.getValue() + " ");
					});
					System.out.println();
				}
				
			}
			///////////////////////////////end of prerequisite conflicts loop////////////////////////////////////////////////////////
			
			
			//set semester attribute to node objects 
			int semester = 0;
			for(int i = 0; i < schedule.length; i++) {
				if(i % 4 == 0) {
					semester++;
				}
				schedule[i].setSemester(semester);
			}
			
			//print out prerequisite conflict free schedule
			System.out.println("Prerequisite conflict free schedule");
			printSchedule(schedule);		
			
			//check that each class on schedule is available during the scheduled semester
			System.out.println("\nadding days to schedule schedule\n");
			//add days to class nodes
			setCourseDays(schedule, originalSchedule);
			printSchedule(schedule);
			
			
			///////////////////////////////////////////start of class days conflicts loop/////////////////////////////////
			int daysLoopCounter = 0;
			pickDay(schedule); //add random days to schedule
			conflicts = daysConflicts(schedule);
			totalConflicts = totalConflicts(conflicts); //get total conflicts
			System.out.println("conflicts:" + totalConflicts);
			if(restart = true) {
				while(totalConflicts < 999 && totalConflicts > 0 && daysLoopCounter < 30000) { //continue to randomize days until no conflicts
					System.out.println("\nMain Loop Counter: " + mainLoopCounter);
					System.out.println("Days Loop Counter: " + daysLoopCounter);
					pickDay(schedule); //add random days to schedule
					conflicts = daysConflicts(schedule);
					//printSchedule(schedule);
					
					semester = 0;
					System.out.println();
					for(int i = 0; i < schedule.length; i++) {
						if(i % 4 == 0) {
							System.out.print("\n" + semesterNames[semester] + "\t");
							semester++;
						}
						//need to use class names (add attribute to node class for names) and remove days 
						System.out.print(schedule[i].getAbbrev() + " " + schedule[i].getCharDay() + conflicts[i] + "\t\t");
					}
					System.out.println(); 

					totalConflicts = totalConflicts(conflicts);
					System.out.println("Total days conflicts:" + totalConflicts);
					daysLoopCounter++;
						
				}
				///////////////////////////////////////////end of class days conflicts loop/////////////////////////////////
				
				//go back to start of main loop
				if(totalConflicts > 998) {
					restart = true;
				}
				
				//schedule is good!
				if(totalConflicts == 0) {
					restart = false;
					System.out.println("Main Loop Counter: " + mainLoopCounter);
					System.out.println("\n\nYour School Schedule");
					printSchedule(schedule);
					System.out.println();
					
					//output final schedule to file
					semester = 0;
					output.print("Your School Schedule");
					for(int i = 0; i < schedule.length; i++) {
						if(i % 4 == 0) {
							output.print("\n" + semesterNames[semester] + "\t");
							semester++;
						} 
						output.print(schedule[i].getName() + " " + schedule[i].getCharDay() + "\t\t");
					}
				}
			}
			
			mainLoopCounter++;
		}///////////////////////////////////////end of main loop/////////////////////////////////////////////////////

		
		output.flush();
	}
	
	
	public static Node[] createRandomSchedule(Node[] schedule, HashMap<Node, Integer> map) {
		Random r = new Random();
		for(int course = 0; course < schedule.length; course++) {
			int pos = r.nextInt(31-course);
			Node temp;
			temp = schedule[course];
			schedule[course] = schedule[pos];
			schedule[pos] = temp;
			
			//if temp in HashMap, swap class with neighbor to the right 
			if(map.containsKey(temp) && map.get(temp) == (Integer)pos) {
				temp = schedule[pos+1];
				schedule[pos+1] = schedule[pos];
				schedule[pos] = temp;
			}

		}
		return schedule;
	}
	
	public static int totalConflicts(int[] conflicts) {
		int total = 0;
		for(int i = 0; i < conflicts.length; i++) {
			total += conflicts[i];
		}
		return total;
	}
	
	public static int[] getPrerequisiteConflicts(Node[] schedule) {
		int[] conflicts = new int[schedule.length];
		for(int m = 0; m < schedule.length; m++) {
			for(Edge e : schedule[m].getOutgoingEdges()) {
				if(!e.getLabel().equals(">=")) {
					Node prerequ = e.getHead();
					//start on first index of current semester in schedule array and check if any prerequisites
					//are after this class in the array (scheduled later in school schedule) 
					int offset = m % 4; //offset back for classes in same semester
					for(int restOfSchedule = (m - offset); restOfSchedule < schedule.length; restOfSchedule++) {
						if(prerequ == schedule[restOfSchedule]) {
							conflicts[m]++;
						}
					}
				}
			}
		}
		return conflicts;
	}
	
	public static Node[] shiftPrerequisiteClasses(Node[] schedule, int mostConflictsIndex) {
		
		System.out.println("Index of most conflicts: " + mostConflictsIndex);
		
		//use random index and shift classes left or right to make room
		System.out.println("class with most conflicts: " + schedule[mostConflictsIndex].getAbbrev());
		
		//get a random index for new location for the class with the most conflicts 
		Random rand = new Random();
		int randomIndex = rand.nextInt(30);
		System.out.println("random = " + randomIndex);
		
		//shift class to left or right and place class with most conflicts in the index specified by randomIndex
		Node tempNode = schedule[mostConflictsIndex];
		if(randomIndex > mostConflictsIndex) { //if new index is greater than current index, shift elements in array left until current index
			for(int shiftIndex = mostConflictsIndex; shiftIndex < randomIndex; shiftIndex++) {
				schedule[shiftIndex] = schedule[shiftIndex+1];
			}
			schedule[randomIndex] = tempNode; //place node with most conflicts in new index
		}else { //shift element right to make room at randomIndex
			for(int shiftIndex = mostConflictsIndex; shiftIndex > randomIndex; shiftIndex--) {
				schedule[shiftIndex] = schedule[shiftIndex - 1];
			}
			schedule[randomIndex] = tempNode; //place node with the most conflicts in new index 
		}
		return schedule;
	}
	
	//add course days to node attribute 
	public static void setCourseDays(Node[] schedule, Node[] originalSchedule) {
		
		//fixed courseDaySem matrix 
		String[][] courseDaySem = 
			{ { "A", "A", "A", "A", "A", "A", "A", "A", "A" },
			  { "WS", "MT", "~", "HF", "MS", "M", "MH", "TF", "M" },
			  { "H", "H", "~", "H", "H", "~", "H", "N", "~" },
			  { "M", "TH", "~", "W", "H", "~", "H", "MT", "~" },
			  { "MHF", "MHFS", "W", "MF", "HFS", "W", "HF", "M", "W" },
			  { "H", "~", "~", "H", "~", "~", "H", "H", "~" },
			  { "MTWH", "MTW", "TH", "MTWH", "MT", "W", "TWH", "MT", "TW" },
			  { "MTWH", "MHF", "TW", "MTWH", "MH", "T", "TWF", "MWH", "WH" },
			  { "TH", "MW", "H", "T", "M", "H", "W", "M", "H" },
			  { "MF", "HS", "TW", "MH", "TF", "TW", "MWH", "MT", "TW" },
			  { "MWH", "MWH", "MW", "MTH", "MTW", "TH", "MTH", "MWH", "TH" },
			  { "MT", "MWH", "H", "MH", "HS", "H", "WH", "WH", "H" },
			  { "H", "~", "~", "H", "M", "~", "T", "T", "~" },
			  { "MW", "TW", "M", "TW", "TH", "W", "MT", "MT", "T" },
			  { "H", "TH", "T", "MT", "WH", "T", "WH", "MH", "W" },
			  { "~", "M", "~", "~", "H", "~", "~", "W", "~" },
			  { "H", "MH", "M", "M", "WH", "W", "M", "MW", "H" },
			  { "TW", "T", "H", "TH", "TH", "H", "TH", "TH", "T" },
			  { "M", "MW", "~", "H", "H", "~", "M", "H", "~" },
			  { "MTWH", "WHS", "MW", "TWH", "TWH", "TH", "MTW", "TWH", "TW" },
			  { "W", "~", "~", "W", "~", "~", "W", "~", "~" },
			  { "TF", "WH", "W", "TF", "TF", "W", "MT", "TW", "M" },
			  { "MT", "TH", "T", "TW", "WH", "T", "MW", "WH", "T" },
			  { "H", "M", "T", "M", "M", "H", "M", "M", "H" },
			  { "MTWH", "MTW", "MH", "MTWH", "MWH", "WH", "MTW", "MWH", "MT" },
			  { "MTWH", "MTWH", "~", "MTWH", "MTWH", "~", "MTWH", "MTWH", "~" },
			  { "F", "M", "~", "F", "M", "~", "F", "M", "~" },
			  { "H", "W", "~", "~", "H", "~", "~", "M", "~" },
			  { "T", "H", "~", "T", "WT", "~", "MH", "W", "~" },
			  { "A", "A", "A", "A", "A", "A", "A", "A", "A" },
			  { "A", "A", "A", "A", "A", "A", "A", "A", "A" }
			};
		
		
		int semester = 0;
		int originalIndex = 0;
		for(int i = 0; i < schedule.length; i++) {
			if(i % 4 == 0 && i != 0) {
				semester++;
			}
			Node course = schedule[i];
			for(int j = 0; j < originalSchedule.length; j++) {
				//get original index of class
				if(course.equals(originalSchedule[j])){
					originalIndex = j;
				}
			}
			//System.out.println(originalIndex + " " + semester);
			String strDay = courseDaySem[originalIndex][semester];
			if(!strDay.equals("~")) {
				course.setDay(strDay); //set the day as the first char of courseDaySem
			}else {
				course.setDay("0");
			}
		}
	}
	
	//pick a day
	public static void pickDay(Node[] schedule) {
		Random r = new Random();
		for(int i = 0; i < schedule.length; i++) {
			if(schedule[i].getDay().length() > 3) {
				//pick a day at random between 1 and 4
				int pos = r.nextInt(4); 
				schedule[i].setDay(schedule[i].getDay().charAt(pos));
			}else if(schedule[i].getDay().length() > 2) {
				//pick a day at random between 1 and 3
				int pos = r.nextInt(3);
				schedule[i].setDay(schedule[i].getDay().charAt(pos));
			}else if(schedule[i].getDay().length() > 1) {
				//pick a day at random between 1 and 2
				int pos = r.nextInt(2);
				schedule[i].setDay(schedule[i].getDay().charAt(pos));
			}else {
				//it only has one day
				schedule[i].setDay(schedule[i].getDay().charAt(0));
			}
		}

	}
	
	public static int totalDaysConflicts(int[] conflicts) {
		int total = 0;
		for(int i = 0; i < conflicts.length; i++) {
			if(conflicts[i] < 999) {
				total += conflicts[i];
			}
		}
		return total;
	}
	
	public static int[] daysConflicts(Node[] schedule) {
		int scheduleLength = schedule.length;
		int[] conflicts = new int[scheduleLength];
		
		for(int j = 0; j < scheduleLength; j++) {
			Node n = schedule[j];
			for(int k = 0; k < scheduleLength; k++) {
				if(!n.equals(schedule[k]) &&  n.getSemester() == schedule[k].getSemester() && n.getCharDay() == schedule[k].getCharDay()) {
					conflicts[j] += 1;
				}
			}
		}
		
		//check if class is not offered during schedule semester
		//if char '0' found, start over (sentinel value = 999)
		for(int i = 0; i < scheduleLength; i++) {
			if(schedule[i].getDay() == "0") {
				conflicts[i] = 999; 
			}
		}
		return conflicts;
	}
	
	public static void printSchedule(Node[] schedule) {
		
		//need to map classes to abbrev
		
		/* schedule: 4 classes per semester, 8 semesters 
		 * index 0-3: semester 0 
		 * index 4-7: semester 1 
		 * index 8-11: semester 2 
		 * index 12-15: semester 3 
		 * index 16-19: semester 4 
		 * index 20-23: semester 5 
		 * index 24-27: semester 6 
		 * index 28-31: semester 7 
		 */
		//place semester names in an array
		String[] semesterNames = new String[8];
		semesterNames[0] = "Fa-20:";
		semesterNames[1] = "Sp-21:";
		semesterNames[2] = "Su-21:";
		semesterNames[3] = "Fa-21:";
		semesterNames[4] = "Sp-22:";
		semesterNames[5] = "Su-22:";
		semesterNames[6] = "Fa-22:";
		semesterNames[7] = "Sp-23:";
		
		int semester = 0;
		System.out.println();
		for(int i = 0; i < schedule.length; i++) {
			if(i % 4 == 0) {
				System.out.print("\n" + semesterNames[semester] + "\t");
				semester++;
			}
			//need to use class names (add attribute to node class for names) and remove days 
			System.out.print(schedule[i].getName() + " " + schedule[i].getCharDay() + "\t\t");
		}
		System.out.println();
	}
	
	public static void testDelay() { //delay for testing 
		try {
			Thread.sleep(1000); //delay in milliseconds 
		}catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
