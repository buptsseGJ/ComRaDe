package cn.edu.thu.platform.parser;

import java.awt.TextArea;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.edu.thu.platform.entity.Race;
import cn.edu.thu.platform.entity.Report;
import cn.edu.thu.platform.entity.Reports;
import cn.edu.thu.platform.frame.RoughResultFrame;

public class DomToEntity {

	String textAreaInfo = "";
	TextArea textArea = null;

	public void startDom(Node node) {
		if (node == null) {
			return;
		}
		Node root = ((Document) node).getDocumentElement();
		loopDom(root);
	}

	public String startDom(Document validationResult, String textAreaInfo, TextArea textArea2) {
		this.textAreaInfo = textAreaInfo;
		this.textArea = textArea2;
		if (validationResult == null) {
			return "";
		}
		Node root = validationResult.getDocumentElement();
		loopDom(root);
		this.textArea = null;
		// return this.textAreaInfo;
		return "";
	}

	public String startDomForResultXml(Document validationResult, String textAreaInfo, TextArea textArea2) {
		this.textAreaInfo = textAreaInfo;
		this.textArea = textArea2;
		if (validationResult == null) {
			return "";
		}
		Node root = validationResult.getDocumentElement();
		Reports.userReports.clear();
		Reports.userNames.clear();
		Reports.wrongNames.clear();
		loopDomForResultXml(root);
		// return this.textAreaInfo;
		this.textArea = null;
		return "";
	}

	public void loopDomForResultXml(Node node)
	{
		if (node.hasChildNodes()) {
			// get all the report element
			Node textNode = node.getFirstChild();// textNode stands for text node
			NodeList reportList = node.getChildNodes();
			for (int i = 0; i < reportList.getLength(); i++) {
				if (reportList.item(i).getNodeType() != (Node.TEXT_NODE)) {
					// get all the race element for each report
					if(textArea!=null) {
						textAreaInfo=textAreaInfo+"reportList Length:"+reportList.getLength()+"\n";
//						textArea.setText(textAreaInfo);
					}
//					System.out.println("reportList Length:"	+ reportList.getLength());
					NodeList raceList = reportList.item(i).getChildNodes();
					Set<Race> races = new HashSet<Race>();
					Set<Race> compareRaces = new HashSet<Race>();
					for (int j = 0; j < raceList.getLength(); j++) {
						// deal with each race
						if(textArea!=null) {
							textAreaInfo=textAreaInfo+"raceList Length:"+raceList.getLength()+"\n";
//							textArea.setText(textAreaInfo);
						}
//						System.out.println("raceList length:"+ raceList.getLength());
						//According to the xml document, the following codes read the element value into object in turn.
						if (raceList.item(j).getNodeType() != Node.TEXT_NODE) {
							if (raceList.item(j).hasChildNodes()) {
								Node tempNode = raceList.item(j).getFirstChild();
								Node tempLine1 = getNonTextNode(tempNode);
								tempNode = tempLine1.getNextSibling();
								Node tempLine2 = getNonTextNode(tempNode);

								if(textArea!=null) {
									textAreaInfo=textAreaInfo+"line1:"+tempLine1.getFirstChild().getNodeValue().toString()+"\n";
//									textArea.setText(textAreaInfo);
								}
//								System.out.println("line1:"	+ tempLine1.getFirstChild().getNodeValue().toString());
								String line1 = tempLine1.getFirstChild() != null ? (tempLine1.getFirstChild().getNodeValue().toString()).trim() : "0";
								String line2 = tempLine2.getFirstChild()!= null ? (tempLine2.getFirstChild().getNodeValue().toString()).trim() : "0";
								System.out.println("line1:"+line1+"\nline2:"+line2);
								int x1 = Integer.parseInt(line1.trim());
								int x2 = Integer.parseInt(line2.trim());
								if(x1<x2) {
									Race race = new Race(line1.trim(),line2.trim());
									races.add(race);
								}else {
									Race race = new Race(line2.trim(),line1.trim());
									races.add(race);			
								}
							}
						}
					}
					NamedNodeMap reportAttributes = reportList.item(i).getAttributes();
					String programName = reportAttributes.getNamedItem("name").getNodeValue().toString();
					String totalTime = reportAttributes.getNamedItem("totalTime").getNodeValue().toString();
					System.out.println("name:"+programName+"\ntotalTime:"+totalTime);
					if(Reports.programNames.contains(programName)){
						Reports.userNames.add(programName);
					}else{
						Reports.wrongNames.add(programName);
					}
					Report report = new Report(programName,totalTime,races);
					Reports.userReports.put(programName, report);
				}
			}
		}
	}

	// Note:each element node may contain test node,
	// so we need to eliminate it.
	public void loopDom(Node node) {
		if (node.hasChildNodes()) {
			// get all the report element
			Node textNode = node.getFirstChild();// textNode stands for text
													// node
			NodeList reportList = node.getChildNodes();
			for (int i = 0; i < reportList.getLength(); i++) {
				if (reportList.item(i).getNodeType() != (Node.TEXT_NODE)) {
					// get all the race element for each report
					if (textArea != null) {
						textAreaInfo = textAreaInfo + "reportList Length:" + reportList.getLength() + "\n";
						// textArea.setText(textAreaInfo);
					}
					// System.out.println("reportList Length:" +
					// reportList.getLength());
					NodeList raceList = reportList.item(i).getChildNodes();
					Set<Race> races = new HashSet<Race>();
					Set<Race> compareRaces = new HashSet<Race>();
					for (int j = 0; j < raceList.getLength(); j++) {
						// deal with each race
						if (textArea != null) {
							textAreaInfo = textAreaInfo + "raceList Length:" + raceList.getLength() + "\n";
							// textArea.setText(textAreaInfo);
						}
						// System.out.println("raceList length:"+
						// raceList.getLength());
						// According to the xml document, the following codes
						// read the element value into object in turn.
						if (raceList.item(j).getNodeType() != Node.TEXT_NODE) {
							if (raceList.item(j).hasChildNodes()) {
								Node tempNode = raceList.item(j).getFirstChild();
								Node tempLine1 = getNonTextNode(tempNode);
								tempNode = tempLine1.getNextSibling();
								Node tempLine2 = getNonTextNode(tempNode);
								tempNode = tempLine2.getNextSibling();
								Node tempVariable = getNonTextNode(tempNode);
								tempNode = tempVariable.getNextSibling();
								Node tempPackageClass = getNonTextNode(tempNode);
								tempNode = tempPackageClass.getNextSibling();
								Node tempDetail = getNonTextNode(tempNode);
								tempNode = tempDetail.getNextSibling();
								Node tempTime = getNonTextNode(tempNode);
								tempNode = tempTime.getNextSibling();
								Node tempVariableType = getNonTextNode(tempNode);
								tempNode = tempVariableType.getNextSibling();
								Node tempCodeStructure = getNonTextNode(tempNode);
								tempNode = tempCodeStructure.getNextSibling();
								Node tempMethodSpan = getNonTextNode(tempNode);
								tempNode = tempMethodSpan.getNextSibling();
								Node tempSensitiveBranch = getNonTextNode(tempNode);
								tempNode = tempSensitiveBranch.getNextSibling();
								Node tempSensitiveLoop = getNonTextNode(tempNode);
								tempNode = tempSensitiveLoop.getNextSibling();
								Node tempCause = getNonTextNode(tempNode);
								tempNode = tempCause.getNextSibling();
								Node tempCommonUsage = getNonTextNode(tempNode);
								tempNode = tempCommonUsage.getNextSibling();
								Node tempBug = getNonTextNode(tempNode);

								if (textArea != null) {
									textAreaInfo = textAreaInfo + "line1:"
											+ tempLine1.getFirstChild().getNodeValue().toString() + "\n";
									// textArea.setText(textAreaInfo);
								}
								// System.out.println("line1:" +
								// tempLine1.getFirstChild().getNodeValue().toString());
								String line1 = tempLine1.getFirstChild() != null
										? (tempLine1.getFirstChild().getNodeValue().toString()).trim() : null;
								String line2 = tempLine2.getFirstChild() != null
										? (tempLine2.getFirstChild().getNodeValue().toString()).trim() : null;
								String time = tempTime.getFirstChild() != null
										? (tempTime.getFirstChild().getNodeValue().toString()).trim() : "";
								String variableType = tempVariableType.getFirstChild() != null
										? (tempVariableType.getFirstChild().getNodeValue().toString()).trim() : "";
								String codeStructure = tempCodeStructure.getFirstChild() != null
										? (tempCodeStructure.getFirstChild().getNodeValue().toString()).trim() : "";
								String methodSpan = tempMethodSpan.getFirstChild() != null
										? (tempMethodSpan.getFirstChild().getNodeValue().toString()).trim() : "";
								String sensitiveBranch = tempSensitiveBranch.getFirstChild() != null
										? (tempSensitiveBranch.getFirstChild().getNodeValue().toString()).trim() : "";
								String sensitiveLoop = tempSensitiveLoop.getFirstChild() != null
										? (tempSensitiveLoop.getFirstChild().getNodeValue().toString()).trim() : "";
								String cause = tempCause.getFirstChild() != null
										? (tempCause.getFirstChild().getNodeValue().toString()).trim() : "";
								String commonUsage = tempCommonUsage.getFirstChild() != null
										? (tempCommonUsage.getFirstChild().getNodeValue().toString()).trim() : "";
								String bug = tempBug.getFirstChild() != null
										? (tempBug.getFirstChild().getNodeValue().toString()).trim() : "";

								String variable = "";
								if (tempVariable.getFirstChild() != null) {
									variable = tempVariable.getFirstChild().getNodeValue() != null
											? tempVariable.getFirstChild().getNodeValue().toString().trim() : null;
								} else {
									variable = "";
								}
								// String variable =
								// tempVariable.getFirstChild().getNodeValue()
								// != null ?
								// tempVariable.getFirstChild().getNodeValue().toString().trim()
								// : null;
								String packageClass = tempPackageClass.getFirstChild().getNodeValue() != null
										? tempPackageClass.getFirstChild().getNodeValue().toString().trim() : null;
								String detail = "";
								if (tempDetail.getFirstChild() != null) {
									if (tempDetail.getFirstChild().getNodeType() == Node.TEXT_NODE) {
										detail = tempDetail.getFirstChild().getNextSibling().getNodeValue() != null
												? tempDetail.getFirstChild().getNextSibling().getNodeValue().toString()
												: null;
										// System.out.println("detail:" +
										// tempDetail.getFirstChild().getNextSibling().getNodeValue().toString());
										// detail.replace("\t", "");
										if (textArea != null) {
											textAreaInfo = textAreaInfo + "detail:" + "      "
													+ tempDetail.getFirstChild().getNextSibling().getNodeValue()
															.toString().replace("\t", "")
													+ "\n";
											// textArea.setText(textAreaInfo);
										}
									} else {
										detail = tempDetail.getFirstChild().getNodeValue() != null
												? tempDetail.getFirstChild().getNodeValue().toString() : null;
										// System.out.println("detail:" +
										// tempDetail.getFirstChild().getNodeValue().toString());
										if (textArea != null) {
											textAreaInfo = textAreaInfo + "detail:" + "      " + tempDetail
													.getFirstChild().getNodeValue().toString().replace("\t", "") + "\n";
											// textArea.setText(textAreaInfo);
										}
									}
								}
								Race tempRace, compareRace;
								if (Integer.parseInt(line1) < Integer.parseInt(line2)) {
									tempRace = new Race(line1, line2, variable, packageClass, detail, time,
											variableType, codeStructure, methodSpan, sensitiveBranch, sensitiveLoop,
											cause, commonUsage, "");
									compareRace = new Race(line1, line2, packageClass);
								} else {
									tempRace = new Race(line2, line1, variable, packageClass, detail, time,
											variableType, codeStructure, methodSpan, sensitiveBranch, sensitiveLoop,
											cause, commonUsage, "");
									compareRace = new Race(line2, line1,packageClass);
								}
								races.add(tempRace);
								compareRaces.add(compareRace);
							}
						}
					}
					NamedNodeMap reportAttributes = reportList.item(i).getAttributes();
					String programName = reportAttributes.getNamedItem("name").getNodeValue().toString();
					String totalTime = reportAttributes.getNamedItem("totalTime").getNodeValue().toString();
					Reports.programNames.add(programName);
					Report report = new Report(races, programName, totalTime);
					Report compareReport = new Report(programName, totalTime, compareRaces);// is
																							// a
																							// set
																							// all
																							// the
																							// races
																							// for
																							// each
																							// program
					if (!races.isEmpty()) {
						Reports.reports.put(programName, report);
						Reports.compareReports.put(programName, compareReport);
					}
				}
			}
		}
	}

	public Node getNonTextNode(Node node) {
		while (node.getNodeType() == Node.TEXT_NODE) {
			node = node.getNextSibling();
		}
		return node;
	}

}
