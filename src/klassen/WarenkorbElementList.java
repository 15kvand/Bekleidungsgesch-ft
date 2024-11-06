package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class WarenkorbElementList {
	private ArrayList<WarenkorbElement> warenkorbElemente;

	// Konstruktor für leere WarenkorbElementList
	public WarenkorbElementList() {
		warenkorbElemente = new ArrayList<>();
	}

	// Konstruktor zum Initialisieren der WarenkorbElementList mit XML-String
	public WarenkorbElementList(String xmlString) {
		this();
		if (xmlString != null && !xmlString.isEmpty()) {
			deserializeXML(xmlString);
		}
	}

	// Konstruktor, der eine Liste von WarenkorbElementen übernimmt
	public WarenkorbElementList(List<WarenkorbElement> elementeListe) {
		this.warenkorbElemente = new ArrayList<>(elementeListe);
	}

	// Getter für die Liste der WarenkorbElemente
	public ArrayList<WarenkorbElement> getWarenkorbElemente() {
		return warenkorbElemente;
	}

	// Setter für die Liste der WarenkorbElemente
	public void setWarenkorbElemente(ArrayList<WarenkorbElement> warenkorbElemente) {
		this.warenkorbElemente = warenkorbElemente;
	}

	// Serialisierungsmethode für die gesamte WarenkorbElementList
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
			serializeWarenkorbElementListXML(); // Serialisiert die WarenkorbElement-Liste
			XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
			return sw.toString(); // Gibt den XML-String zurück
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null; // Gibt null zurück, wenn eine Ausnahme auftritt
		}
	}

	// Methode zum Serialisieren der WarenkorbElemente in der Liste
	private void serializeWarenkorbElementListXML() throws XMLStreamException {
		XMLUtilities.generateStartTag("warenkorbElementListe"); // Start-Tag <warenkorbElementListe>
		for (WarenkorbElement element : warenkorbElemente) {
			element.serializeWarenkorbElementXML(); // Serialisiert jedes WarenkorbElement-Objekt in der Liste
		}
		XMLUtilities.generateEndTag("warenkorbElementListe"); // End-Tag </warenkorbElementListe>
	}

	// Methode zum Deserialisieren eines XML-Strings in die WarenkorbElementList
	private void deserializeXML(String xmlString) {
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new StringReader(xmlString));
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement() && "warenkorbelement".equals(event.asStartElement().getName().getLocalPart())) {
					WarenkorbElement element = new WarenkorbElement();
					element.deserializeWarenkorbElementXML(eventReader);
					warenkorbElemente.add(element);
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	// Deserialisiert die Liste aus einem XMLEventReader
	@SuppressWarnings("unused")
	private void deserializeWarenkorbElementListXML(XMLEventReader eventReader) throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement() && "warenkorbelement".equals(event.asStartElement().getName().getLocalPart())) {
				WarenkorbElement element = new WarenkorbElement();
				element.deserializeWarenkorbElementXML(eventReader);
				warenkorbElemente.add(element);
			} else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("warenkorbElementListe")) {
				return; // Beendet die Deserialisierung, wenn das End-Tag </warenkorbElementListe> erreicht ist
			}
		}
	}
}