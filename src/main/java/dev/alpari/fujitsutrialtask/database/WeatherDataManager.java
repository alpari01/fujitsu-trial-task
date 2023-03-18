package dev.alpari.fujitsutrialtask.database;

import dev.alpari.fujitsutrialtask.HttpRequester;
import dev.alpari.fujitsutrialtask.model.WeatherData;
import lombok.Getter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class WeatherDataManager {

    private final Set<String> stationsToCheck = new HashSet<>(Set.of("TALLINN-HARKU", "TARTU-TÕRAVERE", "PÄRNU"));
    private final Map<String, WeatherData> latestWeatherData = new HashMap<>();

    private final HttpRequester httpRequester = new HttpRequester();

    /*
     Store here information about which location's weather data is available in which station.

     e.g. Tartu: Tartu-Tõravere station, Tõravere: Tartu-Tõravere station.
     */
    private final Map<String, String> locations = new HashMap<>();


    public WeatherDataManager() {
        setLocationToWeatherStation("TALLINN", "TALLINN-HARKU");
        setLocationToWeatherStation("TARTU", "TARTU-TÕRAVERE");
        setLocationToWeatherStation("PÄRNU", "PÄRNU");
    }

    /**
     * Start scheduler to update weather every fixed interval.
     *
     * @param saveDataToDatabase store read weather data to database or not
     */
    public void beginScheduledWeatherUpdate(boolean saveDataToDatabase) {
        // Create a new scheduled cronjob task.
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        Runnable task = () -> updateWeatherData(saveDataToDatabase);
//        executorService.scheduleAtFixedRate(task, getSchedulerInitialDelay(), 1, TimeUnit.HOURS);
        executorService.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * Calculate an offset (initial delay) for scheduler to start to hit the update interval of HH:15:00.
     *
     * @return initial delay until next interval start.
     */
    public long getSchedulerInitialDelay() {

        long currentTimeMillis = System.currentTimeMillis();
        long hourMillis = TimeUnit.HOURS.toMillis(1);
        long fifteenMinutesInMillis = TimeUnit.MINUTES.toMillis(15);

        long untilNextHour = currentTimeMillis - (currentTimeMillis % hourMillis) + hourMillis;
        return untilNextHour + fifteenMinutesInMillis - currentTimeMillis;
    }

    /**
     * Update weather data.
     */
    public void updateWeatherData(boolean saveDataToDatabase) {
        System.out.println("Weather updated");
        readXmlFromLink("https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php", "weather-data.xml");
        readXmlFromFile("src/main/java/dev/alpari/fujitsutrialtask/database/weather-data.xml", saveDataToDatabase);
    }

    /**
     * Read data from https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php.
     *
     * Save this data as .xml file.
     *
     * @param urlString url to read data from.
     */
    public void readXmlFromLink(String urlString, String outputFileName) {

        try {

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(connection.getInputStream());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            FileOutputStream fos = new FileOutputStream("src/main/java/dev/alpari/fujitsutrialtask/database/" + outputFileName);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            StreamResult result = new StreamResult(osw);
            transformer.transform(new DOMSource(doc), result);

        } catch (IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse string value of timestamp to human-readable format.
     *
     * 1678738197 -> 2023-03-13 22:09:57 PM.
     * @param timestampString string value of timestamp.
     * @return Human-readable timestamp.
     */
    public String parseTimestamp(String timestampString) {

        long timestamp = Long.parseLong(timestampString) * 1000L;  // Convert to milliseconds.
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        return formatter.format(date);
    }

    /**
     * Read data from .xml file.
     *
     * @param path path to .xml file.
     */
    public void readXmlFromFile(String path, boolean saveDataToDatabase) {

        try {

            File xmlInputFile = new File(path);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(xmlInputFile);
            NodeList nodeList = document.getElementsByTagName("station");

            String observationTimestamp = parseTimestamp(document.getDocumentElement().getAttribute("timestamp"));

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String stationName = element.getElementsByTagName("name").item(0).getTextContent().toUpperCase();

                    if (stationsToCheck.contains(stationName)) {
                        // Read data for specified stations only.

                        String stationWmoCode = element.getElementsByTagName("wmocode").item(0).getTextContent();
                        float airTemperature = Float.parseFloat(element.getElementsByTagName("airtemperature")
                                .item(0).getTextContent());
                        float windSpeed = Float.parseFloat(element.getElementsByTagName("windspeed").item(0).getTextContent());
                        String weatherPhenomenon = element.getElementsByTagName("phenomenon").item(0).getTextContent();

                        // Build WeatherData object.
                        WeatherData weatherData = WeatherData.builder()
                                .stationName(stationName)
                                .stationWmoCode(stationWmoCode)
                                .airTemperature(airTemperature)
                                .windSpeed(windSpeed)
                                .weatherPhenomenon(weatherPhenomenon)
                                .observationTimestamp(observationTimestamp)
                                .build();

                        // Save it to the database.
                        if (saveDataToDatabase) httpRequester.makeHttpPostRequest("http://localhost:8080/api/weatherdata/add", weatherData.toJson());

                        // Update the latest weather data.
                        latestWeatherData.put(stationName, weatherData);
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get latest weather data for specified location.
     *
     * @param locationName name of the location to get data for.
     * @return WeatherData object.
     */
    public WeatherData getLatestWeatherDataForLocation(String locationName) {

        locationName = locationName.toUpperCase();
        String stationName = locations.get(locationName);

        if (!latestWeatherData.containsKey(stationName))
            throw new NoSuchElementException("Could not find data for location: " + locationName);

        return latestWeatherData.get(stationName);
    }

    /**
     * Set location to weather station.
     *
     * setLocationToWeatherStation("Tartu", "Tartu-Tõravere") -> Tartu city is now bonded to weather station "Tartu-Tõravere".
     *
     * @param stationName name of the weather station
     * @param location name of the location: city, town, village, etc.
     */
    public void setLocationToWeatherStation(String location, String stationName) {

        if (locations.containsKey(location.toUpperCase())) throw new IllegalArgumentException("This location already exists.");
        else locations.put(location.toUpperCase(), stationName.toUpperCase());
    }
}
