package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import model.*;

public class SaxXmlDefaultHandler extends DefaultHandler {

    private final EnergyReport energyReport = new EnergyReport();
    private final AccountTimeSeries accountTimeSeries = new AccountTimeSeries();
    private final List<HourConsumption> hourConsumptions = new ArrayList<>();
    
    private String currentTag = null;
    private String currentTs = null;

    // Map to handle tag processing logic without if-else
    private final Map<String, Consumer<String>> tagActions = Map.of(
        "DocumentIdentification", energyReport::setDocumentIdentification,
        "DocumentDateTime", energyReport::setDocumentDateTime,
        "MeasurementUnit", accountTimeSeries::setMeasurementUnit,
        "AccountingPoint", accountTimeSeries::setAccountingPoint
    );

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentTag = qName;
        if ("HourConsumption".equals(qName)) {
            currentTs = attributes.getValue("ts");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String data = new String(ch, start, length).trim();
        if (data.isEmpty()) return;

        // Process simple tags from the map
        if (tagActions.containsKey(currentTag)) {
            tagActions.get(currentTag).accept(data);
        } 
        // Handle complex object creation for HourConsumption
        else if ("HourConsumption".equals(currentTag)) {
            HourConsumption hc = new HourConsumption(data, currentTs);
            hourConsumptions.add(hc);
            
            ConsumptionHistory history = new ConsumptionHistory(new ArrayList<>(hourConsumptions));
            accountTimeSeries.setConsumptionHistory(history);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        // Link the time series to the report once structure is built
        energyReport.setAccountTimeSeries(accountTimeSeries);
        currentTag = null;
    }

    public EnergyReport getEnergyReport() {
        return energyReport;
    }
}