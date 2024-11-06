package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class Warenkorb {
	private int id; // Warenkorb ID
	private ArrayList<WarenkorbElement> elemente; // Liste von WarenkorbElementen
	private Kunde kunde; // Der Kunde, der den Warenkorb besitzt
	private LocalDate datum; // Das Datum des Warenkorbs
	private int status; // status 1 - offen(aktiver Warenkorb), 2 - abgeschlossen(bestellt)

	// Konstruktor, der die ID, die Liste der WarenkorbElemente, den Kunden und das Datum setzt
	public Warenkorb(int id, ArrayList<WarenkorbElement> elemente, Kunde kunde, LocalDate datum, int status) {
		this.id = id;
		this.elemente = elemente;
		this.kunde = kunde;
		this.datum = datum;
		this.status = status;

	}

	// Standardkonstruktor, der eine leere Liste und das aktuelle Datum initialisiert
	public Warenkorb() {
		this.elemente = new ArrayList<>();
		this.datum = LocalDate.now();
		this.status = 1; // Standardmäßig auf "offen" setzen
	}


	// Konstruktor, der ein Warenkorbobjekt aus einer XML-Zeichenkette erstellt.
	public Warenkorb(String xmlString) {
		this.elemente = new ArrayList<>();
		this.datum = LocalDate.now();
		this.status = 1; 
		if (xmlString != null && !xmlString.isEmpty()) {
			deserializeXML(xmlString);
		}
	}


	// Gibt die Warenkorb-ID zurück.
	public int getId() {
		return id;
	}


	// Setzt die Warenkorb-ID.
	public void setId(int id) {
		this.id = id;
	}



	public int getStatus() {
		return status;
	}



	public void setStatus(int status) {
		this.status = status;
	}


	// Gibt die Liste der Warenkorb-Elemente zurück.
	public ArrayList<WarenkorbElement> getElemente() {
		return elemente;
	}


	// Setzt die Warenkorb-Elemente.
	public void setElemente(ArrayList<WarenkorbElement> elemente) {
		this.elemente = elemente;
	}


	// Gibt den zugehörigen Kunden zurück.
	public Kunde getKunde() {
		return kunde;
	}


	// Setzt den Kunden.
	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}


	// Gibt das Datum zurück.
	public LocalDate getDatum() {
		return datum;
	}


	// Setzt das Datum.
	public void setDatum(LocalDate datum) {
		this.datum = datum;
	}


	// Serialisiert den Warenkorb in ein XML-Format.
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
			serializeWarenkorbXML(); // Serialisiert die Warenkorbdaten
			XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
			return sw.toString(); // Gibt den XML-String zurück
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null; // Gibt null zurück, wenn eine Ausnahme auftritt
		}
	}


	// Methode zur Serialisierung der Warenkorbdaten
	public void serializeWarenkorbXML() throws XMLStreamException {
		XMLUtilities.generateStartTag("warenkorb"); // Start-Tag <warenkorb>
		XMLUtilities.generateElement("id", String.valueOf(id)); // Element <id>
		kunde.serializeKundeXML(); // Serialisiert die Kundendaten
		XMLUtilities.generateElement("datum", datum.toString()); // Element <datum>
		XMLUtilities.generateElement("status", Integer.toString(status)); // Element <status>
		for (WarenkorbElement element : elemente) {
			element.serializeWarenkorbElementXML(); // Serialisiert jedes WarenkorbElement-Objekt in der Liste
		}
		XMLUtilities.generateEndTag("warenkorb"); // End-Tag </warenkorb>
	}


	// Deserialisiert den Warenkorb aus einer XML-Zeichenkette.
	public void deserializeXML(String xmlString) {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try (StringReader sr = new StringReader(xmlString)) {
			XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("warenkorb")) {
					deserializeWarenkorbXML(eventReader); // Deserialisiert die Warenkorbdaten
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}


	// Hilfsmethode zur Deserialisierung des Warenkorbs aus einem XMLEventReader.
	@SuppressWarnings("exports")
	public void deserializeWarenkorbXML(XMLEventReader eventReader) throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				String elementName = event.asStartElement().getName().getLocalPart();
				if (elementName.equals("id")) {
					event = eventReader.nextEvent();
					this.id = Integer.parseInt(event.asCharacters().getData());
				} else if (elementName.equals("kunde")) {
					kunde = new Kunde(); // Erstellt ein neues Kunde-Objekt
					kunde.deserializeKundeXML(eventReader); // Deserialisiert die XML-Daten in das Kunde-Objekt
				} else if (elementName.equals("warenkorbelement")) {
					WarenkorbElement neuesElement = new WarenkorbElement(); // Erstellt ein neues WarenkorbElement-Objekt
					neuesElement.deserializeWarenkorbElementXML(eventReader); // Deserialisiert die XML-Daten in das WarenkorbElement-Objekt
					elemente.add(neuesElement); // Fügt das deserialisierte WarenkorbElement-Objekt zur Liste hinzu
				} else if (elementName.equals("datum")) {
					event = eventReader.nextEvent(); // Holt den Textinhalt des <datum> Elements
					datum = LocalDate.parse(event.asCharacters().getData()); // Setzt das Datum
				} else if (elementName.equals("status")) {
					event = eventReader.nextEvent();
					status = Integer.parseInt(event.asCharacters().getData());
				}
			} else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("warenkorb")) {
				return; // Beendet die Deserialisierung, wenn das End-Tag </warenkorb> erreicht ist
			}
		}
	}

	@Override
	public String toString() {
		return "Warenkorb [id=" + id + ", elemente=" + elemente.size() + " Elemente, kunde=" + kunde + ", datum=" + datum + ", status=" + status + "]";
	}
}