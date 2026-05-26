package util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import model.EnergyReport;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SaxXmlParser.class) // Focus test on the parser component
public class SaxXmlParserTest {

    @Autowired
    private SaxXmlParser saxXmlParser;

    @Test
    public void shouldParseXmlCorrectly() throws Exception {
        // Minimal sample XML to verify the parser logic
        String xmlString = 
            "<root>" +
            "  <DocumentIdentification>12345</DocumentIdentification>" +
            "  <DocumentDateTime>2026-05-26T07:00:00</DocumentDateTime>" +
            "  <AccountingPoint>98765</AccountingPoint>" +
            "  <MeasurementUnit>KWH</MeasurementUnit>" +
            "  <HourConsumption ts='2026-05-26T07:00:00'>0.50</HourConsumption>" +
            "</root>";

        // Act
        EnergyReport energyReport = saxXmlParser.parse(xmlString);

        // Assert
        assertNotNull("EnergyReport should not be null", energyReport);
        assertEquals("12345", energyReport.getDocumentIdentification());
        assertEquals("2026-05-26T07:00:00", energyReport.getDocumentDateTime());
        
        assertNotNull(energyReport.getAccountTimeSeries());
        assertEquals("98765", energyReport.getAccountTimeSeries().getAccountingPoint());
        
        // Verify list was populated
        assertEquals(1, energyReport.getAccountTimeSeries().getConsumptionHistory().getHourConsumption().size());
        assertEquals("0.50", energyReport.getAccountTimeSeries().getConsumptionHistory().getHourConsumption().get(0).getContent());
    }
}