package cn.edu.thu.platform.match;

public class Match {

	public static MatchInterface getMatch(String type) {
		MatchInterface match = null;
		//interface for matching a specified tool which is selected by user
		switch (type) {
			case "CalFuzzer":
				match = new MatchCalfuzzer();
				break;
			case "DATE":
//				match = new MatchDATE();
				break;
			case "Rv-Predict":
				match = new MatchRvPredict();
				break;
			case "other":
				match = new MatchOther();
				break;
			default:
				break;
		}
		return match;
	}
}