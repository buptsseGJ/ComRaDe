package cn.edu.thu.platform.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Reports {
	public static Map<String, Report> reports = new HashMap<String, Report>();
	// find all the race information with corresponding name
	public static Map<String, Report> compareReports = new HashMap<String, Report>();

	public static Map<String, Report> userReports = new HashMap<String, Report>();
	
	public static List<String> programNames = new ArrayList<String>();
	
	public static List<String> userNames = new ArrayList<String>();
	//store names of programs which are not in xml file
	public static List<String> wrongNames = new ArrayList<String>();
	
	//true means use regular expression to compare, false use input file to compare 
	public static boolean flag = true;
	
	public static void removeAllBenchmakrs() {
		reports.clear();
		programNames.clear();
	}

	public static Map<String, Set<Race>> cloneReports() {
		Map<String, Set<Race>> clone = new HashMap<String, Set<Race>>();
		for (int i = 0; i < programNames.size(); i++) {
			Set<Race> cloneReport = (Set<Race>) reports.get(Reports.programNames.get(i)).cloneRace();
			clone.put(Reports.programNames.get(i),cloneReport);
		}
		return clone;
	}  
}  
