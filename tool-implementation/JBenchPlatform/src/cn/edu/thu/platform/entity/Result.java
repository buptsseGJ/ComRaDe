package cn.edu.thu.platform.entity;

public class Result {
	private String programName;
	private int totalRaace;
	private int mathchedRace;
	private int additianlRace;
	private int missedRace;
	private double initialTime;
	private double totalTime;
	
	public Result(String programName, int totalRaace, int mathchedRace,
			int additianlRace, int missedRace, double initialTime,
			double totalTime) {
		super();
		this.programName = programName;
		this.totalRaace = totalRaace;
		this.mathchedRace = mathchedRace;
		this.additianlRace = additianlRace;
		this.missedRace = missedRace;
		this.initialTime = initialTime;
		this.totalTime = totalTime;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public int getTotalRaace() {
		return totalRaace;
	}

	public void setTotalRaace(int totalRaace) {
		this.totalRaace = totalRaace;
	}

	public int getMathchedRace() {
		return mathchedRace;
	}

	public void setMathchedRace(int mathchedRace) {
		this.mathchedRace = mathchedRace;
	}

	public int getAdditianlRace() {
		return additianlRace;
	}

	public void setAdditianlRace(int additianlRace) {
		this.additianlRace = additianlRace;
	}

	public int getMissedRace() {
		return missedRace;
	}

	public void setMissedRace(int missedRace) {
		this.missedRace = missedRace;
	}

	public double getInitialTime() {
		return initialTime;
	}

	public void setInitialTime(double initialTime) {
		this.initialTime = initialTime;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}	
	
}
