package klassen;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class WarenkorbElement {
	private int id; // Eindeutige Identifikation des WarenkorbElements
	private Warenkorb warenkorb; // Der Warenkorb, zu dem dieses Element gehört
	private Artikel artikel; // Artikelobjekt, das den Artikel im Warenkorb repräsentiert
	private int menge; // Anzahl des Artikels im Warenkorb
	private int status; // Status des Artikels (1 = in Warenkorb, 2 = bestellt, 3 = verschickt)

	public WarenkorbElement(int id, Warenkorb warenkorb, Artikel artikel, int menge, int status) {
		this.id = id;
		this.warenkorb = warenkorb;
		this.artikel = artikel;
		this.menge = menge;
		this.status = status;
	}

	public WarenkorbElement() {}

	public WarenkorbElement(Artikel artikel) {
		this.artikel = artikel;
		this.menge = 1; // Standardmäßig 1 Menge
		this.status = 1; // Standardmäßig  "in Warenkorb"
	}


	// Konstruktor, der ein Warenkorb-Element aus einer XML-Zeichenkette erstellt.
	public WarenkorbElement(String xmlString) {
		this(); // Den leeren Konstruktor aufrufen, um Standardwerte zu setzen
		if (xmlString != null && !xmlString.isEmpty()) {
			deserializeXML(xmlString);
		}
	}

	public String getArtikelName() {
		return artikel != null ? artikel.getName() : null;
	}

	public String getArtikelBeschreibung() {
		return artikel != null ? artikel.getBeschreibung() : null;
	}

	public String getArtikelGroesse() {
		return artikel != null ? artikel.getGroesse() : null;
	}

	public int getArtikelPreis() {
		return artikel != null ? artikel.getPreis() : 0;
	}


	// Gibt die ID des Warenkorb-Elements zurück.
	public int getId() {
		return id;
	}


	// Setzt die ID des Warenkorb-Elements.
	public void setId(int id) {
		this.id = id;
	}

	public Warenkorb getWarenkorb() {
		return warenkorb;
	}

	public void setWarenkorb(Warenkorb warenkorb) {
		this.warenkorb = warenkorb;
	}


	// Gibt den Artikel zurück.
	public Artikel getArtikel() {
		return artikel;
	}


	// Setzt den Artikel.
	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}


	// Gibt die Menge des Artikels zurück.
	public int getMenge() {
		return menge;
	}


	// Setzt die Menge des Artikels.
	public void setMenge(int menge) {
		this.menge = menge;
	}


	// Gibt den Status des Artikels zurück.
	public int getStatus() {
		return status;
	}


	// Setzt den Status des Artikels.
	public void setStatus(int status) {
		this.status = status;
	}


	@Override
	public String toString() {
		return "WarenkorbElement [id=" + id + ", artikel=" + artikel + ", menge=" + menge + ", status=" + status + "]";
	}


	// Serialisiert das Warenkorb-Element in ein XML-Format.
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
			serializeWarenkorbElementXML(); // Serialisiert die WarenkorbElement-Daten
			XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
			return sw.toString(); // Gibt den XML-String zurück
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null; // Gibt null zurück, wenn eine Ausnahme auftritt
		}
	}

	// Methode zur Serialisierung der WarenkorbElementdaten
	public void serializeWarenkorbElementXML() throws XMLStreamException {
		XMLUtilities.generateStartTag("warenkorbelement"); // Start-Tag <warenkorbelement>
		XMLUtilities.generateElement("id", Integer.toString(id)); // Element <id>
		if (warenkorb != null) {
			XMLUtilities.generateElement("warenkorbId", Integer.toString(warenkorb.getId()));
		}
		artikel.serializeArtikelXML(); // Serialisiert das Artikelobjekt innerhalb des WarenkorbElements
		XMLUtilities.generateElement("menge", Integer.toString(menge)); // Element <menge>
		XMLUtilities.generateElement("status", Integer.toString(status)); // Element <status>
		XMLUtilities.generateEndTag("warenkorbelement"); // End-Tag </warenkorbelement>
	}


	// Deserialisiert den WarenkorbElement aus einer XML-Zeichenkette.
	public void deserializeXML(String xmlString) {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try (StringReader sr = new StringReader(xmlString)) {
			XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("warenkorbelement")) {
					deserializeWarenkorbElementXML(eventReader); // Deserialisiert die WarenkorbElement-Daten
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}


	// Hilfsmethode zur Deserialisierung des WarenkorbElements aus einem XMLEventReader.
	@SuppressWarnings("exports")
	public void deserializeWarenkorbElementXML(XMLEventReader eventReader) throws XMLStreamException {
		String text = null;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				String elementName = event.asStartElement().getName().getLocalPart();
				if (elementName.equals("artikel")) {
					artikel = new Artikel();
					artikel.deserializeArtikelXML(eventReader);
				}
				// 'warenkorb' ist optional, daher wird es nur behandelt, wenn es vorhanden ist
				else if (elementName.equals("warenkorb")) {
					warenkorb = new Warenkorb();
					warenkorb.deserializeWarenkorbXML(eventReader);
				}
			} else if (event.isCharacters()) {
				text = event.asCharacters().getData();
			} else if (event.isEndElement()) {
				switch (event.asEndElement().getName().getLocalPart()) {
				case "id" -> id = Integer.parseInt(text);
				case "menge" -> menge = Integer.parseInt(text);
				case "status" -> status = Integer.parseInt(text);
				case "warenkorbelement" -> {
					return;
				}
				}
			}
		}
	}
}