package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class MitarbeiterList {
	private ArrayList<Mitarbeiter> mitarbeiter; // Liste von Mitarbeiter-Objekten

	// Konstruktor, der eine vorhandene Liste von Mitarbeitern übernimmt
	public MitarbeiterList(ArrayList<Mitarbeiter> mitarbeiter) {
		this.mitarbeiter = mitarbeiter;
	}

	// Standardkonstruktor, der eine leere Liste initialisiert
	public MitarbeiterList() {
		this.mitarbeiter = new ArrayList<>();
	}

	// Konstruktor, der eine XML-Zeichenkette übernimmt und daraus die Liste der Mitarbeiter deserialisiert
	public MitarbeiterList(String xmlString) {
		this.mitarbeiter = new ArrayList<>();
		if (xmlString != null && !xmlString.isEmpty()) {
			deserializeXML(xmlString);
		}
	}

	public ArrayList<Mitarbeiter> getMitarbeiter() {
		return mitarbeiter;
	}

	public void setMitarbeiter(ArrayList<Mitarbeiter> mitarbeiter) {
		this.mitarbeiter = mitarbeiter;
	}


	// Serialisiert die Mitarbeiterliste in ein XML-Format.
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
			serializeMitarbeiterListXML(); // Serialisiert die Liste der Mitarbeiter
			XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
			return sw.toString(); // Gibt den XML-String zurück
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null; // Gibt null zurück, wenn eine Ausnahme auftritt
		}
	}


	// Hilfsmethode zur Serialisierung der Mitarbeiterliste in XML.
	private void serializeMitarbeiterListXML() throws XMLStreamException {
		XMLUtilities.generateStartTag("mitarbeiterliste"); // Start-Tag <mitarbeiterliste>
		for (Mitarbeiter mitarbeiter : mitarbeiter) {
			mitarbeiter.serializeMitarbeiterXML(); // Serialisiert jedes Mitarbeiter-Objekt in der Liste
		}
		XMLUtilities.generateEndTag("mitarbeiterliste"); // End-Tag </mitarbeiterliste>
	}


	// Deserialisiert die Mitarbeiterliste aus einer XML-Zeichenkette.
	public void deserializeXML(String xmlString) {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try (StringReader sr = new StringReader(xmlString)) {
			XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("mitarbeiterliste")) {
					deserializeMitarbeiterListXML(eventReader); // Deserialisiert die Mitarbeiterdaten aus der XML-Zeichenkette
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}


	// Hilfsmethode zur Deserialisierung der Mitarbeiterliste aus einem XMLEventReader.
	private void deserializeMitarbeiterListXML(XMLEventReader eventReader) throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("mitarbeiter")) {
				Mitarbeiter neuerMitarbeiter = new Mitarbeiter(); // Erstellt ein neues Mitarbeiter-Objekt
				neuerMitarbeiter.deserializeMitarbeiterXML(eventReader); // Deserialisiert die XML-Daten in das Mitarbeiter-Objekt
				mitarbeiter.add(neuerMitarbeiter); // Fügt das deserialisierte Mitarbeiter-Objekt zur Liste hinzu
			} else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("mitarbeiterliste")) {
				return; // Beendet die Deserialisierung, wenn das End-Tag </mitarbeiterliste> erreicht ist
			}
		}
	}

	@Override
	public String toString() {
		return "MitarbeiterList [mitarbeiter=" + mitarbeiter + "]";
	}
}
