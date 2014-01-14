package soot.jimple.infoflow.methodSummary.xml;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;

public class WriterFactory {
	public static ISummaryWriter createXMLWriter(String file, String folder) {
		try {
			if(folder == null || folder == "")
				return new XMLWriter(new File(file));
			else
				return new XMLWriter(new File(folder + File.separator + file));	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static ISummaryWriter createXMLWriter(String file) {
		try {
			return new XMLWriter(new File(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static ISummaryWriter createXMLWriter(File file) {
		try {
			return new XMLWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
