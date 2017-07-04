package cn.edu.thu.platform.parser;

import javax.swing.JOptionPane;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class MyErrorHandler implements ErrorHandler {

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("warining");
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("error");
		JOptionPane.showMessageDialog(null,
				"\nxml file has problem\n" + exception.getMessage(), "error",
				JOptionPane.ERROR_MESSAGE);
		throw new SAXException();
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		// TODO Auto-generated method stub
		System.out.println("fatalError");
	}

}
