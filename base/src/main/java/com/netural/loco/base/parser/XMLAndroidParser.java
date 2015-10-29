package com.netural.loco.base.parser;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLAndroidParser {

    public static HashMap<String, String> parseFile(String path) throws SAXException, IOException, ParserConfigurationException {

        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();

        FileReader reader = new FileReader(path);
        InputSource inputSource = new InputSource(reader);
        StringContentHandler stringContentHandler = new StringContentHandler();
        xmlReader.setContentHandler(stringContentHandler);

        xmlReader.parse(inputSource);

        return stringContentHandler.getContent();
    }
}
