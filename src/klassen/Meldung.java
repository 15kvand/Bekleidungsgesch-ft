package klassen;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class Meldung {
	private String text;

	public Meldung() { }

	// Konstruktor mit Text oder XML
	public Meldung(String text) {
		super();
		if (text == null || text.length() == 0)
			return;
		if (!text.startsWith("<")) {
			this.text = text;
		} else {
			deserializeXML(text);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}


	// Serialisiert die Meldung in ein XML-Format für die Übertragung.
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw);
			XMLUtilities.generateStartTag("meldung");
			XMLUtilities.generateElement("meldungText", text);
			XMLUtilities.generateEndTag("meldung");
			XMLUtilities.stopGeneration();
			return sw.toString();
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null;
		}
	}


	// Deserialisiert die Meldung aus einer XML-Zeichenkette.
	public void deserializeXML(String xmlString) {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try {
			XMLEventReader eventReader = inputFactory.createXMLEventReader(new StringReader(xmlString));
			String text = null;
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isCharacters()) {
					text = event.asCharacters().getData();
				} else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("meldung")) {
					this.text = text;
					break;
				}
			}
			eventReader.close();
		} catch (XMLStreamException e) {
			this.text = null;
		}
	}
}
