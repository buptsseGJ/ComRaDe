package cn.edu.thu.platform.entity;

import java.util.HashSet;
import java.util.Set;

public class Report {
	private Set<Race> races = new HashSet<Race>();
	private String name;
	private Set<Race> compareRaces = new HashSet<Race>();
	private String time; 
	
	public Report(Set<Race> compareRaces) {
		this.compareRaces = compareRaces;
	}
	public Report(String name,String time,Set<Race> compareRaces) {
		this.name = name;
		this.time = time;
		this.compareRaces = compareRaces;
	}

	public Report(Set<Race> races, String name) {
		super();
		this.races = races;
		this.name = name;
	}

	public Report(Set<Race> races, String name, String time) {
		super();
		this.races = races;
		this.name = name;
		this.time = time;
	}

	public Report() {
	}

	public Set<Race> getRaces() {
		return races;
	}

	public void setRaces(Set<Race> races) {
		this.races = races;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Race> getCompareRaces() {
		return compareRaces;
	}

	public void setCompareRaces(Set<Race> compareRaces) {
		this.compareRaces = compareRaces;
	}
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Object  cloneRace(){
		
		return ((HashSet<Race>)races).clone();
	}
}
