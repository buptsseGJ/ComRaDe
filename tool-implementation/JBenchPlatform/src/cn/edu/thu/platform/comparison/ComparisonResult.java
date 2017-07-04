package cn.edu.thu.platform.comparison;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.edu.thu.platform.entity.Report;
import cn.edu.thu.platform.entity.Result;

public class ComparisonResult {
	public static String tool = "CalFuzzer";
	public static String result = "";
	public static Map<String, Report> findRace = new HashMap<String, Report>();
	public static Map<String, Report> missRace = new HashMap<String, Report>();
	public static Map<String, Report> additianalRace = new HashMap<String, Report>();
	public static Map<String, String> summary = new HashMap<String, String>();
	public static Map<String,Result> resultsMap = new HashMap<String,Result>();
	public static List<Result> results = new ArrayList<Result>();
}
