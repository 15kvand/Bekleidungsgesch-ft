package klassen;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

public class WarenkorbList {
	private ArrayList<Warenkorb> warenkoerbe;

	public WarenkorbList() {
		warenkoerbe = new ArrayList<>();
	}

	public WarenkorbList(String xmlString) {
		this();
		if (xmlString != null && !xmlString.isEmpty()) {
			deserializeXML(xmlString);
		}
	}

	public ArrayList<Warenkorb> getWarenkoerbe() {
		return warenkoerbe;
	}

	public void setWarenkoerbe(ArrayList<Warenkorb> warenkoerbe) {
		this.warenkoerbe = warenkoerbe;
	}


	// Serialisiert die Warenkorbliste in ein XML-Format.
	public String serializeXML() {
		StringWriter sw = new StringWriter();
		try {
			XMLUtilities.startGeneration(sw); // Initialisiert die XML-Erstellung
			serializeWarenkorbListXML(); // Serialisiert die Warenkorb-Liste
			XMLUtilities.stopGeneration(); // Beendet die XML-Erstellung
			return sw.toString(); // Gibt den XML-String zurück
		} catch (XMLStreamException e) {
			e.printStackTrace();
			return null; // Gibt null zurück, wenn eine Ausnahme auftritt
		}
	}


	// Deserialisiert die Warenkorbliste aus einer XML-Zeichenkette.
	private void deserializeXML(String xmlString) {
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new StringReader(xmlString));
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement() && "warenkorb".equals(event.asStartElement().getName().getLocalPart())) {
					Warenkorb warenkorb = new Warenkorb();
					warenkorb.deserializeWarenkorbXML(eventReader);
					warenkoerbe.add(warenkorb);
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}


	// Hilfsmethode zur Serialisierung der Warenkorbliste in XML.
	private void serializeWarenkorbListXML() throws XMLStreamException {
		XMLUtilities.generateStartTag("warenkorbliste"); // Start-Tag <warenkorbliste>
		for (Warenkorb warenkorb : warenkoerbe) {
			warenkorb.serializeWarenkorbXML(); // Serialisiert jedes Warenkorb-Objekt in der Liste
		}
		XMLUtilities.generateEndTag("warenkorbliste"); // End-Tag </warenkorbliste>
	}


	// Hilfsmethode zur Deserialisierung der Warenkorbliste aus einem XMLEventReader.
	@SuppressWarnings("unused")
	private void deserializeWarenkorbListXML(XMLEventReader eventReader) throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (event.isStartElement() && "warenkorb".equals(event.asStartElement().getName().getLocalPart())) {
				Warenkorb warenkorb = new Warenkorb();
				warenkorb.deserializeWarenkorbXML(eventReader);
				warenkoerbe.add(warenkorb);
			} else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("warenkorbliste")) {
				return; // Beendet die Deserialisierung, wenn das End-Tag </warenkorbliste> erreicht ist
			}
		}
	}
}