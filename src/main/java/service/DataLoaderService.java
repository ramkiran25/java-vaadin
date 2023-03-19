package service;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;


public interface DataLoaderService {
  String loadSeedData() throws ParserConfigurationException, SAXException, IOException;
}
