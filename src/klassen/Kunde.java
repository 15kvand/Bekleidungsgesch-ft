package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

// Kunde erbt von Benutzer
public class Kunde extends Benutzer {
	
    private LocalDate registrierungsdatum;

    public Kunde(int id, String name, String passwort, String email, String adresse, String telNummer, String plz, LocalDate registrierungsdatum) {
        super(name, passwort, email, telNummer, adresse, id, plz); 
        this.registrierungsdatum = registrierungsdatum;
    }

    public Kunde() {
        super("", "", "", "", "", 0, ""); // Konstruktor der Basisklasse mit Standardwerten aufrufen
        this.registrierungsdatum = LocalDate.now(); 
    }

    
    // Konstruktor, der ein Kundenobjekt aus einer XML-Zeichenkette erstellt.
    public Kunde(String xmlString) {
        this(); // Den leeren Konstruktor aufrufen, um Standardwerte zu setzen
        if (xmlString != null && !xmlString.isEmpty()) {
            deserializeXML(xmlString);
        }
    }


    // Serialisiert den Kunden in ein XML-Format.
    public String serializeXML() {
        StringWriter sw = new StringWriter();
        try {
            XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
            serializeKundeXML(); // Serialisiert die Kundendaten
            XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
            return sw.toString(); // Gibt den XML-String zurück
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return null; 
        }
    }

    // Methode zur Serialisierung der Kundendaten
    public void serializeKundeXML() throws XMLStreamException {
    	XMLUtilities.generateStartTag("kunde"); // Start-Tag <kunde>
        if (id != 0) {
            XMLUtilities.generateElement("id", Integer.toString(id)); // Element <id>
        }
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
        if (plz != null) {
            XMLUtilities.generateElement("plz", plz); // Element <plz>
        }
        if (registrierungsdatum != null) {
            // Das Datum im ISO-Format serialisieren
            XMLUtilities.generateElement("registrierungsdatum", registrierungsdatum.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        XMLUtilities.generateEndTag("kunde"); // End-Tag </kunde>
    }

    
    // Deserialisiert den Kunden aus einer XML-Zeichenkette.
    public void deserializeXML(String xmlString) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try (StringReader sr = new StringReader(xmlString)) {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("kunde")) {
                    deserializeKundeXML(eventReader); 
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    
    // Hilfsmethode zur Deserialisierung des Kunden aus einem XMLEventReader.
    @SuppressWarnings("exports")
	public void deserializeKundeXML(XMLEventReader eventReader) throws XMLStreamException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement()) {
                String localPart = event.asStartElement().getName().getLocalPart();
                StringBuilder data = new StringBuilder();
                event = eventReader.nextEvent();
                while (event.isCharacters()) {
                    data.append(event.asCharacters().getData());
                    event = eventReader.nextEvent();
                }
                if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(localPart)) {
                    String text = data.toString().trim();
                    switch (localPart) {
                        case "id" -> id = Integer.parseInt(text);
                        case "name" -> name = text;
                        case "passwort" -> passwort = text;
                        case "email" -> email = text;
                        case "adresse" -> adresse = text;
                        case "telNummer" -> telNummer = text;
                        case "plz" -> plz = text;
                        case "registrierungsdatum" -> registrierungsdatum = LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
                    }
                }
            } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("kunde")) {
                return; 
            }
        }
    }

    
    // Gibt das Registrierungsdatum zurück.
    public LocalDate getRegistrierungsdatum() {
        return registrierungsdatum;
    }

    
    // Setzt das Registrierungsdatum.
    public void setRegistrierungsdatum(LocalDate registrierungsdatum) {
        this.registrierungsdatum = registrierungsdatum;
    }

    @Override
    public String toString() {
        return "Kunde [id=" + id + ", name=" + name + ", passwort=" + passwort + ", email=" + email + ", adresse="
                + adresse + ", telNummer=" + telNummer + ", plz=" + plz + ", registrierungsdatum=" + registrierungsdatum
                + "]";
    }
}