package dev.alpari.fujitsutrialtask.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WeatherDataManagerTest {

    @Test
    public void testParseTimestamp() {

        WeatherDataManager weatherDataManager = new WeatherDataManager();

        String expected = "2023-03-13 22:09:57 PM";
        String result = weatherDataManager.parseTimestamp("1678738197");

        assertEquals(expected, result);
    }

    @Test
    public void testReadXmlWeatherDataFromLink() {

        WeatherDataManager weatherDataManager = new WeatherDataManager();

        String url = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
        String outputFileName = "test-weather-output.xml";

        // If test is successful, a new .xml file with weather data will be created in 'database' package.
        weatherDataManager.readXmlFromLink(url, outputFileName);
    }

    @Test
    public void testGetWeatherDataForSpecifiedLocation() {

        // First read the weather data from URl to create a file to read from.
        WeatherDataManager weatherDataManager = new WeatherDataManager();

        String url = "https://www.ilmateenistus.ee/ilma_andmed/xml/observations.php";
        String outputFileName = "test-weather-output.xml";

        weatherDataManager.readXmlFromLink(url, outputFileName);

        // Now read save .xml data, in this test do not save data to database.
        weatherDataManager.readXmlFromFile("src/main/java/dev/alpari/fujitsutrialtask/database/" + outputFileName, false);

        // As a result, weather data for specified stations (i.e. "Tallinn-Harku", "Tartu-Tõravere" and "Pärnu") is received.
        assertNotNull(weatherDataManager.getLatestWeatherDataForLocation("Tallinn"));
        assertNotNull(weatherDataManager.getLatestWeatherDataForLocation("Tartu"));
        assertNotNull(weatherDataManager.getLatestWeatherDataForLocation("Pärnu"));

        // Print data to see contents of weather data and compare with actual .xml source file.
        System.out.println("Weather in Tallinn");
        System.out.println(weatherDataManager.getLatestWeatherDataForLocation("Tallinn").toJson());
    }
}
