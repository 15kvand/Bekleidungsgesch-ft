package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

// Mitarbeiter erbt von Benutzer
public class Mitarbeiter extends Benutzer {

	private String iban;
	private int gehalt;
	private String rolle;

	public Mitarbeiter(int id, String name, String passwort, String email, String adresse, String telNummer, String iban, String plz, int gehalt, String rolle) {
		super(name, passwort, email, telNummer, adresse, id, plz); // Konstruktor der Basisklasse aufrufen
		this.iban = iban;
		this.gehalt = gehalt;
		this.rolle = rolle;
	}

	// Leerer Konstruktor mit Standardwerten
	public Mitarbeiter() {
		super("", "", "", "", "", 0, ""); // Konstruktor der Basisklasse mit Standardwerten aufrufen
		this.iban = "";
		this.gehalt = 0; // Standardwert für das Gehalt
		this.rolle = "";
	}

	// Konstruktor, der ein Mitarbeiterobjekt aus einer XML-Zeichenkette erstellt.
	public Mitarbeiter(String xmlString) {
		this(); // Den leeren Konstruktor aufrufen, um Standardwerte zu setzen
		if (xmlString != null && !xmlString.isEmpty()) {
			deserializeXML(xmlString);
		}
	}


	// Serialisiert den Mitarbeiter in ein XML-Format.
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
			serializeMitarbeiterXML(); // Serialisiert die Mitarbeiterdaten
			XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
			return sw.toString(); // Gibt den XML-String zurück
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null; // Gibt null zurück, wenn eine Ausnahme auftritt
		}
	}


	// Methode zur Serialisierung der Mitarbeiterdaten
	public void serializeMitarbeiterXML() throws XMLStreamException {
		XMLUtilities.generateStartTag("mitarbeiter"); // Start-Tag <mitarbeiter>
		XMLUtilities.generateElement("id", Integer.toString(id)); // Element <id>
		if (name != null) {
			XMLUtilities.generateElement("name", name); // Element <name>
		}
		if (passwort != null) {
			XMLUtilities.generateElement("passwort", passwort); // Element <passwort>
		}
		if (email != null) {
			XMLUtilities.generateElement("email", email); // Element <email>
		}
		if (adresse != null) {
			XMLUtilities.generateElement("adresse", adresse); // Element <adresse>
		}
		if (telNummer != null) {
			XMLUtilities.generateElement("telNummer", telNummer); // Element <telNummer>
		}
		if (iban != null) {
			XMLUtilities.generateElement("iban", iban); // Element <iban>
		}
		if (plz != null) {
			XMLUtilities.generateElement("plz", plz); // Element <plz>
		}
		XMLUtilities.generateElement("gehalt", Integer.toString(gehalt)); // Element <gehalt>
		if (rolle != null) {
			XMLUtilities.generateElement("rolle", rolle); // Element <rolle>
		}
		XMLUtilities.generateEndTag("mitarbeiter"); // End-Tag </mitarbeiter>
	}


	// Deserialisiert den Mitarbeiter aus einer XML-Zeichenkette.
	public void deserializeXML(String xmlString) {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try (StringReader sr = new StringReader(xmlString)) {
			XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("mitarbeiter")) {
					deserializeMitarbeiterXML(eventReader); // Deserialisiert die Mitarbeiterdaten
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}


	// Hilfsmethode zur Deserialisierung des Mitarbeiters aus einem XMLEventReader.
	@SuppressWarnings("exports")
	public void deserializeMitarbeiterXML(XMLEventReader eventReader) throws XMLStreamException {
		String text = null;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isCharacters()) {
				text = event.asCharacters().getData(); // Liest den Textinhalt des aktuellen Elements
			} else if (event.isEndElement()) {
				switch (event.asEndElement().getName().getLocalPart()) {
				case "id" -> id = Integer.parseInt(text);
				case "name" -> name = text;
				case "passwort" -> passwort = text;
				case "email" -> email = text;
				case "adresse" -> adresse = text;
				case "telNummer" -> telNummer = text;
				case "iban" -> iban = text;
				case "plz" -> plz = text;
				case "gehalt" -> gehalt = Integer.parseInt(text);
				case "rolle" -> rolle = text;
				case "mitarbeiter" -> { return; } // Ende der Mitarbeiter-Tag-Verarbeitung
				}
			}
		}
	}


	// Gibt die Rolle zurück.
	public String getRolle() {
		return rolle;
	}


	// Setzt die Rolle.
	public void setRolle(String rolle) {
		this.rolle = rolle;
	}


	// Gibt die IBAN zurück.
	public String getIban() {
		return iban;
	}


	// Setzt die IBAN.
	public void setIban(String iban) {
		this.iban = iban;
	}


	// Gibt das Gehalt zurück.
	public int getGehalt() {
		return gehalt;
	}


	// Setzt das Gehalt.
	public void setGehalt(int gehalt) {
		this.gehalt = gehalt;
	}

	@Override
	public String toString() {
		return "Mitarbeiter [id=" + id + ", name=" + name + ", passwort=" + passwort + ", email=" + email + 
				", adresse=" + adresse + ", telNummer=" + telNummer + ", iban=" + iban + 
				", plz=" + plz + ", gehalt=" + gehalt + "]";
	}
}