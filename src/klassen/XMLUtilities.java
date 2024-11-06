package klassen;

import java.io.Writer;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

public class XMLUtilities {
    private static XMLEventWriter eventWriter;
    private static XMLEventFactory eventFactory;

    public static void startGeneration(Writer writer) throws XMLStreamException {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        eventWriter = outputFactory.createXMLEventWriter(writer);
        eventFactory = XMLEventFactory.newInstance();
        eventWriter.add(eventFactory.createStartDocument());
    }

    public static void stopGeneration() throws XMLStreamException {
        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
    }

    public static void generateElement(String tag, String value) throws XMLStreamException {
        eventWriter.add(eventFactory.createStartElement("", "", tag));
        eventWriter.add(eventFactory.createCharacters(value));
        eventWriter.add(eventFactory.createEndElement("", "", tag));
    }

    public static void generateStartTag(String tag) throws XMLStreamException {
        eventWriter.add(eventFactory.createStartElement("", "", tag));
    }

    public static void generateEndTag(String tag) throws XMLStreamException {
        eventWriter.add(eventFactory.createEndElement("", "", tag));
    }

    public static void generateCDataElement(String name, String cdataText) throws XMLStreamException {
        eventWriter.add(eventFactory.createStartElement("", "", name));
        eventWriter.add(eventFactory.createCData(cdataText));
        eventWriter.add(eventFactory.createEndElement("", "", name));
    }
}