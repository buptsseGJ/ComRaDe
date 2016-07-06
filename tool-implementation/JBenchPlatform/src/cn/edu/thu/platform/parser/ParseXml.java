package cn.edu.thu.platform.parser;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.edu.thu.platform.entity.Race;

import java.io.*;

public class ParseXml {

	public String validateXmlFilePath="";
	public DocumentBuilder builder = null;
	public DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	public Document validateXml(String fileAbsolutePath) {
		validateXmlFilePath=fileAbsolutePath;
		try {
			factory.setValidating(true);
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		builder.setEntityResolver(new EntityResolver(){
            @Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
            {
                   String dtd_uri = System.getProperty("user.dir").replace('\\', '/')+"/file/standard.dtd";
                   return new InputSource(dtd_uri);
            }
		});
		builder.setErrorHandler(new MyErrorHandler());
		try {
			Document document = builder.parse(fileAbsolutePath);
//			System.out.println("ok,Parse correctly!");
			return document;
		} catch (SAXException | IOException e) {
//			e.printStackTrace();
		}
		return null;
	}
	
	public Document buildDocument(String fileAbsolutePath) throws SAXException, IOException{
//		String filePath = "file/bench1.xml";
		if(fileAbsolutePath.equals("")){
			fileAbsolutePath = validateXmlFilePath;
		}
        Document doc = builder.parse(fileAbsolutePath);
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	public Document emptyDocument(String fileAbsolutePath) throws SAXException, IOException{
//		String filePath = "file/bench1.xml";
		Document doc = builder.parse(fileAbsolutePath);
		doc.getDocumentElement().normalize();
		return doc;
	}

    public void addElement(Document doc, String id, Race race,String totalTime) {
    	Element report = doc.getElementById(id);    	
    	Element raceElement = doc.createElement("race");
    	Element line1Element = doc.createElement("line1");
    	line1Element.appendChild(doc.createTextNode(race.getLine1()));
    	Element line2Element = doc.createElement("line2");
    	line2Element.appendChild(doc.createTextNode(race.getLine2()));
    	Element variableElement = doc.createElement("variable");
    	variableElement.appendChild(doc.createTextNode(race.getVariable()));
    	Element packageClassElement = doc.createElement("packageClass");
    	packageClassElement.appendChild(doc.createTextNode(race.getPackageClass()));
    	Element detailElement = doc.createElement("detail");
    	detailElement.appendChild(doc.createCDATASection(race.getDetail()));
    	Element runningTimeElement = doc.createElement("runningTime");
    	runningTimeElement.appendChild(doc.createTextNode(race.getTime()));
    	Element variableTypeElement = doc.createElement("variableType");
    	variableTypeElement.appendChild(doc.createTextNode(race.getVariableType()));
    	Element codeStructureElement = doc.createElement("codeStructure");
    	codeStructureElement.appendChild(doc.createTextNode(race.getCodeStructure()));
    	Element methodSpanElement = doc.createElement("methodSpan");
    	methodSpanElement.appendChild(doc.createTextNode(race.getMethodSpan()));
    	Element sensitiveBranchElement = doc.createElement("sensitiveBranch");
    	sensitiveBranchElement.appendChild(doc.createTextNode(race.getSensitiveBranch()));
    	Element sensitiveLoopElement = doc.createElement("sensitiveLoop");
    	sensitiveLoopElement.appendChild(doc.createTextNode(race.getSensitiveLoop()));
    	Element causeElement = doc.createElement("cause");
    	causeElement.appendChild(doc.createTextNode(race.getCause()));
    	Element commonUsageElement = doc.createElement("commonUsage");
    	commonUsageElement.appendChild(doc.createTextNode(race.getCommonUsage()));
    	Element bugElement = doc.createElement("bug");
    	bugElement.appendChild(doc.createTextNode(race.getBug()));
    	raceElement.appendChild(line1Element);
    	raceElement.appendChild(line2Element);
    	raceElement.appendChild(variableElement);
    	raceElement.appendChild(packageClassElement);
    	raceElement.appendChild(detailElement);
    	raceElement.appendChild(runningTimeElement);
    	raceElement.appendChild(variableTypeElement);
    	raceElement.appendChild(codeStructureElement);
    	raceElement.appendChild(methodSpanElement);
    	raceElement.appendChild(sensitiveBranchElement);
    	raceElement.appendChild(sensitiveLoopElement);
    	raceElement.appendChild(causeElement);
    	raceElement.appendChild(commonUsageElement);
    	raceElement.appendChild(bugElement);
    	if(report == null){
    		report = doc.createElement("report");
    		report.setAttribute("name", id);
    		report.setIdAttribute("name", true);
    		Element root = doc.getDocumentElement();
    		root.appendChild(report);
    	}
    	if(report.hasAttribute("totalTime")&&!report.getAttribute("totalTime").equals(totalTime))
    		report.setAttribute("totalTime", totalTime);
    	report.appendChild(raceElement);
    }
    
    public void deleteElement(Document doc, String id, Race race) {
    	String currentLine1 = race.getLine1();
    	String currentLine2 = race.getLine2();
    	String temp = currentLine1;
    	if(Integer.parseInt(currentLine1) > Integer.parseInt(currentLine2)){
    		currentLine1 = currentLine2;
    		currentLine2 = temp;
    	}
        Element report = doc.getElementById(id);
        NodeList elements = report.getElementsByTagName("race");
        Element element = null;
        //loop for each element
        for(int i=0; i<elements.getLength();i++){
            element = (Element) elements.item(i);//a race
            Node line1Node = element.getElementsByTagName("line1").item(0);
            Node line2Node = element.getElementsByTagName("line2").item(0);
            String line1 = line1Node.getFirstChild().getNodeValue();
            String line2 = line2Node.getFirstChild().getNodeValue();
            temp = line1;
            if(Integer.parseInt(line1) > Integer.parseInt(line2)){
            	line1 = line2;
            	line2 = temp;
            }
            if(line1.equals(currentLine1)&&line2.equals(currentLine2)){
            	report.removeChild(element);
            }            
        }
    }
    
    /*
     * 
     */    
    public boolean writeDomToXml(Document doc,String file) throws TransformerException{
    	if(file==null){//save the old file
    			file=validateXmlFilePath;
    	}
    	doc.getDocumentElement().normalize();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, doc.getDoctype().getSystemId());
        DOMSource source = new DOMSource(doc);
        File curFile = new File(file);
	    try {
			if(curFile.exists()){
			    StreamResult result = new StreamResult(curFile);
			    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			    transformer.transform(source, result);
//			    System.out.println("XML file updated successfully");
			}else{
		    	curFile.createNewFile();
			}
		} catch (IllegalArgumentException | IOException e) {
			return false;
		}
	return true;
    }    
}

