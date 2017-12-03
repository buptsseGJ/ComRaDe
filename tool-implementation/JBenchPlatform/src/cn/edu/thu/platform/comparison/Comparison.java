package cn.edu.thu.platform.comparison;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import cn.edu.thu.platform.entity.Race;
import cn.edu.thu.platform.entity.Report;
import cn.edu.thu.platform.entity.Reports;

import cn.edu.thu.platform.entity.Result;
import cn.edu.thu.platform.script.Script;

public class Comparison {

	public String compare(String programName,Report originalReport, Report newReport) {
//		if(newReport != null && originalReport !=null){
			Set<Race> originalRaces = originalReport.getCompareRaces();
			Set<Race> newRaces = newReport.getCompareRaces();//用户的report
			int originalLength = originalRaces.size();
			int newlength = newRaces.size();
			int rightMatch = 0;
			int falseMatch = 0;
			Iterator<Race> it = newRaces.iterator();
			Set<Race> findRaces = new HashSet<Race>();//find data race which is in benchmark xml file
			Set<Race> missRaces = new HashSet<Race>();//fail to find data race which is in benchmark xml file
			Set<Race> additionalRaces = new HashSet<Race>();//find other data race which is not in benchmark xml file, it is true or false data race
			Report findReport = null;
			Report missReport = null;
			Report additionalReport = null;
			while (it.hasNext()) {
				Race race1 = it.next();
				if (originalRaces.contains(race1)) {
					rightMatch++;
					findRaces.add(race1);
				} else {
					falseMatch++;
					additionalRaces.add(race1);
				}
			}
			missRaces.addAll(originalRaces);
			missRaces.removeAll(newRaces);
			findReport = new Report(findRaces);
			missReport = new Report(missRaces);
			additionalReport = new Report(additionalRaces);
			Result result = new Result(programName, originalLength, rightMatch, falseMatch, originalLength - rightMatch, 0, 0);
			ComparisonResult.results.add(result);
			ComparisonResult.resultsMap.put(programName, result);
			if(rightMatch != originalLength){
				Script.suggestName.add(programName);
			}
//			ComparisonResult.result += message;
			ComparisonResult.findRace.put(programName, findReport);
			ComparisonResult.missRace.put(programName, missReport);
			ComparisonResult.additianalRace.put(programName, additionalReport);
//			ComparisonResult.summary.put(programName, message);
			Iterator<Race> itFind = newRaces.iterator();
			while(itFind.hasNext()){
				Race race = itFind.next();
//				message += getSuccessInformation(Reports.reports.get(programName).getRaces(),findRaces);
//				message += getMissInformation(Reports.reports.get(programName).getRaces(),missRaces);
//				message += getAdditionInformation(additionalRaces);
			}
			return null ;
	}
	
	public String getAdditionInformation(Set<Race> current){
		String info = "";
		if(current.isEmpty()){
			info += "\nThese data races may be false: none\n";
		}else{
			Iterator<Race> it = current.iterator();
			int count = 1;
			while(it.hasNext()){
				Race temp = it.next();
				if(count == 1){
					info += "These data races may be false:\n";
				}
				info += "\t" + count + ")location：<" + temp.getLine1() + "," + temp.getLine2() + ">\n";
				count++;
			}
		}
		return info;
	}
	
	public String getMissInformation(Set<Race> original, Set<Race> current){
		String info = "";
		if(current.isEmpty()){
			info += "\nThe data races missed are the following: none\n";
		}else{
			Iterator<Race> it = original.iterator();
			int count = 1;
			while(it.hasNext()){
				Race temp = it.next();
				if(current.contains(temp)){
					if(count == 1){
						info += "The data races missed are the following:\n";
					}
					info += "\t" + count + ")location:<" + temp.getLine1() + "," + temp.getLine2() + ">\n";
					info += "\t  variable:" + temp.getVariable() + "\n";
					info += "\t  class:" + temp.getPackageClass() + "\n";
					info += "\t  detals:" + temp.getDetail() + "\n";
					count++; 
				}
			}		
		}
		return info;
	}
	
	public String getSuccessInformation(Set<Race> original, Set<Race> current){
		String info = "";
		if(current.isEmpty()){
			info += "The data races found successfully are the following: none\n";
		}else{
			Iterator<Race> it = original.iterator();
			int count = 1;
			while(it.hasNext()){
				Race temp = it.next();
				if(current.contains(temp)){
					if(count == 1){
						info += "The data races found successfully are the following:\n";
					}
					info += "\t" + count + ")location:<" + temp.getLine1() + "," + temp.getLine2() + ">\n";
					info += "\t  variable:" + temp.getVariable() + "\n";
					info += "\t  class:" + temp.getPackageClass() + "\n";
					info += "\t  details:" + temp.getDetail() + "\n";
					count++;
				}
			}
		}
		return info;
	}

	public Set<Race> getUniqueRace(Set<Race> races) {
		Iterator it = races.iterator();
		while (it.hasNext()) {
			Race race1 = (Race) it.next();
			races.remove(race1);
			if (!races.contains(race1)) {
				races.add(race1);
			}
		}
		return races;
	}
}
