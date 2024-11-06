package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class KundeList {
	private ArrayList<Kunde> kunden; // Liste von Kunde-Objekten

    // Konstruktor, der eine vorhandene Liste von Kunden übernimmt
    public KundeList(ArrayList<Kunde> kunden) {
        this.kunden = kunden;
    }

    // Standardkonstruktor, der eine leere Liste initialisiert
    public KundeList() {
        this.kunden = new ArrayList<>();
    }

    // Konstruktor, der eine XML-Zeichenkette übernimmt und daraus die Liste der Kunden deserialisiert
    public KundeList(String xmlString) {
        this.kunden = new ArrayList<>();
        if (xmlString != null && !xmlString.isEmpty()) {
            deserializeXML(xmlString);
        }
    }

    public ArrayList<Kunde> getKunden() {
        return kunden;
    }

    public void setKunden(ArrayList<Kunde> kunden) {
        this.kunden = kunden;
    }

    
    // Serialisiert die Kundenliste in ein XML-Format.
    public String serializeXML() {
        StringWriter sw = new StringWriter();
        try {
            XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
            serializeKundeListXML(); // Serialisiert die Liste der Kunden
            XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
            return sw.toString(); // Gibt den XML-String zurück
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return null; // Gibt null zurück, wenn eine Ausnahme auftritt
        }
    }

    
    // Hilfsmethode zur Serialisierung der Kundenliste in XML.
    private void serializeKundeListXML() throws XMLStreamException {
        XMLUtilities.generateStartTag("kundenliste"); // Start-Tag <kundenliste>
        for (Kunde kunde : kunden) {
            kunde.serializeKundeXML(); // Serialisiert jedes Kunde-Objekt in der Liste
        }
        XMLUtilities.generateEndTag("kundenliste"); // End-Tag </kundenliste>
    }

    
    // Deserialisiert die Kundenliste aus einer XML-Zeichenkette.
    public void deserializeXML(String xmlString) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try (StringReader sr = new StringReader(xmlString)) {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("kundenliste")) {
                    deserializeKundeListXML(eventReader); // Deserialisiert die Kundendaten aus der XML-Zeichenkette
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    
    // Hilfsmethode zur Deserialisierung der Kundenliste aus einem XMLEventReader.
    private void deserializeKundeListXML(XMLEventReader eventReader) throws XMLStreamException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("kunde")) {
                Kunde neuerKunde = new Kunde(); // Erstellt ein neues Kunde-Objekt
                neuerKunde.deserializeKundeXML(eventReader); // Deserialisiert die XML-Daten in das Kunde-Objekt
                kunden.add(neuerKunde); // Fügt das deserialisierte Kunde-Objekt zur Liste hinzu
            } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("kundenliste")) {
                return; // Beendet die Deserialisierung, wenn das End-Tag </kundenliste> erreicht ist
            }
        }
    }

    @Override
    public String toString() {
        return "KundeList [kunden=" + kunden + "]";
    }

}
