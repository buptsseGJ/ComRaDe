package cn.edu.thu.platform.script;

import java.util.ArrayList;
import java.util.List;

public class Script {
	public static List<String> scripts = new ArrayList<String>();
	public static List<String> suggestName = new ArrayList<String>();
	
	public static boolean contain(String name){
		boolean flag = false;
		if(suggestName.contains(name)){
			flag = true;
		}
		return flag;
	}
	
	public static List<String> getSuggestScripts(){
		List<String> suggestScripts = new ArrayList<String>();
		for(int i = 0; i < scripts.size(); i++){
			String temp = scripts.get(i);
			int index = temp.indexOf(']');
			String name = temp.substring(1, index);
			if(suggestName.contains(name)){
				suggestScripts.add(temp);
			}
		}
		return suggestScripts;
	}
}
