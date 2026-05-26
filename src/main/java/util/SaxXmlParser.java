package util;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import annotation.TrackExecutionTime;
import model.EnergyReport;

/**
 * FIX: @TrackExecutionTime was on a static method — Spring AOP proxies cannot intercept
 * static methods, so the annotation was silently ignored. Converted to an instance method
 * and made the class a Spring @Component so AOP can wrap it properly.
 *
 * FIX: Handler is now created as an instance and its instance getter is used, eliminating
 * the static state coupling from the old SaxXmlDefaultHandler.getEnergyReport().
 */
@org.springframework.stereotype.Component
public class SaxXmlParser {

  @TrackExecutionTime
  public EnergyReport parse(String xmlString)
      throws ParserConfigurationException, SAXException, IOException {
    SaxXmlDefaultHandler datahandler = new SaxXmlDefaultHandler();
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(new InputSource(new StringReader(xmlString)), datahandler);
    } catch (Exception e) {
      // Rethrow so the caller (DataLoaderServiceImpl) can handle/report failure correctly.
      throw new RuntimeException("XML parsing failed: " + e.getMessage(), e);
    }
    return datahandler.getEnergyReport();
  }
}
