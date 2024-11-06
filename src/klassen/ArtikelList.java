package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class ArtikelList {
	private ArrayList<Artikel> artikel; // Liste von Artikel-Objekten

    // Konstruktor, der eine vorhandene Liste von Artikeln übernimmt
    public ArtikelList(ArrayList<Artikel> artikel) {
        this.artikel = artikel;
    }

    // Standardkonstruktor, der eine leere Liste initialisiert
    public ArtikelList() {
        this.artikel = new ArrayList<>();
    }

    // Konstruktor, der eine Artikelliste aus einer XML-Zeichenkette erstellt.
    public ArtikelList(String xmlString) {
        this.artikel = new ArrayList<>();
        if (xmlString != null && !xmlString.isEmpty()) {
            deserializeXML(xmlString);
        }
    }

    
    // Gibt die Artikelliste zurück.
    public ArrayList<Artikel> getArtikel() {
        return artikel;
    }

    
    // Setzt die Artikelliste.
    public void setArtikel(ArrayList<Artikel> artikel) {
        this.artikel = artikel;
    }

    
    // Serialisiert die Artikelliste in ein XML-Format.
    public String serializeXML() {
        StringWriter sw = new StringWriter();
        try {
            XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
            serializeArtikelListXML(); // Serialisiert die Liste der Artikel
            XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
            return sw.toString(); // Gibt den XML-String zurück
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return null; // Gibt null zurück, wenn eine Ausnahme auftritt
        }
    }

    
    // Hilfsmethode zur Serialisierung der Artikelliste in XML.
    private void serializeArtikelListXML() throws XMLStreamException {
        XMLUtilities.generateStartTag("artikelliste");
        for (Artikel artikel : artikel) {
            artikel.serializeArtikelXML();
        }
        XMLUtilities.generateEndTag("artikelliste");
    }

    
    // Deserialisiert die Artikelliste aus einer XML-Zeichenkette.
    public void deserializeXML(String xmlString) {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try (StringReader sr = new StringReader(xmlString)) {
            XMLEventReader eventReader = inputFactory.createXMLEventReader(sr);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("artikelliste")) {
                    deserializeArtikelListXML(eventReader); // Deserialisiert die Artikeldaten aus der XML-Zeichenkette
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    
   // Hilfsmethode zur Deserialisierung der Artikelliste aus einem XMLEventReader.
    private void deserializeArtikelListXML(XMLEventReader eventReader) throws XMLStreamException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals("artikel")) {
                Artikel neuerArtikel = new Artikel(); // Erstellt ein neues Artikel-Objekt
                neuerArtikel.deserializeArtikelXML(eventReader); // Deserialisiert die XML-Daten in das Artikel-Objekt
                artikel.add(neuerArtikel); // Fügt das deserialisierte Artikel-Objekt zur Liste hinzu
            } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("artikelliste")) {
                return; // Beendet die Deserialisierung, wenn das End-Tag </artikelliste> erreicht ist
            }
        }
    }

    @Override
    public String toString() {
        return "ArtikelList [artikel=" + artikel + "]";
    }
}
