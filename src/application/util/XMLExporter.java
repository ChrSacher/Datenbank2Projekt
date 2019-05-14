package application.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import application.TableInfo.TableData;

public class XMLExporter {

    public boolean exportTable(TableData data, String fileName) throws ParserConfigurationException, TransformerException {


	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder;
	    docBuilder = docFactory.newDocumentBuilder();
	    // root elements
	    Document doc = docBuilder.newDocument();
	    Element rootElement = doc.createElement("Table");
	    doc.appendChild(rootElement);

	    rootElement.setAttribute("TableName", data.getTableName());
	    
	    data.getValues().forEach((entry) ->{
		 Element element = doc.createElement("Entry");
		 entry.getDbEntryValueMap().forEach( (key,value) ->{
		     Element valueElement = doc.createElement(key);
		     valueElement.setTextContent(value);
		     element.appendChild(valueElement);
		 });
		 rootElement.appendChild(element);
	    });
	    
	    
	    // write the content into xml file
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(new File(fileName));

	    // Output to console for testing
	    // StreamResult result = new StreamResult(System.out);

	    transformer.transform(source, result);

	return true;
    }

}
