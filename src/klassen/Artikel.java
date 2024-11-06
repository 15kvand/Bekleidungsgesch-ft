package klassen;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


public class Artikel {
	private int artikelNummer;
	private String name;
	private String beschreibung;
	private int preis;
	private String groesse;
	private byte[] bild;
	private int anzahl;
	private boolean deaktiv;

	// Konstruktoren
	public Artikel(int artikelNummer, String name, String beschreibung, int preis, String groesse, byte[] bild, int anzahl, boolean deaktiv) {
		this.artikelNummer = artikelNummer;
		this.name = name;
		this.beschreibung = beschreibung;
		this.preis = preis;
		this.groesse = groesse;
		this.bild = bild;
		this.anzahl = anzahl;
		this.deaktiv = deaktiv;
	}

	public Artikel(int artikelNummer, String name, String beschreibung, int preis, String groesse, byte[] bild, int anzahl) {
		this(artikelNummer, name, beschreibung, preis, groesse, bild, anzahl, false);
	}

	public Artikel() {
		this.deaktiv = false;
	}


	// Konstruktor, der ein Artikelobjekt aus einer XML-Zeichenkette erstellt.
	public Artikel(String xmlString) {
		deserializeXML(xmlString);
	}


	// Überprüft, ob der Artikel deaktiviert ist.
	public boolean isDeaktiv() {
		return deaktiv;
	}


	// Setzt den Aktivierungsstatus des Artikels.
	public void setDeaktiv(boolean deaktiv) {
		this.deaktiv = deaktiv;
	}


	// Gibt die Artikelnummer zurück.
	public int getArtikelNummer() {
		return artikelNummer;
	}


	// Setzt die Artikelnummer.
	public void setArtikelNummer(int artikelNummer) {
		this.artikelNummer = artikelNummer;
	}


	// Gibt den Namen des Artikels zurück.
	public String getName() {
		return name;
	}


	// Setzt den Namen des Artikels.
	public void setName(String name) {
		this.name = name;
	}


	// Gibt die Beschreibung des Artikels zurück.
	public String getBeschreibung() {
		return beschreibung;
	}


	// Setzt die Beschreibung des Artikels.
	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}


	// Gibt den Preis des Artikels zurück.
	public int getPreis() {
		return preis;
	}


	// Setzt den Preis des Artikels.
	public void setPreis(int preis) {
		this.preis = preis;
	}


	// Gibt die Größe des Artikels zurück.
	public String getGroesse() {
		return groesse;
	}


	// Setzt die Größe des Artikels.
	public void setGroesse(String groesse) {
		this.groesse = groesse;
	}


	// Gibt das Bild des Artikels zurück.
	public byte[] getBild() {
		return bild;
	}


	// Setzt das Bild des Artikels.
	public void setBild(byte[] bild) {
		this.bild = bild;
	}


	// Gibt die verfügbare Menge des Artikels zurück.
	public int getAnzahl() {
		return anzahl;
	}


	// Setzt die verfügbare Menge des Artikels.
	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	@Override
	public String toString() {
		return "Artikel [artikelNummer=" + artikelNummer + ", name=" + name + ", beschreibung=" + beschreibung +
				", preis=" + preis + ", groesse=" + groesse + ", bild=" + bild + ", anzahl=" + anzahl +
				", deaktiv=" + deaktiv + "]";
	}

	// Serialisiert den Artikel in ein XML-Format für die Übertragung.
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
			serializeArtikelXML(); // Serialisiert die Artikeldaten
			XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
			return sw.toString(); // Gibt den XML-String zurück
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null; // Gibt null zurück, wenn eine Ausnahme auftritt
		}
	}

	
	// Deserialisiert den Artikel aus einer XML-Zeichenkette.
	public void deserializeXML(String xmlString) {
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		try (StringReader sr = new StringReader(xmlString)) {
			XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("artikel")) {
					deserializeArtikelXML(eventReader); // Hier wird die XML-Daten in den Artikel-Objekt deserialisiert
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	
	// Hilfsmethode zur Serialisierung des Artikels in XML.
	public void serializeArtikelXML() throws XMLStreamException {
		XMLUtilities.generateStartTag("artikel");
		XMLUtilities.generateElement("artikelNummer", Integer.toString(artikelNummer));
		if (name != null) {
			XMLUtilities.generateElement("name", name);
		}
		if (beschreibung != null) {
			XMLUtilities.generateElement("beschreibung", beschreibung);
		}
		XMLUtilities.generateElement("preis", Integer.toString(preis));
		if (groesse != null) {
			XMLUtilities.generateElement("groesse", groesse);
		}
		if (bild != null && bild.length > 0) {
			String base64Bild = Base64.getEncoder().encodeToString(bild);
			XMLUtilities.generateCDataElement("bild", base64Bild);
		}
		XMLUtilities.generateElement("anzahl", Integer.toString(anzahl));
		XMLUtilities.generateElement("deaktiv", Boolean.toString(deaktiv));
		XMLUtilities.generateEndTag("artikel");
	}

	
	// Hilfsmethode zur Deserialisierung des Artikels aus einem XMLEventReader.
	@SuppressWarnings("exports")
	public void deserializeArtikelXML(XMLEventReader eventReader) throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement()) {
				String elementName = event.asStartElement().getName().getLocalPart();
				StringBuilder textBuilder = new StringBuilder();
				while (eventReader.hasNext()) {
					event = eventReader.nextEvent();
					if (event.isCharacters()) {
						textBuilder.append(event.asCharacters().getData());
					} else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(elementName)) {
						break; 
					}
				}
				String text = textBuilder.toString();
				switch (elementName) {
				case "artikelNummer":
					this.artikelNummer = Integer.parseInt(text);
					break;
				case "name":
					this.name = text;
					break;
				case "beschreibung":
					this.beschreibung = text;
					break;
				case "preis":
					this.preis = Integer.parseInt(text);
					break;
				case "groesse":
					this.groesse = text;
					break;
				case "bild":
					if (!text.isEmpty()) {
						try {
							String base64Text = text.replaceAll("\\s+", "");
							this.bild = Base64.getDecoder().decode(base64Text);
						} catch (IllegalArgumentException e) {
							this.bild = null;
						}
					} else {
						this.bild = null;
					}
					break;
				case "anzahl":
					this.anzahl = Integer.parseInt(text);
					break;
				case "deaktiv":
					this.deaktiv = Boolean.parseBoolean(text);
					break;
				}
			} else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("artikel")) {

				return;
			}
		}
	}

}